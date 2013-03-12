/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;
import java.io.*;
/**
 *
 * @author Administrator
 */
public class TestReceive {
    public static void main(String args[]){
        try{
            FileOutputStream fos=new FileOutputStream("receive.jpg");
            ReceiveLittle receiveLittle=new ReceiveLittle("localhost",10000,12000);
            final int littleLength=1024*1024;
            byte b[]=new byte[littleLength];
            while(true){
                int length=receiveLittle.receiveAll(b);
                if(length==3){
                    String end=new String(b,0,3);
                    if(end.equals("end")){
                        fos.close();
                    }
                }
                fos.write(b, 0, length);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
