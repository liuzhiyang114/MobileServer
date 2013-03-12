package com.smartlab.mobileserver.thread;

import com.smartlab.mobileserver.entity.ServerForDB;
import com.smartlab.mobileserver.entity.ServerForData;

public class ReadDBServiceThread extends Thread {

	ServerForDB _serverForDB;
	ServerForData _serverForData;

	public ReadDBServiceThread(ServerForDB serverForDB,
			ServerForData serverForData) {
		// TODO Auto-generated constructor stub
		_serverForDB = serverForDB;
		_serverForData = serverForData;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {
			if (_serverForDB.isConnected == false) {
				boolean again = _serverForDB.ConnectToDBServer();
				if (again == false) {
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				String readMsg=_serverForDB.Read();
				System.out.println("recive from DB:"+readMsg);
				if(readMsg!=""){
					_serverForData.setData(readMsg);
				}
			}
		}
	}

}
