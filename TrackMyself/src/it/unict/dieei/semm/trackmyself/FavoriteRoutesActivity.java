package it.unict.dieei.semm.trackmyself;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unict.dieei.semm.trackmyself.dialogs.AddModifyFavoriteDialogFragment;
import it.unict.dieei.semm.trackmyself.dialogs.FavoriteItemOptionsDialogFragment;
import it.unict.dieei.semm.trackmyself.dialogs.SelectTimeRangeDialogFragment;
import it.unict.dieei.semm.trackmyself.services.TrackerService;
import it.unict.dieei.semm.trackmyself.util.Favorite;
import it.unict.dieei.semm.trackmyself.util.ListAdapter;
import it.unict.dieei.semm.trackmyself.util.RequestToServer;
import it.unict.dieei.semm.trackmyself.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FavoriteRoutesActivity extends Activity {

	private Context context;
	private AddModifyFavoriteDialogFragment diag;
	private List<Favorite> yourData;
	private ListAdapter favoriteAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_routes);
		context = this;
		diag = new AddModifyFavoriteDialogFragment();
		ManageFavoritesTask task = new ManageFavoritesTask();
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.favorite_routes, menu);
		return true;
	}

	public void popolaList(String jsonResponseText) {
		yourData = Utils.extractFavorites(jsonResponseText);
		initLista();
	}

	private void initLista() {
		ListView yourListView = (ListView) findViewById(R.id.listViewID);
		favoriteAdapter = new ListAdapter(this, R.layout.itemlistrow, yourData);
		yourListView.setAdapter(favoriteAdapter);
		yourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final Favorite item = (Favorite) parent.getItemAtPosition(position);
				showItemOptionsDialog(item);
			}

		});
	}

	private void showItemOptionsDialog(Favorite fav) {
		Bundle args = new Bundle();
		FavoriteItemOptionsDialogFragment diag = new FavoriteItemOptionsDialogFragment();
		args.putSerializable("favoriteKey", fav);
		diag.setArguments(args);
		diag.show(this.getFragmentManager(), "SelectRange");

	}

	private void addFavoriteToLocalList(Favorite fav) {
		yourData.add(fav);
		favoriteAdapter.notifyDataSetChanged();
	}

	private void removeFavoriteFromLocalList(Favorite fav) {
		yourData.remove(fav);
		favoriteAdapter.notifyDataSetChanged();
	}

	private void editFavoriteFromLocalList(Favorite fav) {
		for (Favorite x : yourData) {
			if (x.getFavoriteName().equalsIgnoreCase(fav.getFavoriteName())) {
				x.setFromTime(fav.getFromTime());
				x.setToTime(fav.getToTime());
			}
		}

		favoriteAdapter.notifyDataSetChanged();
	}

	private class ManageFavoritesTask extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(FavoriteRoutesActivity.this);
		private String response;
		private String username;
		private String mode;
		private String jsonRequest;
		private Favorite fav;

		public ManageFavoritesTask(String mode, String text, Favorite fav) {
			this(mode, fav);
			this.jsonRequest = text;

		}

		public ManageFavoritesTask(String mode, Favorite fav) {
			this.mode = mode;
			this.fav = fav;
		}

		public ManageFavoritesTask() {
			this.mode = "GET";
		}

		@Override
		protected Void doInBackground(String... params) {
			response = "Errore";
			username = Utils.getStringPreference("username", context);
			Resources res = getResources();
			String serverURL = res.getString(R.string.webservice_favorite);
			if (params.length > 0)
				serverURL += params[0] + "/";
			if (Utils.isOnline(context)) {
				if (mode.equalsIgnoreCase("GET"))
					doInBackgroundGet(serverURL);
				else if (mode.equalsIgnoreCase("POST"))
					doInBackgroundPost(serverURL);
				else if (mode.equalsIgnoreCase("PUT"))
					doInBackgroundPut(serverURL);
				else if (mode.equalsIgnoreCase("DELETE"))
					doInBackgroundDelete(serverURL);
			} else {
				response = "Offline";
			}
			return null;
		}

		private Void doInBackgroundGet(String serverURL) {
			try {
				response = RequestToServer.sendGet(serverURL + username);
			} catch (Exception e) {
				response = "Errore";
				Log.e("Errore recupero lista preferiti", e.getMessage() + e.getStackTrace());
			}
			return null;
		}

		private Void doInBackgroundPost(String serverURL) {
			try {
				response = RequestToServer.sendPostJSON(serverURL, jsonRequest);
			} catch (Exception e) {
				response = "Errore";
				Log.e("Errore creazione nuovo preferito", e.getMessage() + e.getStackTrace());
			}
			return null;
		}

		private Void doInBackgroundPut(String serverURL) {
			try {
				response = RequestToServer.sendPutJSON(serverURL + username, jsonRequest);
			} catch (Exception e) {
				response = "Errore";
				Log.e("Errore aggiornamento preferito", e.getMessage() + e.getStackTrace());
			}
			return null;
		}

		private Void doInBackgroundDelete(String serverURL) {
			try {
				response = RequestToServer.sendGet(serverURL + username);
			} catch (Exception e) {
				response = "Errore";
				Log.e("Errore rimozione preferito", e.getMessage() + e.getStackTrace());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mode.equalsIgnoreCase("GET"))
				onPostExecuteGet(result);
			else if (mode.equalsIgnoreCase("POST"))
				onPostExecutePost(result);
			else if (mode.equalsIgnoreCase("PUT"))
				onPostExecutePut(result);
			else if (mode.equalsIgnoreCase("DELETE"))
				onPostExecuteDelete(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Dialog.setMessage("Attendere...");
			Dialog.show();
		}

		private void onPostExecuteGet(Void result) {
			if (response.equals("Errore")) {
				Dialog.dismiss();
				Utils.showDialog("Impossibile contattare il server", "Non è possibile recuperare la lista dei preferiti adesso. Riprova più tardi.", true,
						false, context, finishDialog(), finishDialog());
			} else if (response.equalsIgnoreCase("offline")) {
				Dialog.dismiss();
				Utils.showDialog("Connettività assente", "Riconnettiti alla rete e riprova.", true, false, context, finishDialog(), finishDialog());
			} else {
				popolaList(response);
				Dialog.dismiss();
			}
		}

		private void onPostExecutePost(Void result) {
			if (response.equals("Errore")) {
				Dialog.dismiss();
				Utils.showDialog("Impossibile contattare il server", "Non è possibile aggiungere l'elemento alla lista dei preferiti. Riprova più tardi.",
						true, false, context, finishDialog(), finishDialog());
			} else if (response.equalsIgnoreCase("offline")) {
				Dialog.dismiss();
				Utils.showDialog("Connettività assente", "Riconnettiti alla rete e riprova.", true, false, context, finishDialog(), finishDialog());
			} else {
				if (response.equalsIgnoreCase("yes"))
					addFavoriteToLocalList(fav);
				else
					Utils.showDialog("Elemento esistente", "Un elemento con lo stesso nome è già presente. Digitane un altro.", true, false, context);
				Dialog.dismiss();
			}
		}

		private void onPostExecutePut(Void result) {
			if (response.equals("Errore")) {
				Dialog.dismiss();
				Utils.showDialog("Impossibile contattare il server", "Non è possibile modificare l'elemento alla lista dei preferiti. Riprova più tardi.",
						true, false, context, finishDialog(), finishDialog());
			} else if (response.equalsIgnoreCase("offline")) {
				Dialog.dismiss();
				Utils.showDialog("Connettività assente", "Riconnettiti alla rete e riprova.", true, false, context, finishDialog(), finishDialog());
			} else {
				editFavoriteFromLocalList(fav);
				Dialog.dismiss();
			}
		}

		private void onPostExecuteDelete(Void result) {
			if (response.equals("Errore")) {
				Dialog.dismiss();
				Utils.showDialog("Impossibile contattare il server", "Non è possibile rimuovere l'elemento alla lista dei preferiti. Riprova più tardi.", true,
						false, context, finishDialog(), finishDialog());
			} else if (response.equalsIgnoreCase("offline")) {
				Dialog.dismiss();
				Utils.showDialog("Connettività assente", "Riconnettiti alla rete e riprova.", true, false, context, finishDialog(), finishDialog());
			} else {
				removeFavoriteFromLocalList(fav);
				Dialog.dismiss();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_favorite_route:
			addRoute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addRoute() {
		Bundle args = new Bundle();
		args.putString("mode", "add");
		diag.setArguments(args);
		diag.show(this.getFragmentManager(), "AddRoute");
	}

	public void editRoute(Favorite fav) {
		Bundle args = new Bundle();
		args.putString("mode", "edit");
		args.putSerializable("favorite", fav);
		diag.setArguments(args);
		diag.show(this.getFragmentManager(), "EditRoute");
	}

	public void deleteRoute(final Favorite fav) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				doRemoveItem(fav);
			}
		};
		Utils.showDialog(fav.getFavoriteName(), "Desideri eliminare questo preferito?", true, true, context, listener, null);
	}

	private DialogInterface.OnClickListener finishDialog() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((Activity) context).finish();
			}
		};
	}

	public void doAddItem(Favorite fav, String jsonParams) {
		ManageFavoritesTask task = new ManageFavoritesTask("POST", jsonParams, fav);
		task.execute();
	}

	public void doEditItem(Favorite fav, String jsonParams) {
		ManageFavoritesTask task = new ManageFavoritesTask("PUT", jsonParams, fav);
		task.execute(fav.getFavoriteName());
	}

	public void doRemoveItem(Favorite fav) {
		ManageFavoritesTask task = new ManageFavoritesTask("DELETE", null, fav);
		task.execute("delete/"+fav.getFavoriteName());
	}
	public void showFavoriteRouteOnMap(Favorite fav){
		Intent returnIntent = new Intent();
		 returnIntent.putExtra("resultFavorite",fav);
		 setResult(RESULT_OK,returnIntent);     
		 finish();
	}
}
