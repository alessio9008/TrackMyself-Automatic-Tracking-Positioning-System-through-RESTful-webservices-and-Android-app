package it.unict.dieei.semm.trackmyself;

import org.json.JSONObject;

import it.unict.dieei.semm.trackmyself.util.RequestToServer;
import it.unict.dieei.semm.trackmyself.util.Utils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends Activity {
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		context=this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	public void signUp(View view) {
		String username = ((TextView) findViewById(R.id.signUpUsernameField))
				.getText().toString();
		String password = ((TextView) findViewById(R.id.signUpPasswordField))
				.getText().toString();

		// Controlla se i campi non sono vuoti:
		if (username.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
			Utils.showDialog("Registrazione", "Alcuni campi sono rimasti vuoti. Riempili.",
					true, false,this);

		} else {
			// WebServer Request URL
			Resources res = getResources();
			String serverURL = res.getString(R.string.webservice_user);
			// Use AsyncTask execute Method To Prevent ANR Problem
			if (!Utils.isOnline(context)) {
				Utils.showDialog("Registrazione non riuscita", "Connessione a Internet assente.",
						true, false,this);
			} else new LongOperation(username).execute(serverURL,username,password);
		}
	}
	private class LongOperation extends AsyncTask<String, Void, Void> {

		// Required initialization
		private String response;
		private String Content = "";
		private ProgressDialog Dialog = new ProgressDialog(SignUpActivity.this);

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
					JSONObject obj = new JSONObject();
					username=urls[1];
					obj.put("username", urls[1]);
					obj.put("password", urls[2]);
					Log.e("REGISTRAZIONE: QUERY JSON",obj.toString());
					response = RequestToServer.sendPostJSON(urls[0],obj.toString());
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
			Log.i("Risultato registrazione", response);
			if (response != null) {
				if (response.equalsIgnoreCase("yes")) {
					Utils.setPreference("username", username, context);
					finish();
				} else if (response.equalsIgnoreCase("errore")) {
					Utils.showDialog("Problemi con il server",
							"Riprova più tardi.", true,
							false,context);
				} else {
					Utils.showDialog("Registrazione fallita",
							"Esiste già un utente con lo stesso nome.", true,
							false,context);
				}
			}
		}

	}


}
