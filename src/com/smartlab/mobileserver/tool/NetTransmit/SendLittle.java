package com.smartlab.mobileserver.tool.NetTransmit;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
/**
 *
 * @author Administrator
 */
public class SendLittle {
    //��ݰ���
    DatagramSocket socket;    
    //��ݰ����Ϣ
    String yourAddress="192.168.1.4"; 
    int yourPort=10000;
    int myPort=12000;
    /*���ڷ�������*/
    final int StartTip[]={12,0,4,8};                   //��ʼ�����(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StartAckTip[]={12,0,4,8};                //��ʼȷ�ϰ�(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StopTip[]={12,0,4,8};                    //�������(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StopAckTip[]={12,0,4,8};                 //����ȷ�ϰ�(12)    ��ʶ(4)|�������(4)|��β(4)
    final int HeadTip[]={18,0,4,8,12,14};              //����ͷ��(18)      ��ʶ(4)|�������(4)|�������(4)|��������(2)|��β(4)
    final int HeadAckTip[]={16,0,4,8,12};              //����ͷȷ�ϰ�(16)  ��ʶ(4)|�������(4)|�������(4)|��β(4)
    final int DataTip[]={1020,0,4,8,12,14,16,1016};      //������ݰ�(520) ��ʶ(4)|�������(4)|�������(4)|���(2)|����(2)|���(500)|��β(4)
    final int FinishTip[]={20,0,4,8,12,16};            //������ϰ�(20)    ��ʶ(40|�������(4)|�������(4)|С�����(4)|��β(4)
    final int ReqAgainTip[]={522,0,4,8,12,16,18,518};   //�����ط���(522)  ��ʶ(4)|�������(4)|�������(4)|С�����(4)|����(2)|���(500)|��β(4)
    /*��ʶ*/
    final String datagramHead="[]01";
    final String datagramData="[]02";
    final String datagramHeadAck="[]03";
    final String datagramReqAgain="[]04";
    final String datagramFinish="[]05";    
    final String datagramStart="[]08";
    final String datagramStartAck="[]0A";
    final String datagramStop="[]06";
    final String datagramStopAck="[]07";
    final String datagramTail="end|";
    /*��ݳ��ȵĹ涨*/
    final int dataLength=1000;
    final int groupNum=250;
    final int littleLength=1024*1024;
    //����
    JFrame frame;
    JPanel contentPane;
    JButton bun;
    public SendLittle(String addr,int mp,int yp){
        try {
            Date begin;
            Date end;
            yourAddress=new String(addr);
            yourPort=yp;
            myPort=mp;
            socket = new DatagramSocket(myPort);
            socket.setReceiveBufferSize(1024*1024*5);
        } catch (SocketException ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /*��ַ���,������*/
    public void sendAll(byte littleBuffer[],int littleLength){
        byte buffer[]=new byte[dataLength*groupNum];
        int numberRandom=(new Random()).nextInt();
        sendStart(numberRandom);
        int bigNumber=littleLength/(dataLength*groupNum)+(littleLength%(dataLength*groupNum)==0?0:1);
        for(int i=0;i<bigNumber;i++){
            //���buffer
            int length;
            if(i==bigNumber-1){
                length=littleLength-i*(dataLength*groupNum);
            }else{
                length=dataLength*groupNum;
            }
            for(int j=0;j<length;j++){
                buffer[j]=littleBuffer[j+i*dataLength*groupNum];
            }
            sendGroup(numberRandom,buffer,length);
        }
        sendOver(numberRandom);
    }
    /*���Ϳ�ʼ�����*/
    public void sendStart(int numberRandom){
        DatagramPacket sendPacket=null,receivePacket=null;
        byte sendBuffer[]=new byte[StartTip[0]];
        byte receiveBuffer[]=new byte[StartAckTip[0]];
        //��俪ʼ�����
        ByteChange.StringToByte(datagramStart, sendBuffer, StartTip[1], 4);
        ByteChange.intToByte(sendBuffer, StartTip[2], numberRandom);
        ByteChange.StringToByte(datagramTail, sendBuffer, StartTip[3], 4);
        //���ó�ʱ1ms����ʼ����ݰ�
        try{
            socket.setSoTimeout(1);
            sendPacket=new DatagramPacket(sendBuffer,sendBuffer.length,InetAddress.getByName(yourAddress),yourPort);
            receivePacket=new DatagramPacket(receiveBuffer,receiveBuffer.length);
        }catch(Exception e){}
        boolean canSend=true;
        while(true){
            try{
                if(canSend){
                    socket.send(sendPacket);
                }
                socket.receive(receivePacket);
                //�����յ���ݰ�,�������,��һ�ζ����ܷ���
                canSend=false;
                //�жϱ�ʶ
                String str=new String(receiveBuffer,StartAckTip[1],4);
                if(!str.equals(datagramStartAck)){
                    continue;
                }
                //�жϴ������
                int num=ByteChange.byteToInt(receiveBuffer, StartAckTip[2]);
                if(num!=numberRandom){
                    continue;
                }
                //������ճɹ�,�˳�ѭ��
                break;
            }catch(Exception e){
                if(e instanceof SocketTimeoutException){
                    canSend=true;
                }
            }
        }
    }
    /*����һ����ݰ�*/
    public void sendGroup(int numberRandom,byte buffer[],int length){
        assert length<=dataLength*groupNum:"Error:�������������������!";
        //��������
        int blockRandom=(new Random()).nextInt();
        //�����������
        int number=length/dataLength+(length%dataLength==0?0:1);
        //���ͷ���ͷ
        sendDatagramHead(numberRandom,blockRandom,number);
        //System.out.println("���ͷ���:"+number);
        //���ͷ�����
        /*
         * ���ͷ�ʽ��
         * 1:ȫ��������
         * 2.���ͷ�����ϰ�
         * 3:���������ط���.�����ճ�ʱ,ִ��2
         * 4:��������ط����������,���ط���Ӧ�İ�,Ȼ��ִ��2.����ִ��5
         * 5:���ͳɹ�,����
         */
        //�Ƚ�buffer��ֳ�number������,��¼ÿ��������buffer�е���ʼ�±����ݳ���
        int startTip[]=new int[number];
        int lengthTip[]=new int[number];
        boolean sendTip[]=new boolean[number];
        //һ��ʼ,ÿ����ݶ�δ����ɹ�
        for(int i=0;i<number;i++){
            sendTip[i]=false;
        }
        if(number==1){
            //���ֻ��һ������,�Ƚ�����.Ϊ���Ч��,��������
            startTip[0]=0;
            lengthTip[0]=length;
        }else{
            //���鳬��һ��
            for(int i=0;i<number;i++){
                if(i==0){
                    startTip[i]=0;
                }else{
                    //������,ֻ�����һ����ݳ��Ȳſ��ܲ�ΪdataLength
                    //����,ÿһ��ǰ��ķ��鳤�ȿ϶�����dataLength
                    startTip[i]=dataLength*i;
                }
                if(i!=number-1){
                    lengthTip[i]=dataLength;
                }else{
                    lengthTip[i]=length-dataLength*i;
                }
            }
        }
        //���濪ʼ����
        //�趨����С�����Ļ���
        //�������С�����
        MyRandom myRandom=new MyRandom(1);
        //����random1,���ڷ�ֹ���յ�ͬһ������ӷ�����ظ�����
        //���Ͷ˱�֤,��ֵᷢΪ0��С�����
        while(true){
            //��˳������δ�ɹ����͵���ݰ�
            for(int i=0;i<number;i++){
                if(sendTip[i]==false){
                    sendDatagramData(numberRandom,blockRandom,i,buffer,startTip[i],lengthTip[i]);
                }
            }
            //���������ط���(��ʱ5ms),�ں�����"������ϰ�"
            //��getDatagramReq��,�Ѿ��޸���sendTip��ֵ
            boolean flag=getDatagramReq(numberRandom,blockRandom,myRandom.getNextRandom(),sendTip);
            if(flag){
                break;
            }
        }
    }    
    /*���ͷ���ͷ*/
    public void sendDatagramHead(int numberRandom,int blockRandom,int number){
        byte buffer[]=new byte[HeadTip[0]];
        DatagramPacket packet=null;
        //������ͷ��ʶ
        ByteChange.StringToByte(datagramHead, buffer, HeadTip[1], 4);
        //���������
        ByteChange.intToByte(buffer, HeadTip[2], numberRandom);
        //���������
        ByteChange.intToByte(buffer, HeadTip[3], blockRandom);
        //д����������
        assert number<=groupNum:"������������500";
        ByteChange.shortToByte(buffer,HeadTip[4],(short)number);
        //��䱨β��ʶ
        ByteChange.StringToByte(datagramTail, buffer, HeadTip[5], 4);
        //���ͷ���ͷ
        try {
             //�����ý��ճ�ʱʱ��
            socket.setSoTimeout(1);
            packet=new DatagramPacket(buffer,buffer.length,InetAddress.getByName(yourAddress),yourPort);
        } catch (Exception ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        //���ͺ���ձ�ͷȷ�ϱ�,���ճ�ʱ���߽��յĲ�ƥ�����ط�����ͷ
        boolean canSendHead=true;
        while(true){
            try {
                if(canSendHead){
                    socket.send(packet);
                }
                byte c[]=new byte[HeadAckTip[0]];
                DatagramPacket dp=new DatagramPacket(c,c.length);
                ByteChange.cleanByte(c);
                socket.receive(dp);
                //ֻҪ���յ���ݰ�,�����ǲ���ȷ,�´�ѭ���Ͳ��ܷ���ͷ
                canSendHead=false;
                //���м��
                 //1.������Ƿ��Ƿ���ͷȷ�ϰ�
                String s=new String(c,HeadAckTip[1],4);
                if(!s.equals(datagramHeadAck)){
                    continue;
                }
                //2.�����������,�Ƿ����Լ���ƥ��
                int r=ByteChange.byteToInt(c, HeadAckTip[2]);
                if(r!=numberRandom){
                    continue;
                }
                //3.�����������
                r=ByteChange.byteToInt(c, HeadAckTip[3]);
                if(r!=blockRandom){
                    continue;
                }
                //���м�����ȷ,���ͳɹ�,����
                break;
            } catch (Exception ex) {
                //Logger.getLogger(Send.class.getName()).log(Level.SEVERE, null, ex);
                //����ǽ��ճ�ʱ,��һ�´�ѭ����ʱ��,Ҫ�ȷ���ͷ
                if(ex instanceof SocketTimeoutException){
                    canSendHead=true;
                }
            }
        }
    }
    /*���ͷ����,�޻���*/
    public void sendDatagramData(int numberRandom,int blockRandom,int serial,byte buffer[],int start,int length){
        DatagramPacket packet=null;
        byte b[]=new byte[DataTip[0]];
        //System.out.println("���ͷ���,���к�:"+serial);
        assert length<=dataLength:"Error[���ͷ����]:��ݳ��ȴ���dataLength!";
        //��������ʶ
        ByteChange.StringToByte(datagramData, b, DataTip[1], 4);
        //���������
        ByteChange.intToByte(b, DataTip[2], numberRandom);
        //���������
        ByteChange.intToByte(b, DataTip[3], blockRandom);
        //���������
        ByteChange.shortToByte(b, DataTip[4], (short)serial);
        //�����ݳ���
        ByteChange.shortToByte(b, DataTip[5], (short)length);
        //���ȴ�������
        for(int i=0;i<length;i++){
            b[i+DataTip[6]]=buffer[i+start];
        }
        //��䱨β
        ByteChange.StringToByte(datagramTail, b, DataTip[7], 4);
        //������,����
        try {
            packet = new DatagramPacket(b, b.length, InetAddress.getByName(yourAddress), yourPort);
            socket.send(packet);
        } catch (Exception ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*���������ط���*/
    /*���ؽ��շ��Ƿ�������*/
    public boolean getDatagramReq(int numberRandom,int blockRandom,int littleRandom,boolean sendTip[]){
        //����,���ý��ճ�ʱ5ms
        try {
            socket.setSoTimeout(5);
        } catch (SocketException ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        DatagramPacket packetReq=null;
        //���������ط����͵�����Ҫ�ط��ķ���
        //����,�Ƚ��ɹ����ͱ��ȫ����true,�ٸ��
        for(int i=0;i<sendTip.length;i++){
            sendTip[i]=true;
        }
        //�����ط�����ʧ�ܵ���false
        byte buffer[]=new byte[ReqAgainTip[0]];
        boolean canSendFinish=true;
        while(true){
            try {
                if(canSendFinish){
                    sendFinish(numberRandom,blockRandom,littleRandom);
                }
                packetReq = new DatagramPacket(buffer, buffer.length);
                ByteChange.cleanByte(buffer);
                socket.receive(packetReq);
                //�����յ���ݰ�,�����Ƿ���ȷ,��һ�ֶ����ܷ�δ���������ط���
                canSendFinish=false;
                //�Խ��յ����뷨���ط�����н���
                //��֤�ǲ��������ط���
                String s=new String(buffer,ReqAgainTip[1],4);
                if(!s.equals(datagramReqAgain)){
                    continue;
                }
                //ƥ��������
                int r=ByteChange.byteToInt(buffer, ReqAgainTip[2]);
                if(r!=numberRandom){
                    continue;
                }
                //ƥ��������
                r=ByteChange.byteToInt(buffer, ReqAgainTip[3]);
                if(r!=blockRandom){
                    continue;
                }       
//                //��֤�ð��Ƿ�����
//                s=new String(buffer,1014,4);
//                if(!s.equals(datagramTail)){
//                    continue;
//                }
                //��֤�������ط����С������Ƿ��뱾�η��͵�"������ϰ�"��ͬ
                r=ByteChange.byteToInt(buffer, ReqAgainTip[4]);
                if(r!=littleRandom){
                    continue;
                }
                //��֤���,��ȡ�����ط��ķ���
                //��ȡ��ݳ���
                int length=ByteChange.byteToShort(buffer, ReqAgainTip[5]);
                //�����ݳ�����0,��֤һ�����������,�������,�򷵻���ݽ������
                if(length==0){
                    s=new String(buffer,ReqAgainTip[5]+2,4);
                    if(s.equals(datagramTail)){
                        //�������,����
                        return true;
                    }else{
                        //��ݲ�����,�˰���Ч,����ѭ��
                        continue;
                    }
                }else{
                    for(int i=0;i<length;i++){
                        int tip=ByteChange.byteToShort(buffer, i*2+ReqAgainTip[6]);
                        sendTip[tip]=false;
                    }
                    break;
                }
            } catch (Exception ex) {
                //Logger.getLogger(Send.class.getName()).log(Level.SEVERE, null, ex);
                //����ǽ��ճ�ʱ,��ô��һ��ѭ����Ӧ�÷���"������ϰ�"
                if(ex instanceof SocketTimeoutException){
                    canSendFinish=true;
                    //System.out.println("���������ط���ʱ");
                }
            }
        }
        return false;
    }
    /*���ͷ�����ϰ�*/
    public void sendFinish(int numberRandom,int blockRandom,int littleRandom){
        byte buffer[]=new byte[FinishTip[0]];
        //��䷢����ϰ��ʶ
        ByteChange.StringToByte(datagramFinish, buffer, FinishTip[1], 4);
        //���������
        ByteChange.intToByte(buffer, FinishTip[2], numberRandom);
        //���������
        ByteChange.intToByte(buffer, FinishTip[3], blockRandom);
        //���С�����
        ByteChange.intToByte(buffer, FinishTip[4], littleRandom);
        //��䱨β
        ByteChange.StringToByte(datagramTail, buffer, FinishTip[5], 4);
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(yourAddress), yourPort);
            socket.send(packet);
        } catch (Exception ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*���ͽ������*/
    public void sendOver(int numberRandom){
        //�趨���ճ�ʱ1ms
        try {
            socket.setSoTimeout(1);
        } catch (SocketException ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        //��ɴ�������
        DatagramPacket packetOver,packetOverAck;
        byte overBuffer[]=new byte[StopTip[0]];
        byte overAckBuffer[]=new byte[StopAckTip[0]];
        //����ʶ
        ByteChange.StringToByte(datagramStop, overBuffer, StopTip[1], datagramStop.length());
        //���������
        ByteChange.intToByte(overBuffer, StopTip[2], numberRandom);
        //��䱨β
        ByteChange.StringToByte(datagramTail, overBuffer, StopTip[3], datagramTail.length());
        try {
            packetOver = new DatagramPacket(overBuffer, overBuffer.length, InetAddress.getByName(yourAddress), yourPort);
            packetOverAck =new DatagramPacket(overAckBuffer,overAckBuffer.length);
            while(true){
                try {
                    socket.send(packetOver);
                    ByteChange.cleanByte(overAckBuffer);
                    socket.receive(packetOverAck);
                    //���ACK��ʶ
                    String str=new String(overAckBuffer,StopAckTip[1],4);
                    if(!str.equals(datagramStopAck)){
                        continue;
                    }
                    //���������
                    int num=ByteChange.byteToInt(overAckBuffer, StopAckTip[2]);
                    if(num!=numberRandom){
                        continue;
                    }
                    break;
                } catch (Exception ex) {
                    if(ex instanceof SocketTimeoutException){
                        //���ճ�ʱ,�����ѭ��
                    }
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(SendLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


