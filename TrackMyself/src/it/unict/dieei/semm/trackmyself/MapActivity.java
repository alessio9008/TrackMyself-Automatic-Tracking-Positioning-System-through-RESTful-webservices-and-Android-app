package it.unict.dieei.semm.trackmyself;

import it.unict.dieei.semm.trackmyself.db.DataBaseAdapter;
import it.unict.dieei.semm.trackmyself.dialogs.SelectTimeRangeDialogFragment;
import it.unict.dieei.semm.trackmyself.services.TrackerService;
import it.unict.dieei.semm.trackmyself.util.Favorite;
import it.unict.dieei.semm.trackmyself.util.GMapV2GetRouteDirection;
import it.unict.dieei.semm.trackmyself.util.RequestToServer;
import it.unict.dieei.semm.trackmyself.util.TimeStampPoint;
import it.unict.dieei.semm.trackmyself.util.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author VIJAYAKUMAR M This class for display route current location to hotel
 *         location on google map V2
 */
public class MapActivity extends FragmentActivity {

	private List<Overlay> mapOverlays;
	private LocationManager locManager;
	private Drawable drawable;
	private LinkedList<Document> documents;
	private GMapV2GetRouteDirection v2GetRouteDirection;
	private LinkedList<TimeStampPoint> positions;
	private GoogleMap mGoogleMap;
	private boolean refreshInterval = false;
	private long valuelastime = 1;
	private MarkerOptions markerOptions;
	private Location location;
	private Context context;
	private int map_mode = 0;
	private int routecolor;
	private String routemode;
	private SelectTimeRangeDialogFragment diag;
	private boolean lastestPoints = true;
	private long fromTime = 0, toTime = 0;
	private int zoom;
	private boolean favoriteReturn = false;
	private int contatoreMarker=1;
	
	private static final int FAVORITE_RESULT_CODE = 1;

