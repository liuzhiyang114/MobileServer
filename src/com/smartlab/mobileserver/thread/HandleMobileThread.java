package com.smartlab.mobileserver.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.smartlab.mobileserver.entity.SyncQueue;

public class HandleMobileThread extends Thread {

	SyncQueue _syncQueue;
	String _mport;
	String _dataServerAdress;
	String _dport;

	public HandleMobileThread(SyncQueue syncQueue, String mport,
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
		// 创建UDP服务接收端
		byte[] buffer = new byte[2048];
		try {
			DatagramSocket datagramSocket = new DatagramSocket(
					Integer.parseInt(_mport));
			DatagramPacket datagramPacket = new DatagramPacket(buffer,
					buffer.length);
			try {
				for (;;) {
					System.out.println("begin receive data....");
					datagramSocket.receive(datagramPacket);
					String receive=new String(datagramPacket.getData());
					System.out.println(datagramPacket.getAddress().getAddress()
							.toString()
							+ "=======" + receive);
					if(receive.indexOf("</CFX>") !=-1){
						
						receive=receive.split("</CFX>")[0];
						_syncQueue.put(receive);
						
					}else{
						System.out.println("接收手机端请求数据过长或缺失！");
					}
					datagramPacket.setLength(buffer.length);// Reset length to avoid shrinking buffer
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
