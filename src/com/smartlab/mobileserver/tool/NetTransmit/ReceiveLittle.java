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
public class ReceiveLittle {
    //��ݰ���
    DatagramSocket socket;
    //DatagramPacket packet;
    //��ݰ����Ϣ
    String yourAddress="192.168.1.100";  //119.112.6.203 //180.109.158.89
    int yourPort=12000;
    int myPort=10000;
    /*���ڷ�������*/
    final int StartTip[]={12,0,4,8};                   //��ʼ�����(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StartAckTip[]={12,0,4,8};                //��ʼȷ�ϰ�(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StopTip[]={12,0,4,8};                    //�������(12)    ��ʶ(4)|�������(4)|��β(4)
    final int StopAckTip[]={12,0,4,8};                 //����ȷ�ϰ�(12)    ��ʶ(4)|�������(4)|��β(4)
    final int HeadTip[]={18,0,4,8,12,14};              //����ͷ��(18)      ��ʶ(4)|�������(4)|�������(4)|��������(2)|��β(4)
    final int HeadAckTip[]={16,0,4,8,12};              //����ͷȷ�ϰ�(16)  ��ʶ(4)|�������(4)|�������(4)|��β(4)
    final int DataTip[]={1020,0,4,8,12,14,16,1016};      //������ݰ�(520)   ��ʶ(4)|�������(4)|�������(4)|���(2)|����(2)|���(500)|��β(4)
    final int FinishTip[]={20,0,4,8,12,16};            //������ϰ�(20)    ��ʶ(40|�������(4)|�������(4)|С�����(4)|��β(4)
    final int ReqAgainTip[]={522,0,4,8,12,16,18,518};   //�����ط���(522)   ��ʶ(4)|�������(4)|�������(4)|С�����(4)|����(2)|���(500)|��β(4)
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
    //��������洢�յ��Ĵ�,С�����
    CircleStack randomStack=null,littleRandomStack=null;
    //���ڴ����һ�η��͵������ط���
    DatagramPacket lastReqAgain=null;
    public ReceiveLittle(String addr,int mp,int yp){
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
    /*�������*/
    public int receiveAll(byte littleBuffer[]){
        byte buffer[]=new byte[dataLength*groupNum];
        randomStack=new CircleStack(100);
        int numberRandom=receiveStart();
        int allLength=0;
        while(true){
            int length=receiveGroup(numberRandom,buffer);
            if(length!=-1){
                for(int i=0;i<length;i++){
                    littleBuffer[i+allLength]=buffer[i];
                }
                allLength+=length;
            }else{
                sendOverAck(numberRandom);
                return allLength;
            }
        }
    }
    /*���տ�ʼ�����*/
    public int receiveStart(){
        DatagramPacket sendPacket=null,receivePacket=null;
        //���ڷ��Ϳ�ʼȷ�ϰ�
        byte sendBuffer[]=new byte[StartAckTip[0]];
        //���ڽ��տ�ʼ�����
        byte receiveBuffer[]=new byte[StartTip[0]];
        try{
            socket.setSoTimeout(0);
            sendPacket=new DatagramPacket(sendBuffer,sendBuffer.length,InetAddress.getByName(yourAddress),yourPort);
            receivePacket=new DatagramPacket(receiveBuffer,receiveBuffer.length);
        }catch(Exception e){}
        while(true){
            try {
                socket.receive(receivePacket);
                //����ʶ
                String str=new String(receiveBuffer,StartTip[1],4);
                if(!str.equals(datagramStart)){
                    continue;
                }
                //��ȡ�������
                int numberRandom=ByteChange.byteToInt(receiveBuffer, StartTip[2]);
                sendStartAck(numberRandom);
                return numberRandom;
            } catch (IOException ex) {
                Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /*���Ϳ�ʼȷ�ϰ�*/
    public void sendStartAck(int numberRandom){
        DatagramPacket sendPacket=null;
        byte sendBuffer[]=new byte[StartAckTip[0]];
        //����ʶ
        ByteChange.StringToByte(datagramStartAck, sendBuffer, StartAckTip[1], 4);
        //���������
        ByteChange.intToByte(sendBuffer, StartAckTip[2], numberRandom);
        //��䱨β
        ByteChange.StringToByte(datagramTail, sendBuffer, StartAckTip[3], 4);
        try {
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(yourAddress), yourPort);
            socket.send(sendPacket);
        } catch (Exception ex) {
            Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /*����һ�η���*/
    //����ֵΪ����������ݵĳ���.����,�˺�����д��ͽ���ļ��.���ֽ���,����-1
    public int receiveGroup(int numberRandom,byte buffer[]){
        //���շ���ͷ
        rdh_return rdh=receiveDatagramHead(numberRandom);
        //System.out.println("�յ�����ͷ,�����:"+rdh.random+",��������:"+rdh.number);
        //����������������
        int random=rdh.random;
        int number=rdh.number;
        if(number==0){
            return -1;
        }
        randomStack.add(random);
        littleRandomStack=new CircleStack(100);
        //�趨����ܳ���
        int lengthAll=0;
        //����ֻ�����һ�����ݳ��Ȳ���dataLength,����,�ȼ���ǰnumber-1��������ܳ���
        lengthAll=dataLength*(number-1);
        //���ڼ�¼�ɹ���������Щ����
        boolean receiveTip[]=new boolean[number];
        for(int i=0;i<number;i++){
            receiveTip[i]=false;
        }
        //Ԥ�ȼ������Ӧ����buffer����ʼ�±�
        //���ڳ������һ��,���������ݳ���һ����dataLength.���Լ�����ʼ�±�ǳ���
        int startTip[]=new int[number];
        for(int i=0;i<number;i++){
            startTip[i]=i*dataLength;
        }
        //�����ѭ��,������ȥ���շ���,ֱ�����з������
        while(true){
            int result=getDatagramData(numberRandom,random,buffer,receiveTip,startTip);
            //�����յ�����һ������.Ҫ��¼�䳤��.����,�������һ��,����ĳ���һ����dataLength
            //����,������length����dataLength,˵�������һ��ĳ���
            if(result>0){
                if(result!=dataLength){
                    lengthAll+=result;
                }
                continue;
            }
            //�����յ��Ƿ���ͷ
            else if(result==-2){
                //���ͷ���ͷȷ�ϰ�,Ȼ��ѭ��
                sendDatagramHeadAck(numberRandom,random);
                continue;
            }
            //������µķ�����ϰ�
            else if(result==-3){
                //���������ط���
                //�ڷ��������ط����ʱ��,��Ҫ����Ƿ�ȫ���ɹ����յ�,����,���ﲻ�����¼��,����ʹ������Ľ��
                boolean flag=sendDatagramReqAgain(numberRandom,random,littleRandomStack.getLast(),receiveTip);
                //System.out.println("���������ط���");
                if(flag){
                    break;
                }
                continue; 
            }
            //������ϴεķ�����ϰ�
            else if(result==-4){
                //����ϴη��͵Ĳ���0�����ط���,���ط�
                //��0�����ط���ĳ���,һ����1018
                //System.out.println("���յ��ϴεķ�����ϰ�:");
                if(lastReqAgain.getLength()==ReqAgainTip[0]){
                    try {
                        //System.out.println("�ط���0�����ط���");
                        socket.send(lastReqAgain);
                    } catch (Exception ex) {
                        Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                continue;
            }
        }
        //�����и��ܵ��۵�����.���ڲ�ȷ�����һ���Ƿ�����,ֻ���ڽ��յ����һ������ʱ��֪������ݳ���
        //����շ������ڵ����ĺ��������,����ص��ǽ��յ���ݳ���.��ѭ����,�������ݳ��Ȳ���dataLength
        //����Ϊ�������һ��,�����䳤�ȼ���lengthAll.����,��һ�����ص�����,��Ϊ,ÿ����,ֻ�����һ��������һ��
        //�������ݳ��ȿ��ܲ���dataLength.����,ǰ��Ŀ�,�ڽ��м����ʱ��,�л���������һ��ĳ���.�����Ժ�Ŀ�
        //�������
        if(lengthAll==(number-1)*dataLength){
            lengthAll+=dataLength;
        }
        return lengthAll;
    }
    /*���շ���ͷ*/
    //1.�����ճ�ʱ. 2.���ճɹ�,����÷��ͷ���ͷȷ�ϰ�
    public rdh_return receiveDatagramHead(int numberRandom){
        byte buffer[]=new byte[HeadTip[0]];
        DatagramPacket packet=null;
        rdh_return rdh=new rdh_return();
        //���ò����ճ�ʱ
        try {
            socket.setSoTimeout(0);
        } catch (SocketException ex) {
            Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
            try {
                packet=new DatagramPacket(buffer,buffer.length);//,InetAddress.getByName(yourAddress),yourPort
                ByteChange.cleanByte(buffer);
                socket.receive(packet);
                //������ͷ��ʶ
                String str=new String(buffer,HeadTip[1],4);
                if(!str.equals(datagramHead)){
                    /*��Ȼ���Ƿ���ͷ,���½����ǿ϶���.ֻ����,��Щ���,��Ҫ����һ�������½���*/
                    //����ǿ�ʼ�����
                    if(str.equals(datagramStart)){
                        int num=ByteChange.byteToInt(buffer, StartTip[2]);
                        if(num==numberRandom){
                            sendStartAck(numberRandom);
                            continue;
                        }
                    }
                    //����Ƿ�����ϰ�
                    if(str.equals(datagramFinish)){
                        //�ȼ���������
                        if( ByteChange.byteToInt(buffer, FinishTip[2])==numberRandom ){
                            //�����ǲ�����һ�εķ�����ϰ�
                            //����ǵ�,˵���ϴη���0�����ط���û���ɹ�
                            //�ȼ�����������ǲ�����һ�ε�
                            if( randomStack.getLast()!=-1 && ByteChange.byteToInt(buffer, FinishTip[3])==randomStack.getLast() ){
                                //�ټ����С�����,�ǲ����ϴε�(�����һ�η�������)
                                if(littleRandomStack!=null){
                                    //���littelRandomStack��null,˵�����ǵ�һ�ν��շ���ͷ,��Ȼ������Ҫ�ط�
                                    if(littleRandomStack.getLast()==ByteChange.byteToInt(buffer, FinishTip[4])){
                                        //���С�����Ҳƥ��,�����ϴε�0�����ط���
                                        if(lastReqAgain!=null){
                                            socket.send(lastReqAgain);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //����Ǵ�������,��0�������������rdh,Ȼ�󷵻�
                    if(str.equals(datagramStop)){
                        rdh.number=0;
                        return rdh;
                    }
                    continue;
                }
                //���ð��Ƿ�����
                str=new String(buffer,HeadTip[5],4);
                if(!str.equals(datagramTail)){
                    continue;
                }
                //������,��ȡ�����
                rdh.random=ByteChange.byteToInt(buffer, HeadTip[3]);
                //��ȡ��������
                rdh.number=ByteChange.byteToShort(buffer, HeadTip[4]);
                sendDatagramHeadAck(numberRandom,rdh.random);
                break;
            } catch (Exception ex) {
                Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rdh;
    }
    //�ڴ�,������Ҫ����������Ϣ: 1.���������� 2.��������� ������Ҫ����һ���ڲ���
    class rdh_return{
        int random;
        int number;
    }
    /*���ͷ���ͷȷ�ϰ�*/
    public void sendDatagramHeadAck(int numberRandom,int blockRandom){
        //System.out.println("���ͷ���ͷȷ�ϰ�");
        byte buffer[]=new byte[HeadAckTip[0]];
        DatagramPacket packet=null;
        //������ͷȷ�ϰ��ʶ
        ByteChange.StringToByte(datagramHeadAck, buffer, HeadAckTip[1], 4);
        //���������
        ByteChange.intToByte(buffer, HeadAckTip[2], numberRandom);
        //���������
        ByteChange.intToByte(buffer, HeadAckTip[3], blockRandom);
        //��䱨β
        ByteChange.StringToByte(datagramTail, buffer, HeadAckTip[4], 4);
        //������,���ͱ���
        try {
             packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(yourAddress), yourPort);
             socket.send(packet);
        } catch (Exception ex) {
            //Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*���������ط���*/
    public boolean sendDatagramReqAgain(int numberRandom,int blockRandom,int littleRandom,boolean receiveTip[]){
        //���ж��Ƿ�ȫ�����ճɹ�
        boolean success=true;
        for(int i=0;i<receiveTip.length;i++){
            if(receiveTip[i]==false){
                success=false;
                break;
            }
        }
        byte buffer[]=new byte[ReqAgainTip[0]];
        DatagramPacket packet;
        //��������ط����ʶ
        ByteChange.StringToByte(datagramReqAgain, buffer, ReqAgainTip[1], 4);
        //���������
        ByteChange.intToByte(buffer, ReqAgainTip[2], numberRandom);
        //���������
        ByteChange.intToByte(buffer, ReqAgainTip[3], blockRandom);
        //���С�����
        ByteChange.intToByte(buffer, ReqAgainTip[4], littleRandom);
        //�����ݳ���
        if(success){
            //ȫ���ɹ�����,����Ӧ�÷��͵���0�����ط���
            ByteChange.shortToByte(buffer, ReqAgainTip[5], (short)0);
            //���ڳ���Ϊ0,����û�����,ֱ����䱨β
            ByteChange.StringToByte(datagramTail, buffer, ReqAgainTip[6], 4);
        }
        else{
            //����receiveTip,����false�ͽ��±����buffer
            int length=0;
            for(int i=0;i<receiveTip.length;i++){
                if(receiveTip[i]==false){
                    ByteChange.shortToByte(buffer, ReqAgainTip[6]+length*2, (short)i);
                    length++;
                }
            }
            //�����ݳ���
            ByteChange.shortToByte(buffer, ReqAgainTip[5], (short)length);
            //��䱨β
            ByteChange.StringToByte(datagramTail, buffer, ReqAgainTip[7], 4);
        }
        try {
            //������,����
            if(success){
                packet = new DatagramPacket(buffer, ReqAgainTip[6]+4, InetAddress.getByName(yourAddress), yourPort);
            }else{
                packet = new DatagramPacket(buffer, ReqAgainTip[0], InetAddress.getByName(yourAddress), yourPort);
            }
            lastReqAgain=packet;
            //System.out.println("���������ط���,����"+lastReqAgain.getLength());
            socket.send(packet);
        } catch (Exception ex) {
            Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }
    /*����һ������*/
    /*����ֵ˵��:
     * 1.��������,���ض�ȡ��ݵĳ���
     * 2.�����ճ�ʱ,����-1  //�˾��Ѿ���Ч
     * 3.�����յ�������ķ���ͷ,����-2
     * 4.�����յ��µķ�����ϰ�,����-3
     * 5.�����յ��ϴεķ�����ϰ�,����-4
     * 6.�������,����-5
     */
    //�˺���ֻ���ڽ���,�������κ���Ϣ.��������,ֻ�ǽ������ʶ����.
    //�����յ����Ƿ����,�������buffer����Ӧλ��,������ݵĳ���
    public int getDatagramData(int numberRandom,int random,byte buffer[],boolean receiveTip[],int startTip[]){
        //���ò����ճ�ʱ
        try {
            socket.setSoTimeout(0);
        } catch (SocketException ex) {
            Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte receiveBuffer[]=new byte[DataTip[0]];
        DatagramPacket packet=new DatagramPacket(receiveBuffer,receiveBuffer.length);
        try {
            ByteChange.cleanByte(receiveBuffer);
            socket.receive(packet);
            //�����յ�δ���������ط���,������������ҲӦ�����뱾�����������ͬ��
            //�������
            if( ByteChange.byteToInt(receiveBuffer, DataTip[2])!=numberRandom ){
                return -5;
            }
            //����,���жϿ������
            int r=ByteChange.byteToInt(receiveBuffer, DataTip[3]);
            if(r!=random){
                return -5;
            }
            //�жϷ����ʶ
            String s=new String(receiveBuffer,DataTip[1],4);
            if(!s.equals(datagramData)){
                //���˰��Ƿ����
                //�ж����Ƿ��Ǳ�����ķ���ͷ.����������Ѿ�ƥ��,����,ֻ��Ҫ�жϱ�ʶ
                if(s.equals(datagramHead)){
                    return -2;
                }
                //���ж����Ƿ��Ƿ�����ϰ�
                if(s.equals(datagramFinish)){
                    //���ж�,�Ƿ����µķ�����ϰ�
                    r=ByteChange.byteToInt(receiveBuffer, FinishTip[4]);
                    if(!littleRandomStack.inIt(r)){
                        littleRandomStack.add(r);
                        return -3;
                    }
                    //������ϴεķ�����ϰ�
                    int r2=littleRandomStack.getLast();
                    if(r2!=-1&&r2==r){
                        return -4;
                    }
                }
                //����,����-5
                return -5;
            }
            //�����Ѿ��Ƿ������,���ж�һ�°��������
            //��Ϊ,�����ǲ������,�����ݾ����������,�ǲ��ܽ��յ�
            s=new String(receiveBuffer,DataTip[7],4);
            if(!s.equals(datagramTail)){
                return -5;
            }
            //���������,��ȡ������
            int serial=ByteChange.byteToShort(receiveBuffer, DataTip[4]);
            int number=ByteChange.byteToShort(receiveBuffer, DataTip[5]);
            //�������еİ�δ����ȷ����
            if(!receiveTip[serial]){
                receiveTip[serial]=true;
                for(int i=0;i<number;i++){
                    buffer[i+startTip[serial]]=receiveBuffer[i+DataTip[6]];
                }
                return number;
            }
            //����Ѿ�����ȷ������,����ð�
            else{
                return -5;
            }
        } catch (Exception ex) {
            //Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
            if(ex instanceof SocketTimeoutException){
                return -1;
            }
            return -5;
        }
    }
    /*���ʹ������ȷ�ϰ�*/
    public void sendOverAck(int numberRandom){
        DatagramPacket packetOverAck=null,packetOver=null;
        byte sendBuffer[]=new byte[StopAckTip[0]];
        byte receiveBuffer[]=new byte[StopTip[0]];
        //���ý��ճ�ʱ1000ms
        try {
            socket.setSoTimeout(1000);
            /*������ȷ�ϰ�*/
            //���ȷ�ϰ��ʶ
            ByteChange.StringToByte(datagramStopAck, sendBuffer, StopAckTip[1], 4);
            //���������
            ByteChange.intToByte(sendBuffer, StopAckTip[2], numberRandom);
            //��䱨β
            ByteChange.StringToByte(datagramTail, sendBuffer, StopAckTip[3], 4);
            /*������*/
            packetOverAck=new DatagramPacket(sendBuffer,sendBuffer.length,InetAddress.getByName(yourAddress),yourPort);
            packetOver=new DatagramPacket(receiveBuffer,receiveBuffer.length);
        } catch (Exception ex) {
            Logger.getLogger(ReceiveLittle.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean canSendAck=true;
        while(true){
            try{
                if(canSendAck){
                    socket.send(packetOverAck);
                }
                ByteChange.cleanByte(receiveBuffer);
                socket.receive(packetOver);
                //System.out.println("Over:�յ���,IP"+packetOver.getAddress());
                //�����յ���������,�ͼ���ѭ��
                String str=new String(receiveBuffer,StopTip[1],4);
                if(str.equals(datagramStop)){
                    //���������
                    int num=ByteChange.byteToInt(receiveBuffer, StopTip[2]);
                    if(num==numberRandom){
                        canSendAck=true;
                        continue;
                    }
                }

                //����յ���ʼ�����,����������������εĲ�һ��,���˳�
                if(str.equals(datagramStart)){
                    //���������
                    if( ByteChange.byteToInt(receiveBuffer, StartTip[2])!=numberRandom ){
                        break;
                    }
                }
                //����������,Ҳ����ѭ��,����һ���ֲ�����ȷ�ϰ�
                canSendAck=false;
                continue;
            }catch(Exception e){
                if(e instanceof SocketTimeoutException){
                    //�����ճ�ʱ,���˳�ѭ��
                    //System.out.println("���ճ�ʱ");
                    break;
                }
            }
        }
    }
}
