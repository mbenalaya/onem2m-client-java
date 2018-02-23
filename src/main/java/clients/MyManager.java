package clients;
 
import http.HttpClient;
import http.HttpSrv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import clients.MyActuator.MyHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
 
public class MyManager {
 
	private static String originator="CAE-MYMANAGER";
	private static String cseProtocol="http";
	private static String cseIp = "127.0.0.1";
	private static int csePort = 8443;
	private static String cseId = "server";
	private static String cseName = "server";
 
	private static String aeName = "mymanager";
	private static String aeProtocol="http";
	private static String aeIp = "127.0.0.1";
	private static int aePort = 1500;
	
	private static String subName="managersub";
	private static String targetSensorContainer="/gateway/gateway/mysensor/data";
	private static String targetActuatorContainer="/gateway/gateway/myactuator/data";
 
	private static String csePoa = cseProtocol+"://"+cseIp+":"+csePort;
	private static String appPoa = aeProtocol+"://"+aeIp+":"+aePort;
	

 
	public static void main(String[] args) {
		HttpSrv.start(aePort, "/", new MyHandler());
 
		JSONArray array = new JSONArray();
		array.put(appPoa);
		JSONObject obj = new JSONObject();
		obj.put("rn", aeName);
		obj.put("api", 12346);
		obj.put("rr", true);
		obj.put("poa",array);

		JSONObject resource = new JSONObject();
		resource.put("m2m:ae", obj);
		HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName, resource.toString(), 2);
 
		array = new JSONArray();
		array.put("/"+cseId+"/"+cseName+"/"+aeName);
		obj = new JSONObject();
		obj.put("nu", array);
		obj.put("rn", subName);
		obj.put("nct", 2);
		resource = new JSONObject();		
		resource.put("m2m:sub", obj);
		HttpClient.post(originator, csePoa+"/~"+targetSensorContainer, resource.toString(), 23);
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
						int sensorValue = Integer.parseInt(con);
						System.out.print("OBSERVATION: Sensor Value "+sensorValue);
 
						boolean actuatorState;
						if(sensorValue<250){
							System.out.println(" -> LOW");
							actuatorState=true;
 
						}else{
							System.out.println(" -> HIGH");
							actuatorState=false;
						}
						System.out.println("ACTION: switch actuator state to "+actuatorState+"\n");
 
						JSONObject obj = new JSONObject();
						obj = new JSONObject();
						obj.put("cnf", "application/text");
						obj.put("con", actuatorState);
						JSONObject resource = new JSONObject();
						resource.put("m2m:cin", obj);
						HttpClient.post(originator, csePoa+"/~"+targetActuatorContainer, resource.toString(), 4);
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
}