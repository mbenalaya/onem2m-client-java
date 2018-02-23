package clients;

import http.HttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class InitACP {
	private static String originator="Cae-admin";
	private static String cseProtocol="http";
	private static String cseIp = "127.0.0.1";
	private static int csePort = 8444;
	private static String cseId = "gateway";
	private static String cseName = "gateway";
	private static String acpName = "myacp";
	private static String[] acpOrignators = {"CAE-MYSENSOR","CAE-MYACTUATOR", "CAE-MYMANAGER"}; 	

	private static String csePoa = cseProtocol+"://"+cseIp+":"+csePort;

	public static void main(String[] args) throws Exception {

		JSONArray acor = new JSONArray();
		for(int i=0; i< acpOrignators.length;i++){
			acor.put(acpOrignators[i]);
		}
		
		JSONObject item = new JSONObject();
		item.put("acor", acor);
		item.put("acop", 63);
		
		JSONObject acr_1 = new JSONObject();
		acr_1.put("acr",item);
		
		acor = new JSONArray();
		acor.put(originator); 
		
		item = new JSONObject();
		item.put("acor", acor);
		item.put("acop", 63);
		
		JSONObject acr_2 = new JSONObject();
		acr_2.put("acr",item);
		
		JSONObject obj_1 = new JSONObject(); 
		obj_1.put("rn", acpName); 
		obj_1.put("pv", acr_1); 
		obj_1.put("pvs", acr_2); 
		
		JSONObject resource = new JSONObject();
		resource.put("m2m:acp", obj_1);
		
		HttpClient.post(originator, csePoa+"/~/"+cseId+"/"+cseName+"/", resource.toString(), 1);	
	}
}
