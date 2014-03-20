package it.unict.dieei.semm.trackmyself.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class RequestToServer {
	
	private static final int TIMEOUT=10000;
	
	public static String sendPostJSON(String url, String urlParameters)
			throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		// add request header
		con.setRequestMethod("POST");
		con.setConnectTimeout(TIMEOUT);
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if (!(responseCode == 200 || responseCode == 204)) {
			throw new Exception("Il server non ha risposto correttamente.");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}

	public static String sendGet(String url) throws Exception {
		String line;
		StringBuilder result = new StringBuilder();
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(TIMEOUT);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

	public static String sendPutJSON(String url, String urlParameters)
			throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		// add request header
		con.setRequestMethod("PUT");
		con.setConnectTimeout(TIMEOUT);
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if (!(responseCode == 200 || responseCode == 204)) {
			throw new Exception("Il server non ha risposto correttamente.");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}
	
	
	
}
