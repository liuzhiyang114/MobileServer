/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;

/**
 *
 * @author Administrator
 */
public class ByteChange {
    //��int����byte����,ʹ��С��ģʽ
    public static void intToByte(byte b[],int start,int num){
        for(int i=start;i<start+4;i++){
            b[i]=(byte)num;
            num>>>=8;
        }
    }
    //��byte��������ȡint,ʹ��С��ģʽ
    public static int byteToInt(byte b[],int start){
        int num=0;
        for(int i=3+start;i>=start;i--){
            num=num<<8;
            num+=(0xff&(int)b[i]);
        }
        return num;
    }
    //��short����byte����,ʹ��С��ģʽ
    public static void shortToByte(byte b[],int start,short num){
        for(int i=start;i<start+2;i++){
            b[i]=(byte)num;
            num>>>=8;
        }
    }
    //��byte��������ȡshort,ʹ��С��ģʽ
    public static short byteToShort(byte b[],int start){
        short num=0;
        for(int i=1+start;i>=start;i--){
            num=(short)(num<<8);
            num+=(0xff&(int)b[i]);
        }
        return num;
    }
    //��String���������
    public static void StringToByte(String str,byte b[],int start,int length){
        byte c[]=str.getBytes();
        for(int i=0;i<length;i++){
            b[start+i]=c[i];
        }
    }
    //�������
    public static void cleanByte(byte b[]){
        for(int i=0;i<b.length;i++){
            b[i]=0;
        }
    }
    public static void main(String args[]){
        byte buffer[]=new byte[2000];
        for(int i=0;i<1000;i++){
            shortToByte(buffer,i*2,(short)i);
        }
        for(int i=0;i<1000;i++){
            System.out.print(byteToShort(buffer,i*2)+"  ");
            if(i!=0&&i%30==0){
                System.out.println();
            }
        }
    }
}
