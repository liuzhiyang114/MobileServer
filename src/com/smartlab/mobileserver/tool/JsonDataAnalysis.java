package com.smartlab.mobileserver.tool;

public class JsonDataAnalysis {
	
	
	/*
	 * 示例数据：在网络上的字符串形式
	 * {"type":"alive","res":"1","from":"localhost","fport":"9001","to":"localhost","tport":"9000","msg":[{"cityno":"123"}]}</CFX>
	 * 用type值来表示各种业务：
	 * alive 心跳 	realive 回应心跳
	 * reqLogin 请求卡号登入	respLogin 回应该卡号所有信息
	 * reqPark  请求周边停车场信息 respPark 回应周边停车场信息
	 * reqParkD 请求某一个停车场详细信息  respParkD 回应某一停车场详细信息
	 * reqOrder 请求预定信息	respOrder 回应预订信息
	 * reqDisOrder 取消预订信息	respDisOrder 回应取消预订信息
	 * errDB 数据库服务连接错误（手机服务端发给手机）
	 */
	public static String getValue(String key,String fullMsg){
		JSONObject jsonObj=new JSONObject(fullMsg);
		String value=jsonObj.optString(key);
		return value;	
	}
	
	public static String setValue(String key,String value,String fullMsg){
		String res="";
		JSONObject jsonObj=new JSONObject(fullMsg);
		if(jsonObj.optString(key)!=""){
			jsonObj.remove(key);
			jsonObj.put(key, value);
			res=jsonObj.toString();
		}else{
			jsonObj.put(key, value);
			res=jsonObj.toString();
		}
		return res;
	}
	
	public static String remove(String key,String fullMsg){
		String res="";
		JSONObject jsonObj=new JSONObject(fullMsg);
		jsonObj.remove(key);
		res=jsonObj.toString();
		return res;
	}

}
