package com.smartlab.mobileserver.thread;

import com.smartlab.mobileserver.entity.SyncQueue;

public class SendHeartDataThread extends Thread {

	SyncQueue _syncQueue;
	String _mport;
	String _dataServerAdress;
	String _dport;

	public SendHeartDataThread(SyncQueue syncQueue, String mport,
			String dataServerAdress, String dport) {
		// TODO Auto-generated constructor stub
		_syncQueue = syncQueue;
		_mport = mport;
		_dataServerAdress = dataServerAdress;
		_dport = dport;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {
			String heartData="{\"type\":\"alive\",\"res\":\"1\",\"from\":\"localhost\",\"fport\":\""
							+ _mport
							+ "\",\"to\":\""
							+ _dataServerAdress
							+ "\",\"tport\":\"" + _dport + "\"" + "}";
			System.out.println("send heartData:"+heartData);
			_syncQueue.put(heartData);
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
