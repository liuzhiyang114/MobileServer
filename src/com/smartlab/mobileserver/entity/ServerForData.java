package com.smartlab.mobileserver.entity;

import com.smartlab.mobileserver.tool.JsonDataAnalysis;

public class ServerForData {

	String _readMsg;
	ServerForMobile _serverForMobile;
	
	public ServerForData(ServerForMobile serverForMobile) {
		// TODO Auto-generated constructor stub
		_serverForMobile=serverForMobile;
	}

	public void setData(String readMsg) {
		// TODO Auto-generated method stub
		_readMsg=readMsg;
		doWork();
	}
	
	void doWork(){
		if(_readMsg!=""||_readMsg!=null){
			
			String type=JsonDataAnalysis.getValue("type", _readMsg);

			if(type.equals("realive")){//心跳包的回应数据
				System.out.println("收到DB回应心跳数据！");
				
			}else if(type.equals("respLogin")){//需要经过处理转发给相应的Mobile端
				//解析字符串，获得手机端的地址，端口，使用_serverForMobile类的功能方法发送
				Dispatch();
			}else if(type.equals("respPark")){
				Dispatch();
			}else if(type.equals("respParkD")){
				Dispatch();
			}else if(type.equals("respOrder")){
				Dispatch();
			}else if(type.equals("respDisOrder")){
				Dispatch();
			}else{
				System.out.println("使用未定义的type字段！");
			}
		}
	}
	
	void Dispatch(){
		//去掉from,fport字段数据
		String newmsg=JsonDataAnalysis.remove("from", _readMsg);
		newmsg=JsonDataAnalysis.remove("fport", newmsg);
		_serverForMobile.send(newmsg);
	}
	
}
