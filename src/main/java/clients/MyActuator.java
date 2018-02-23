package clients;
 
import http.HttpClient;
import http.HttpSrv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
 
public class MyActuator {
	
	private static boolean actuatorValue;
 
	private static String originator="CAE-MYACTUATOR";
	private static String cseProtocol="http";
	private static String cseIp = "127.0.0.1";
	private static int csePort = 8444;
	private static String cseId = "gateway";
	private static String cseName = "gateway";
	
	private static String aeName = "myactuator";
	private static String acpId = "/gateway/gateway/myacp";
	private static String cntName = "data";

	private static String aeProtocol="http";
	private static String aeIp = "127.0.0.1";
	private static int aePort = 1400;	
	private static String subName="actuatorsub";
 
	private static String csePoa = cseProtocol+"://"+cseIp+":"+csePort;
	private static String appPoa = aeProtocol+"://"+aeIp+":"+aePort;
 
 
	public static void main(String[] args) {
		HttpSrv.start(aePort, "/", new MyHandler());
		
		JSONArray array = new JSONArray();
		array.put(appPoa);
		JSONObject obj = new JSONObject();
		obj.put("rn", aeName);
		obj.put("api", 12345);
		obj.put("rr", true);
		obj.put("poa",array);
		JSONArray acpi = new JSONArray();
		acpi.put(acpId); 
		obj.put("acpi", acpi); 
		JSONObject resource = new JSONObject();
		resource.put("m2m:ae", obj);
		HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName, resource.toString(), 2);
 
        obj = new JSONObject();
        obj.put("rn", cntName);
		resource = new JSONObject();
        resource.put("m2m:cnt", obj);
		HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName+"/"+aeName, resource.toString(), 3);
 
		array = new JSONArray();
		array.put("/"+cseId+"/"+cseName+"/"+aeName);
		obj = new JSONObject();
		obj.put("nu", array);
		obj.put("rn", subName);
		obj.put("nct", 2);
		resource = new JSONObject();		
		resource.put("m2m:sub", obj);
		HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName+"/"+aeName+"/"+cntName, resource.toString(), 23);
	}
 
	static class MyHandler implements HttpHandler {
 
		public void handle(HttpExchange httpExchange)  {
			System.out.println("Event Recieved!");
 
			try{
				InputStream in = httpExchange.getRequestBody();
 
				String requestBody = "";
				int i;char c;
				while ((i = in.read()) != -1) {
					c = (char) i;
					requestBody = (String) (requestBody+c);
				}
 
				System.out.println(requestBody);
 
				JSONObject json = new JSONObject(requestBody);
				if (json.getJSONObject("m2m:sgn").has("vrq")) {
					System.out.println("Confirm subscription");
				} else {
					JSONObject rep = json.getJSONObject("m2m:sgn").getJSONObject("nev")
							.getJSONObject("rep");
					int ty = rep.getInt("ty");
					System.out.println("Resource type: "+ty);
 
					if (ty == 4) {
						String con = rep.getString("con");
						System.out.println("Actuator state = "+con);
						setActuatorValue(Boolean.parseBoolean(con));
					}
				}	
 
				String responseBudy ="";
				byte[] out = responseBudy.getBytes("UTF-8");
				httpExchange.sendResponseHeaders(200, out.length);
				OutputStream os = httpExchange.getResponseBody();
				os.write(out);
				os.close();
 
			} catch(Exception e){
				e.printStackTrace();
			}		
		}
	}
 
	public static void setActuatorValue(boolean actuatorValue) {
		MyActuator.actuatorValue = actuatorValue;
	}
}