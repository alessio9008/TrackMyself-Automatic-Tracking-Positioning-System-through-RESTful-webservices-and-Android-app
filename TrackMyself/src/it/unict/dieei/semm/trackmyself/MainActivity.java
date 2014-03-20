package it.unict.dieei.semm.trackmyself;

import it.unict.dieei.semm.trackmyself.db.DataBaseAdapter;
import it.unict.dieei.semm.trackmyself.services.TrackerService;
import it.unict.dieei.semm.trackmyself.util.RequestToServer;
import it.unict.dieei.semm.trackmyself.util.Utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		Log.e("MAIN_ACTIVITY","CREATE");
		
		inizializzaProprieta();
		String username = Utils.getStringPreference("username", this);
		if (!username.equalsIgnoreCase("NOPREF")) {
			loginOk();
		}
		

	}

	private void loginOk() {
		// Verifichiamo se siamo online...
		if (Utils.isOnline(this)) {
			goToMapActivity();
		} else
			Utils.showDialog("Login fallito", "Connessione a Internet assente", true,
					false,this);
		// In ogni caso, facciamo partire comunque il servizio per la raccolta
		// dei dati: non appena l'utente andra' online, i dati raccolti
		// temporaneamente verranno spediti
		startTrackerService();

	}

	private void startTrackerService() {
		// use this to start and trigger a service
		Intent i = new Intent(this, TrackerService.class);
		// potentially add data to the intent
		startService(i);

	}

	private void inizializzaProprieta() {
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
	}

	public void checkCredentials(View view) {
		String username = ((TextView) findViewById(R.id.usernameField))
				.getText().toString();
		String password = ((TextView) findViewById(R.id.passwordField))
				.getText().toString();

		// Controlla se i campi non sono vuoti:
		if (username.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
			Utils.showDialog("Login", "Alcuni campi sono rimasti vuoti. Riempili.",
					true, false,this);

		} else {
			// WebServer Request URL
			Resources res = getResources();
			String serverURL = res.getString(R.string.webservice_user);
			serverURL += username + "/" + password;
			// Use AsyncTask execute Method To Prevent ANR Problem
			if (!Utils.isOnline(context)) {
				Utils.showDialog("Login fallito", "Connessione a Internet assente",
						true, false,this);
			} else new LongOperation(username).execute(serverURL);
		}
	}

	private class LongOperation extends AsyncTask<String, Void, Void> {

		// Required initialization
		private String response;
		private String Content = "";
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

		private String username;

		public LongOperation(String username) {
			super();
			this.username = username;
		}

		protected void onPreExecute() {
			// Start Progress Dialog (Message)
			Dialog.setMessage("Attendere...");
			Dialog.show();
		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {
			try {
					response = RequestToServer.sendGet(urls[0]);
				} catch (Exception e) {
					response="errore";
					Log.e("ERRORE (doInBackground)",
							e.getMessage() + "\n" + e.getStackTrace());
				}
			return null;
		}

		protected void onPostExecute(Void unused) {
			// Close progress dialog
			Dialog.dismiss();
			Log.i("Risultato Login", response);
			if (response != null) {
				if (response.equalsIgnoreCase("yes")) {
					Utils.setPreference("username", username, context);
					loginOk();
				} else if (response.equalsIgnoreCase("errore")){
					Utils.showDialog("Problemi con il server",
							"Riprova più tardi.", true,
							false,context);
				}else {
					Utils.showDialog("Login fallito",
							"Controlla le credenziali o registrati.", true,
							false,context);
				}
			}
		}

	}

	private void goToMapActivity() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);

	}
	
	public void goToSignUpActivity(View v){
		if (Utils.isOnline(this)) {
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
		}
		else{
			Utils.showDialog("Connettività assente", "Per poter registrarti, occorre essere connessi a Internet", true,
					false,this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.e("MAIN_ACTIVITY","STOP");
		//finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("MAIN_ACTIVITY","DESTROY");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("MAIN_ACTIVITY","PAUSE");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("MAIN_ACTIVITY","RESUME");
	}


	@Override
	protected void onStart() {
		super.onStart();
		Log.e("MAIN_ACTIVITY","START");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("MAIN_ACTIVITY","RESTART");
	}
}
