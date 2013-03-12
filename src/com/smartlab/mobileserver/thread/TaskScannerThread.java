package com.smartlab.mobileserver.thread;

import com.smartlab.mobileserver.entity.ServerForDB;
import com.smartlab.mobileserver.entity.SyncQueue;
import com.smartlab.mobileserver.tool.JsonDataAnalysis;

public class TaskScannerThread extends Thread{

	SyncQueue _syncQueue;
	ServerForDB _serverForDB;

	public TaskScannerThread(SyncQueue syncQueue, ServerForDB serverForDB) {
		// TODO Auto-generated constructor stub
		_syncQueue=syncQueue;
		_serverForDB=serverForDB;
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
				if(JsonDataAnalysis.getValue("type", cmd).equals("request")){
					//向该手机端返回失败信息
					
				}
			}
		}
	}

	
}
