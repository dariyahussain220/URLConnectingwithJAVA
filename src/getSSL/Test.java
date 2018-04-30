package getSSL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class Test {

	 public static void main(String[] args) throws IOException {
			Test client = new Test();
			Map<String, String> head = new HashMap<>();
			head.put("Authorization", "Basic");
			head.put("Content-Type", "application/xml");
			client.getDetails("https://api.mms.com", head);
		}

		private void getDetails(String string, Map<String, String> head) throws IOException {

			 URL url = new URL(string);
	         HttpsURLConnection.setDefaultHostnameVerifier(new HostVerifier());
	         HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");

	         if(head != null) {
	             for (Map.Entry<String,String> header : head.entrySet()) {
	                 conn.setRequestProperty(header.getKey(), header.getValue());
	             }   
	         }
	         conn.connect();
	         System.err.println(conn.getContentType());
	         System.out.println(conn.getResponseMessage());
	         System.out.println(conn.getResponseCode());
	         if(conn.getErrorStream()!=null){
	        	 BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
	             
	             StringBuilder stringBuilder = new StringBuilder();
	             String line = null;
	             
	             while ((line = reader.readLine()) != null) {
	                 stringBuilder.append(line + "\n");
	             }
	             
	             reader.close();
	             
	             String errorResponse = stringBuilder.toString();
		         System.err.println(errorResponse);
	         }else {
	        	  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                  
                  StringBuilder stringBuilder = new StringBuilder();
                  String line = null;
                  
                  while ((line = reader.readLine()) != null) {
                      stringBuilder.append(line + "\n");
                  }
                  
                  reader.close();
                  
                  System.out.println(stringBuilder.toString());
	         }
	        
		}
		 public class HostVerifier implements HostnameVerifier {

		        @Override
		        public boolean verify(String hostname, SSLSession session) {
		            if ((hostname.startsWith("api")) && (hostname.endsWith("gfcp.io"))) {
		                return true;
		            }
		            return false;
		         }

		    }

}
