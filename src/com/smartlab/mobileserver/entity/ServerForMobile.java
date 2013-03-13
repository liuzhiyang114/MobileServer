package com.smartlab.mobileserver.entity;

import com.smartlab.mobileserver.tool.JsonDataAnalysis;

public class ServerForMobile {

	public void send(String jsonmsg) {
		// TODO Auto-generated method stub
		//解析发送地址
		String add=JsonDataAnalysis.getValue("to", jsonmsg);
		String addport=JsonDataAnalysis.getValue("tport", jsonmsg);
	}

}
