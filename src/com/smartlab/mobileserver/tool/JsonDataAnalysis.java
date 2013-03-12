package com.smartlab.mobileserver.tool;

public class JsonDataAnalysis {
	
	
	public static String getValue(String key,String fullMsg){
		JSONObject jsonObj=new JSONObject(fullMsg);
		String value=jsonObj.optString(key);
		return value;	
	}
	
	

}
