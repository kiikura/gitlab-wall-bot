package org.gitlab.wallbot;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.arnx.jsonic.JSON;

public class WallObserver {

	public static class NoCheckX509TrustManager implements X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	}
	
	public static class NoCheckHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private String baseUrl = "";
	
	private String apiPath = "/api/v3";
	private String note_fetch_template = "/projects/%s/notes?last_fetched_at=%d";
	
	private String privateToken;
	
	private String projectId;
	
	private long lastFetchAt;
	
	//PRIVATE-TOKEN: QVy1PB7sTxfy4pqfZM1U" "http://example.com/api/v3/projects"
	
	private void ignoreSSLCertificate() throws Exception {

		// ignore certificate verify
		TrustManager[] trustAllCerts = new NoCheckX509TrustManager[] { new NoCheckX509TrustManager() };
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// ignore hostname verify
		HttpsURLConnection.setDefaultHostnameVerifier(new NoCheckHostnameVerifier());
	}
	
	protected void fetchProjectNotes() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append(this.baseUrl);
		sb.append(this.apiPath);
		sb.append(String.format(note_fetch_template, projectId,lastFetchAt));
		
		URL url = new URL(sb.toString());
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("PRIVATE-TOKEN", this.privateToken);
	
		con.connect();
		
		int status = con.getResponseCode();
		
		
		Map<String,Object> notes = JSON.decode(con.getInputStream(),Map.class);
		
//		Reader reader = new InputStreamReader(con.getInputStream(),"UTF-8");
//		while (true) {
//			int ch = reader.read();
//			if (ch == -1) {
//				break;
//			}
//			System.out.print((char) ch);
//		}
	}
	
}