package com.smartlab.mobileserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class DataBaseServerSimulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//启动socket监听
				ServerSocket server=null;
				ServerThread thread;
				Socket you=null;
				try{
					server=new ServerSocket(9001);
				}catch(IOException e){
					System.out.println("该端口已经被监听，监听异常！");
				}
				
				while(true){	
					try{
						System.out.println("等待客户呼叫。。。。");
						you=server.accept();
						System.out.println("客户的地址："+you.getInetAddress());
					}catch(IOException e){
						System.out.println("等待客户连接异常！"+e.toString());
					}
					if(you!=null){
						thread=new ServerThread(you);
						thread.start();
					}
				}

	}

}
class ServerThread extends Thread{
	Socket socket;
	OutputStream out=null;
	InputStream in=null;
	String read="";
	int timeout=2000;

	public ServerThread(Socket t) {
		// TODO Auto-generated constructor stub
		socket=t;
		try{
			out=socket.getOutputStream();
			in=socket.getInputStream();
		}catch(IOException e){
			System.out.println("获取套接字的输入输出流失败！原因："+e.toString());
		}	
	}
	
	public void run(){
		byte[] b=new byte[1024];
		int n=-1;
		while(true){
			if(in!=null){
				try{
//					socket.setSoTimeout(timeout);
					while((n=in.read(b))>0){
						read=read+new String(b,0,n);
						if (read.indexOf("</CFX>") != -1) {
	                        break;
	                    }else{
	                    	continue;
	                    }
					}
					System.out.println("读取客户端请求："+read);
					if(read==""){
						break;
					}
					read=read.split("</CFX>")[0];
					read="";
				}catch(Exception e){
					System.out.println("服务端读取客户端请求异常！原因："+e.toString());
					break;
				}
			}
			
			//处理读取的客户端请求数据
			
			
			//返回数据给客户端
			if(out!=null){
				try{
//					socket.setSoTimeout(timeout);
					byte[] temp=("type=phone&action=getGPS&params={\'ParkCode\':\'12345678\',\'RestAmount\':\'"
					+ "50"
					+ "\',\'ParkLat:\':\'"
					+ String.valueOf(38.61748)
					+ "\',\'ParkLon\':\'"
					+ String.valueOf(104.120376)
					+ "\'}"+"</CFX>").getBytes();
					
					out.write(temp);
//					socket.setSoTimeout(0);
					System.out.println("发送客户端回应："+new String(temp));
				}catch(Exception e){
					System.out.println("服务端写客户端回应异常！原因："+e.toString());
					break;
				}
			}
		}
		
		
	}
		
}
