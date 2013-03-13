package com.smartlab.mobileserver.thread;

import com.smartlab.mobileserver.entity.ServerForDB;
import com.smartlab.mobileserver.entity.ServerForMobile;
import com.smartlab.mobileserver.entity.SyncQueue;
import com.smartlab.mobileserver.tool.JsonDataAnalysis;

public class TaskScannerThread extends Thread{

	SyncQueue _syncQueue;
	ServerForDB _serverForDB;
	ServerForMobile _serverForMobile;

	public TaskScannerThread(SyncQueue syncQueue, ServerForDB serverForDB, ServerForMobile serverForMobile) {
		// TODO Auto-generated constructor stub
		_syncQueue=syncQueue;
		_serverForDB=serverForDB;
		_serverForMobile=serverForMobile;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while(true){
			String cmd=(String)_syncQueue.get();
			if(_serverForDB.isConnected!=false){
				_serverForDB.Send(cmd);
			}else{
				//没有连接DBServer成功
				System.out.println("没有连接数据库服务器成功");
				//判断cmd，如果来自于手机端，返回失败信息；如果来自本机心跳，不作处理
				String cmdType=JsonDataAnalysis.getValue("type", cmd);
				if(!cmdType.equals("alive")){
					//向该手机端返回失败信息
					String type="errDB";
					String res="0";
					String to=JsonDataAnalysis.getValue("from", cmd);
					String tport=JsonDataAnalysis.getValue("fport", cmd);
					String resmsg="{\"type\":\""+type+"\",\"res\":\""+res+"\",\"to\":\""+to+"\",\"tport\":\""+tport+"\"}";
					_serverForMobile.send(resmsg);
				}
			}
		}
	}

	
}
