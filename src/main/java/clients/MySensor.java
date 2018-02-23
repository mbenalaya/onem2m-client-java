package clients;
 
import http.HttpClient;

import org.json.JSONArray;
import org.json.JSONObject;
 
public class MySensor {
 
	public static int sensorValue;
 
	private static String originator="CAE-MYSENSOR";
	private static String cseProtocol="http";
	private static String cseIp = "127.0.0.1";
	private static int csePort = 8444;
	private static String cseId = "gateway";
	private static String cseName = "gateway";
	
	private static String aeName = "mysensor";
	private static String acpId = "/gateway/gateway/myacp";
	private static String cntName = "data";
 
	private static String csePoa = cseProtocol+"://"+cseIp+":"+csePort;
 
	public static void main(String[] args) {
		
		JSONObject obj = new JSONObject();
		obj.put("rn", aeName);
		obj.put("api", 12345);
		obj.put("rr", false);
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
 
		while (true){
 
			obj = new JSONObject();
			obj.put("cnf", "application/text");
			obj.put("con", getSensorValue());
			resource = new JSONObject();
			resource.put("m2m:cin", obj);
			HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName+"/"+aeName+"/"+cntName, resource.toString(), 4);
 
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
 
	public static int getSensorValue(){
		sensorValue = (int)(Math.random()*500);
		System.out.println("Sensor value = "+sensorValue);
		return sensorValue;
	}
}