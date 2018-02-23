package http;
 
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
 
public class HttpClient {
	
	 static DefaultHttpClient httpclient = new DefaultHttpClient();
	 
	 public static void configureSSL(String keystoreFile, String keystorePassword){
			try{
	            KeyStore keyStore  = KeyStore.getInstance(KeyStore.getDefaultType());
	            FileInputStream instream = new FileInputStream(new File(keystoreFile));
	            try {
	                keyStore.load(instream, keystorePassword.toCharArray());
	            }catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}catch (CertificateException e) {
					e.printStackTrace();
				}finally {
	                try { instream.close(); } catch (Exception ignore) {}
	            }
					
				}catch (Exception e){
					e.printStackTrace();
				} 
			 }
		 
	 
	public static HttpResponse get(String originator, String uri) {
		System.out.println("HTTP GET "+uri);
		HttpGet httpGet= new HttpGet(uri);
 
		httpGet.addHeader("X-M2M-Origin",originator);
		httpGet.addHeader("Accept","application/json");
 
		HttpResponse httpResponse = new HttpResponse();
 
		try {
			CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpGet);
			try{
				httpResponse.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
				httpResponse.setBody(EntityUtils.toString(closeableHttpResponse.getEntity()));
			}finally{
				closeableHttpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("HTTP Response "+httpResponse.getStatusCode()+"\n"+httpResponse.getBody());
		return httpResponse;	
	}
 
	public static HttpResponse post(String originator, String uri, String body, int ty) {
		System.out.println("HTTP POST "+uri+"\n"+body);
 
		HttpPost httpPost = new HttpPost(uri);
 
		httpPost.addHeader("X-M2M-Origin",originator);
		httpPost.addHeader("Accept","application/json");	
		httpPost.addHeader("Content-Type","application/json;ty="+ty);
 
		HttpResponse httpResponse = new HttpResponse();
		try {
			CloseableHttpResponse closeableHttpResponse=null;
			try{
				httpPost.setEntity(new StringEntity(body));
				closeableHttpResponse = httpclient.execute(httpPost);
			httpResponse.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
				httpResponse.setBody(EntityUtils.toString(closeableHttpResponse.getEntity()));
 
			}finally{
				closeableHttpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("HTTP Response "+httpResponse.getStatusCode()+"\n"+httpResponse.getBody());
		return httpResponse ;	
	}
}