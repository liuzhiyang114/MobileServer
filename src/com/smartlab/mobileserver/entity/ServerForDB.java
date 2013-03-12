package com.smartlab.mobileserver.entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ServerForDB {
	
	String _dataServerAdress;
	int _dport;
	
	public boolean isConnected=false;
	Socket databaseServerSocket=null;
	
	public ServerForDB(String dataServerAdress, String dport){
		_dataServerAdress=dataServerAdress;
		_dport=Integer.parseInt(dport);
	}
	
	public boolean ConnectToDBServer(){
		try {
			InetAddress address = InetAddress.getByName(_dataServerAdress);
			databaseServerSocket = new Socket(address, _dport);
			isConnected=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			databaseServerSocket=null;
			isConnected=false;
		}
		return isConnected;
	}

	public synchronized boolean Send(String message){
		boolean res=true;
		OutputStream out=null;
		try {
			out=databaseServerSocket.getOutputStream();
			out.write((message+"</CFX>").getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res=false;
			isConnected=false;
		}
		return res;
	}
	
	public synchronized String Read(){
		String res="";
		byte[] b=new byte[1024];
		int n=-1;
		InputStream in =null;
		
		try{
			in=databaseServerSocket.getInputStream();
			while((n=in.read(b))>0){
				res=res+new String(b,0,n);
				if (res.indexOf("</CFX>") != -1) {
                    break;
                }
			}
			res=res.split("</CFX>")[0];
		}catch(Exception e){
			e.printStackTrace();
			res="";
			isConnected=false;
		}	
		return res;
	}
}
