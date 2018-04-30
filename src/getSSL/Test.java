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
	public String postSSL(String urlStr, Map<String, String> headers, String body, Context context, boolean bIsDeviceAuth) throws Exception {
        
        String response = null;
        
        try {
            URL url = new URL(urlStr);
             
            HttpsURLConnection.setDefaultHostnameVerifier(new HostVerifier());
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");


            if(headers != null) {
                for (Map.Entry<String,String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }   
            }
            
            byte[] postDataBytes = null;
            int contentLength = 0;
            
            if(body != null) {
                postDataBytes = body.getBytes("UTF-8");
                contentLength = postDataBytes.length;
            }
            
            conn.setRequestProperty("Content-Length", String.valueOf(contentLength));

            String auth = conn.getRequestProperty("Authorization");
    
            conn.setDoOutput(true);
            conn.setReadTimeout(60 * 1000);
            
            conn.connect();
            if(postDataBytes != null) {
                conn.getOutputStream().write(postDataBytes);
            }      
            
            int responseCode = conn.getResponseCode();
            
            if (((403 == responseCode) || (401 == responseCode)) && (true == bIsDeviceAuth)) {
                if (null != context) {

                    Log.i(LOG_TAG, "Device deactivated, deleting database.");
                    context.deleteDatabase("RPOSLogs");
                    
                }
            }
            else {  
                
                if ((null != body) && !body.isEmpty()) {
                    Log.i(LOG_TAG, "post(): XML uploaded: ");
                      
                    if(body.length() > 4000) {
                        for(int i=0; i<body.length(); i+=4000) {
                            if((i+4000) < body.length()) {
                               Log.i(LOG_TAG, body.substring(i, (i+4000)));
                            }
                            else {
                               Log.i(LOG_TAG, body.substring(i, body.length()));
                            }
                        }
                    }
                    else {
                        Log.i(LOG_TAG, body);
                    }        
                }
                
                Log.i(LOG_TAG, "Response Code: " + responseCode);
                       
                if(responseCode == 200) {
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    
                    reader.close();
                    
                    response = stringBuilder.toString();
    
                }
                else {
                    if (null != conn.getErrorStream()) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = null;
                        
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line + "\n");
                        }
                        
                        reader.close();
                        
                        String errorResponse = stringBuilder.toString();
                        
                        Log.e(LOG_TAG, errorResponse);
                    }
                }
            }
            
            conn.disconnect();
            
        } catch(IOException e) {
            
            Log.d(LOG_TAG, "Error posting to cloud with error: " + e.toString());
            
            throw new RemoteSystemException(e);
        }
        
        return response;
        
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
