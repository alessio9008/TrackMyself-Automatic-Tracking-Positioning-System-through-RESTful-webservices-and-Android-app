package it.unict.dieei.semm.trackmyself.services;

import it.unict.dieei.semm.trackmyself.R;
import it.unict.dieei.semm.trackmyself.db.DataBaseAdapter;
import it.unict.dieei.semm.trackmyself.util.Point;
import it.unict.dieei.semm.trackmyself.util.RequestToServer;
import it.unict.dieei.semm.trackmyself.util.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unict.dieei.semm.trackmyself.MapActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class TrackerService extends Service {
	private LocationManager locationManager;
	private LocationListener listenerFine;
	private Point point;
	private Timer timerTracker;
	private static Context context;
	private DataBaseAdapter dba;
	private boolean gpsDialog = false;
	private Timer timerSender;
	private long timeoutTracker;
	private boolean stopThread = false;

	public synchronized long getTimeoutTracker() {
		return timeoutTracker;
	}

	public synchronized void setTimeoutTracker(long timeoutTracker) {
		this.timeoutTracker = timeoutTracker;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		if (dba == null)
			dba = new DataBaseAdapter(this);
		stopThread = false;
		point = new Point();
		createLocationListener();

		registerLocationListener();
		if (!Utils.getStringPreference("username", this).equalsIgnoreCase("NOPREF")) {
			// ==============================================
			try {
				timeoutTracker = Integer.parseInt(Utils.getStringPreference("intervallo_acquisizione", this)) * 1000;
			} catch (NumberFormatException e) {
				timeoutTracker = 120000;
			}
			int timeoutSender = 30000;
			try {
				timeoutSender = Integer.parseInt(Utils.getStringPreference("intervallo_send", this)) * 1000;
			} catch (NumberFormatException e) {
				timeoutSender = 30000;
			}

			TimerTask tracker, sender;

			// Initialization code in onCreate or similar:
			timerSender = new Timer();

			sender = new TimerTask() {
				public void run() {
					Resources res = getResources();
					String serverURL = res.getString(R.string.webservice_pointer);
					String username = Utils.getStringPreference("username", context);
					if (Utils.isOnline(context)) {
						// controlliamo se abbiamo record passati nel db di
						// SQLite
						Cursor cur = dba.returnAllPoints();
						JSONArray jsonArr = new JSONArray();

						while (cur.moveToNext()) {
							long timestamp = cur.getLong(cur.getColumnIndex("timestampinsert"));
							double latitude = cur.getDouble(cur.getColumnIndex("latitude"));
							double longitude = cur.getDouble(cur.getColumnIndex("longitude"));
							if (latitude != 0 && longitude != 0)
								jsonArr.put(Utils.prepareRequestPoint(timestamp, username, new double[] { latitude, longitude }));
						}
						try {
							RequestToServer.sendPostJSON(serverURL, jsonArr.toString());
							Log.i("SERVICE", "INVIATO AL SERVER:" + jsonArr.toString());
							dba.deleteAll();
							Log.i("TEST MESSAGGIO", "" + Utils.getBooleanPreference("can_refresh_map", context));
							if (Utils.getBooleanPreference("can_refresh_map", context))
								refreshMap();
						} catch (Exception e) {
							Log.e("Service: eccezione nell'invio del post", e.getMessage() + e.getStackTrace().toString());
						}

					}
				}
			};

			Thread Mytracker = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!stopThread) {
						double[] p = point.getPoint();
						if (Utils.getBooleanPreference("enable_tracking", context)) {
							if (Utils.isOnline(context) || Utils.gpsON(context)) {
								setGpsDialog(false);
								Log.i("SERVICE - Thread MyTracker", "Inserimento di:" + p[0] + " " + p[1]);
								if (p[0] != 0 && p[1] != 0)
									dba.insertPoint(System.currentTimeMillis(), p[0], p[1]);
							} else {
								if (!isGpsDialog()) {
									gpsCheck();
									setGpsDialog(true);
								}
							}
						}
						try {
							if (timeoutTracker == 0)
								timeoutTracker = 10000;
							Log.d("timerTracker", "Mi sto riposando per " + timeoutTracker + " ms.");
							Thread.sleep(timeoutTracker);
						} catch (InterruptedException e) {
							Log.e("Service: timeout errato", e.getMessage() + e.getStackTrace().toString());
						}
					}
					Log.d("Thread tracker", "Sono uscito da 'Thread tracker'");
				}
			});

			Mytracker.setName("Thread tracker");
			Mytracker.start();

			if (timeoutSender != 0)
				timerSender.scheduleAtFixedRate(sender, timeoutSender, timeoutSender);
		} else {
			stopThread = true;
			this.stopSelf();

		}
		// =======================================================

	}

	private void registerLocationListener() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria fine = new Criteria();
		fine.setAccuracy(Criteria.ACCURACY_FINE);

		if (listenerFine == null)
			createLocationListener();

		locationManager.requestLocationUpdates(locationManager.getBestProvider(fine, true), 0, 0, listenerFine);
	}

	private void createLocationListener() {

		listenerFine = new LocationListener() {
			private int intervallo;
			private float sommaVelocita;
			private float velocitaMedia;

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				int intervallo=120000;
				try{
					intervallo=Integer.parseInt(Utils.getStringPreference("intervallo_acquisizione", context));
				}
				catch(NumberFormatException e){
					intervallo=120000;
				}
				if (intervallo== 0) {
					if (intervallo == 5) {
						velocitaMedia = sommaVelocita / intervallo;
						if (velocitaMedia != 0) {
							long TimeOutvalue = Math.round(210 / velocitaMedia) * 1000;
							if (TimeOutvalue >= 120000)
								setTimeoutTracker(120000);
							else
								setTimeoutTracker(TimeOutvalue);
						} else
							setTimeoutTracker(10000); // se siamo fermi, riprova
														// dopo 10 secondi
						intervallo = 0;
						sommaVelocita = 0;
					} else {
						intervallo++;
						sommaVelocita += location.getSpeed();
					}
				} else {
					intervallo = 0;
					sommaVelocita = 0;
				}
				point.setPoint(location.getLatitude(), location.getLongitude());
			}
		};
	}

	@Override
	public void onDestroy() {
		stopThread = true;
		// The service is no longer used and is being destroyed
		if (timerSender != null)
			timerSender.cancel();
	}

	public boolean isGpsDialog() {
		return gpsDialog;
	}

	public void setGpsDialog(boolean gpsDialog) {
		this.gpsDialog = gpsDialog;
	}

	private void refreshMap() {
		Intent intent = new Intent("refreshMapBroadcast");
		sendLocationBroadcast(intent);
	}

	private void gpsCheck() {
		Intent intent = new Intent("GPSBroadcast");
		sendLocationBroadcast(intent);
	}

	private void sendLocationBroadcast(Intent intent) {
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

}
