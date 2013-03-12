package com.smartlab.mobileserver.entity;

import com.smartlab.mobileserver.tool.JsonDataAnalysis;

public class ServerForData {

	String _readMsg;
	
	public void setData(String readMsg) {
		// TODO Auto-generated method stub
		_readMsg=readMsg;
		doWork();
	}
	
	void doWork(){
		if(_readMsg!=""||_readMsg!=null){
			
			String type=JsonDataAnalysis.getValue("type", _readMsg);
//			switch(type){
//				case "realive"://心跳包的回应数据
//					System.out.println("收到DB回应心跳数据！");
//					break;
//				case "requery"://需要经过处理转发给相应的Mobile端
//					break;
//				default:
//					break;
//			}
			if(type.equals("realive")){//心跳包的回应数据
				System.out.println("收到DB回应心跳数据！");
				
			}else if(type.equals("requery")){//需要经过处理转发给相应的Mobile端
				
			}else{
				
			}
		}
	}

}