	private BroadcastReceiver refreshMapReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("refreshMapReceiver", "Ho ricevuto una richiesta di refresh");
			if (lastestPoints)
				refreshMap();
		}
	};

	private BroadcastReceiver checkGPSReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context con, Intent intent) {
			Log.i("checkGPSReceiver", "Ho ricevuto un messaggio");
			Utils.showDialog("GPS e connessione assenti", "Abilita il sistema di posizionamento", true, false, context);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		LocalBroadcastManager.getInstance(this).registerReceiver(checkGPSReceiver, new IntentFilter("GPSBroadcast"));
		
		setToTime(System.currentTimeMillis());
		try {
			valuelastime = (Long.parseLong(Utils.getStringPreference("ore_ultime_attivita", this)) * 1000);
		} catch (NumberFormatException e) {
			valuelastime = 14400000;
		}
		setFromTime(getToTime() - valuelastime);

		diag = new SelectTimeRangeDialogFragment();
		context = this;

		Log.e("MAP_ACTIVITY", "CREATE");
		documents = new LinkedList<Document>();
		positions = new LinkedList<TimeStampPoint>();

		v2GetRouteDirection = new GMapV2GetRouteDirection();
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mGoogleMap = supportMapFragment.getMap();

		// Enabling MyLocation in Google Map
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
		mGoogleMap.setTrafficEnabled(true);
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		try {
			map_mode = Integer.parseInt(Utils.getStringPreference("map_mode", this));
		} catch (NumberFormatException e) {
			map_mode = 4;
		}
		mGoogleMap.setMapType(map_mode);
		markerOptions = new MarkerOptions();
		
		clearMap();
		GetRouteTask getRoute = new GetRouteTask();
		getRoute.execute();
	}

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		private long fromTime;
		private long toTime;
		String response = "";

		public GetRouteTask(long fromTime, long toTime) {
			super();
			if (fromTime <= toTime) {
				this.fromTime = fromTime;
				this.toTime = toTime;
			} else {
				this.fromTime = toTime;
				this.toTime = fromTime;
			}

		}

		public GetRouteTask() {
			super();
			this.toTime = System.currentTimeMillis();
			try {
				valuelastime = (Long.parseLong(Utils.getStringPreference("ore_ultime_attivita", context)) * 1000);
			} catch (NumberFormatException e) {
				valuelastime = 14400000;
			}
			this.fromTime = toTime - valuelastime;
		}

		private void getPoints() throws Exception {
			Resources res = getResources();
			String serverURL = res.getString(R.string.webservice_pointer);
			String username = Utils.getStringPreference("username", context);

			String url = serverURL + username + "/" + fromTime + "/" + toTime;
			Log.e("URL GET", url);
			String response = RequestToServer.sendGet(url);
			Log.e("STAMPA STRINGA RISPOSTA", response);
			positions.addAll(Utils.extractPoints(response));
		}

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MapActivity.this);
			Dialog.setMessage("Caricamento del percorso...");
			Dialog.show();
			positions.clear();
			documents.clear();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				getPoints();

				// Get All Route values
				if (positions.size() >= 2) {
					if (!Utils.getStringPreference("modalita_percorsi", context).equals("NOPREF")) {
						routemode = Utils.getStringPreference("modalita_percorsi", context);

						int step = (int) Math.round(((double) positions.size()) / 30);

						if (step < 1)
							step = 1;

						for (int counter = step; counter < positions.size(); counter += step) {
								TimeStampPoint fromPosition = positions.get(counter - step);
								TimeStampPoint toPosition = positions.get(counter);

								Document document = v2GetRouteDirection.getDocument(fromPosition.getLatLng(), toPosition.getLatLng(), routemode);
								documents.add(document);
						}
					}
					response = "Success";
				} else
					response = "PochiPunti";
			} catch (java.net.SocketTimeoutException e) {
				response = "Error";
				Log.e("MAP_ACTIVITY: eccezione nell'invio del get per la ricezione degli ultimi punti (TIMEOUT)", e.getMessage() + e.getStackTrace().toString());
			} catch (Exception e) {
				response = "Error";
				Log.e("MAP_ACTIVITY: eccezione nell'invio del get per la ricezione degli ultimi punti", e.getMessage() + e.getStackTrace().toString());
			}
			return response;

		}

		@Override
		protected void onPostExecute(String result) {
			if (response.equalsIgnoreCase("Success")) {
				ArrayList<LatLng> directionPoint = new ArrayList<LatLng>();
				if (!Utils.getStringPreference("modalita_percorsi", context).equals("NOPREF")) {
					for (Document document : documents) {
						directionPoint.addAll(v2GetRouteDirection.getDirection(document));
					}
				} else {
					for (TimeStampPoint p : positions) {
						directionPoint.add(p.getLatLng());

					}
				}
				try {
					routecolor = Integer.parseInt(Utils.getStringPreference("map_color", context));
				} catch (NumberFormatException e) {
					routecolor = -16776961;
				}
				PolylineOptions rectLine = new PolylineOptions().width(10).color(routecolor);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}
				// Adding route on the map
				mGoogleMap.addPolyline(rectLine);
				zoom = 8;
				try {
					zoom = Integer.parseInt(Utils.getStringPreference("zoom_level", context));
				} catch (NumberFormatException e) {
					zoom = 8;
				}
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions.getFirst().getLatLng(), zoom));

				
				for (TimeStampPoint point : positions) {
					GregorianCalendar cal =new GregorianCalendar();
					cal.setTimeInMillis(point.getTimestamp());
					addMarker(point.getLatLng(), String.valueOf(contatoreMarker), "Dettaglio posizione "+contatoreMarker,Utils.getFormattedDate(cal));
					contatoreMarker++;
				}
				if (lastestPoints) {
					refreshInterval = true;
					long Maxtimestamp = 0;
					for (TimeStampPoint p : positions) {
						if (Maxtimestamp < p.getTimestamp())
							Maxtimestamp = p.getTimestamp();
					}
					setFromTime(Maxtimestamp - 1);
					setToTime(System.currentTimeMillis());
				} else
					refreshInterval = false;
			} else if (response.equalsIgnoreCase("Error")) {
				Utils.showDialog("Impossibile contattare il server", "Non è stato possibile contattare il server.", true, false, context);
			} else {
				if (!refreshInterval)
					Utils.showDialog("Impossibile tracciare il percorso",
							"La finestra temporale non contiene almeno due punti. Scegli un intervallo differente.", true, false, context);
			}

			Dialog.dismiss();
		}

		private Bitmap bitmapimage(String number, int width,int height,float textsize,float x,float y, float fatt){

			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			Bitmap bmp = Bitmap.createBitmap(width, height, conf);
			Canvas canvas1 = new Canvas(bmp);

			// paint defines the text color,
			// stroke width, size
			Paint color = new Paint();
			
			color.setColor(Color.BLACK);
			switch(number.length()){
			case 2:
				x-=(3.5*fatt);
				y-=(3*fatt);
				textsize-=(3*fatt);
				break;
			case 3:
				x-=(5*fatt);
				y-=(3*fatt);
				textsize-=(6*fatt);
				break;
			}
			color.setTextSize(textsize);//35
			// modify canvas
			canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_marker), 0, 0, color);
			canvas1.drawText(number, x, y, color);
			return bmp;
		}
		
		private Bitmap customMarker(String number) {
			
			switch (getResources().getDisplayMetrics().densityDpi) {
			case DisplayMetrics.DENSITY_XHIGH:	
			    return bitmapimage(number,47,73,35,14,40,2);
			case DisplayMetrics.DENSITY_LOW:
			case DisplayMetrics.DENSITY_MEDIUM:
			case DisplayMetrics.DENSITY_HIGH:
			default:
				return bitmapimage(number,39,61,26,11,30,1.5f);
			
			
			}
			
		}

		private void addMarker(LatLng point,String number,String title, String text) {
			//add marker to Map
			markerOptions
			    .icon(BitmapDescriptorFactory.fromBitmap(customMarker(number)))
			    .anchor(0.5f, 1)
			.title(title)
		    .snippet(text);
			markerOptions.position(point);
			markerOptions.draggable(false);
			mGoogleMap.addMarker(markerOptions);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.latest_points_menu:
			setToTime(System.currentTimeMillis());
			try {
				valuelastime = (Long.parseLong(Utils.getStringPreference("ore_ultime_attivita", this)) * 1000);
			} catch (NumberFormatException e) {
				valuelastime = 14400000;
			}
			setFromTime(getToTime() - valuelastime);
			lastestPoints = true;
			clearMap();
			refreshMap();
			return true;
		case R.id.favorites_list_menu:
			showFavorites();
			return true;
		case R.id.select_range_menu:
			diag.show(this.getFragmentManager(), "SelectRange");
			return true;
		case R.id.refresh_map:

			refreshMap();
			return true;
		case R.id.action_logout:
			stopService(new Intent(this, TrackerService.class));
			Utils.setPreference("username", "NOPREF", this);
			this.finish();
			return true;
		case R.id.settings:
			showSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void showFavorites() {
		Intent intent = new Intent(this, FavoriteRoutesActivity.class);
		// startActivity(intent);
		startActivityForResult(intent, FAVORITE_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == FAVORITE_RESULT_CODE) {
			Log.i("Ritorno dall'activity", "Ritorno dai preferiti");
			favoriteReturn = (resultCode == RESULT_OK);
			if (favoriteReturn) {
				Favorite result = (Favorite) data.getSerializableExtra("resultFavorite");
				computaPercorso(result.getFromTime(), result.getToTime());
			}
		}
	}

	public void computaPercorso(long fromTime, long toTime) {
		lastestPoints = false;
		refreshInterval = false;
		setFromTime(fromTime);
		setToTime(toTime);
		if (!Utils.isOnline(this)) {
			Utils.showDialog("Attenzione", "Connessione a Internet assente. Per inviare i dati raccolti devi nuovamente accedere alla rete.", true, false, this);
		}

		else {
			clearMap();
			GetRouteTask task = new GetRouteTask(fromTime, toTime);
			task.execute();
		}
	}
	
	private void clearMap(){
		contatoreMarker = 1;
		mGoogleMap.clear();

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.e("MAP_ACTIVITY", "STOP");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("MAP_ACTIVITY", "DESTROY");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshMapReceiver);
		Log.e("MAP_ACTIVITY", "PAUSE");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(refreshMapReceiver, new IntentFilter("refreshMapBroadcast"));

		Log.e("MAP_ACTIVITY", "RESUME");
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Log.e("MAP_ACTIVITY", "RESUME_FRAGMENT");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("MAP_ACTIVITY", "START");
	}

	@Override
	protected void onRestart() {

		super.onRestart();
		int type = 4;
		try {
			type = Integer.parseInt(Utils.getStringPreference("map_mode", this));
		} catch (NumberFormatException e) {
			type = 4;
		}
		if (type != map_mode) {
			map_mode = type;
			mGoogleMap.setMapType(map_mode);
		}

		Log.e("MAP_ACTIVITY", "RESTART");

		if (!favoriteReturn && lastestPoints) {
			long value = 14400000;
			try {
				value = (Long.parseLong(Utils.getStringPreference("ore_ultime_attivita", context)) * 1000);
			} catch (NumberFormatException e) {
				value = 14400000;
			}
			int color = -16776961;
			try {
				color = Integer.parseInt(Utils.getStringPreference("map_color", context));
			} catch (NumberFormatException e) {
				color = -16776961;
			}
			int localZoom = 8;
			try {
				localZoom = Integer.parseInt(Utils.getStringPreference("zoom_level", context));
			} catch (NumberFormatException e) {
				localZoom = 8;
			}
			String mode = "NOPREF";
			mode = Utils.getStringPreference("modalita_percorsi", context);
			
			if (localZoom!=zoom || value != valuelastime || color != routecolor || (!mode.equals(routemode))) {
				valuelastime = value;
				routecolor = color;
				routemode = mode;
				zoom=localZoom;
				long totime = System.currentTimeMillis();
				setToTime(totime);
				setFromTime(totime - valuelastime);
				clearMap();
			}

			refreshMap();
		}
		favoriteReturn = false;
	}

	public void refreshMap() {
		if (this.lastestPoints)
			setToTime(System.currentTimeMillis());
		if (!Utils.isOnline(this)) {
			Utils.showDialog("Attenzione", "Connessione a Internet assente. Per inviare i dati raccolti devi nuovamente accedere alla rete.", true, false, this);
		}

		else {
			GetRouteTask getRoute = new GetRouteTask(getFromTime(), getToTime());
			getRoute.execute();
		}
	}

	@Override
	public void onBackPressed() {
		return;
	}

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}
}