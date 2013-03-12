package com.smartlab.mobileserver;

import java.io.IOException;

import com.smartlab.mobileserver.entity.ServerForDB;
import com.smartlab.mobileserver.entity.ServerForData;
import com.smartlab.mobileserver.entity.SyncQueue;
import com.smartlab.mobileserver.thread.HandleMobileThread;
import com.smartlab.mobileserver.thread.ReadDBServiceThread;
import com.smartlab.mobileserver.thread.SendHeartDataThread;
import com.smartlab.mobileserver.thread.TaskScannerThread;
import com.smartlab.mobileserver.tool.IniReader;

public class Mobileserver {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		String dataServerAdress="";
		String dport="";
		int poollength=2;
		String mport="";
		
		//1 从配置文件中获取database server地址，请求缓冲池的最大任务数值
		IniReader reader;
		try {
			reader = new IniReader("config.ini");
			dataServerAdress=reader.getValue("config1", "dataServerAdress");
			dport=reader.getValue("config1", "dport");
			poollength=Integer.parseInt(reader.getValue("config1", "poollength"));
			mport=reader.getValue("config1", "mport");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} 
		System.out.println("dataServerAdress="+dataServerAdress+":"+dport+",poollength="+poollength+",mport="+mport); 
		
		
		//2 创建请求缓冲池(SyncQueue类)
		SyncQueue syncQueue=new SyncQueue(poollength);
		
		//3 创建一个关于DBServer服务的对象:有连接，接收，发送数据的功能方法(ServerForDB类)，并尝试开始首次连接
		ServerForDB serverForDB=new ServerForDB(dataServerAdress,dport);
		serverForDB.ConnectToDBServer();
		
		//4 创建一个数据处理中心对象:能根据从DBServer返回的字符串做相应的处理(ServerForData类)
		ServerForData serverForData=new ServerForData();
		
		//5 新建一个读取从DBServer返回的数据线程(ReadDBServiceThread类)，读到数据后交予数据处理中心对象处理
		ReadDBServiceThread readDBServiceThread=new ReadDBServiceThread(serverForDB,serverForData);
		readDBServiceThread.start();
		
		//6 开启请求任务扫描线程（扫描请求缓冲池，并利用DBServer服务对象发送）
		TaskScannerThread taskScannerThread=new TaskScannerThread(syncQueue,serverForDB);
		taskScannerThread.start();
		
		//7 开启发送心跳线程（发送心跳数据到请求缓冲池）
		SendHeartDataThread sendHeartDataThread=new SendHeartDataThread(syncQueue,mport,dataServerAdress,dport);
		sendHeartDataThread.start();
		
		//8 开启对Mobile端线程服务线程，对每一个连接进来的UDP连接数据整理后放入到请求缓冲池
		HandleMobileThread handleMobileThread=new HandleMobileThread(syncQueue,mport,dataServerAdress,dport);
		handleMobileThread.start();
		
		
		
			

	}

}
