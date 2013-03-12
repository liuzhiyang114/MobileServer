/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class TestSend {
    public static void main(String args[]){
        FileInputStream fis=null;
        try {
            //����ֱ��ǣ��Է���IP���ҵĶ˿ںţ��Է��Ķ˿ں�
            //����send�󣬾Ϳ�������շ����������
            //ÿ�η�����ݵ���sendAll����ϸ˵�����
            SendLittle send = new SendLittle("localhost", 12000, 10000);
            fis = new FileInputStream("screen.jpg");
            final int littleLength=1024*1024;
            byte b[]=new byte[littleLength];
            while(fis.available()>0){
                int length=fis.read(b);
                //b�Ǵ��͵��ֽ����飬length��Ҫ���͵ĳ���
                //������Է������޴����ݣ���Ȼlength�����������з�Χ��
                //sendAll���ڵȷ��͵���ݣ�����з��飬ÿ�η���һ�����飬��һ��������
                //250����ݰ�ÿ�������󳤶���1000��sendAll�ڳɹ�����һ��������
                //�ᷢ����һ�����顣
                send.sendAll(b, length);
            }
            //֮ǰ������ܻ�е��ɻ�sendAll������������ݵģ���ô������������ӣ��ļ�
            //��������ˣ��Է����֪���أ�
            //����������˷ֲ��˼�룬sendAllֻ�Ǹ�������ݣ���ֻ�ǰѴ���͵����
            //׼ȷ�ط��͸�Է���receiveAllֻ�Ǹ��������ݡ�
            //���ڷ��ͺͽ��յ���ݣ���ʲô������壬�����ǲ��ܵġ�
            //ʲôʱ���ͽ�������԰���Ӧ����Ϣ����sendAll���͵�������,��������������
            String end="end";
            ByteChange.StringToByte(end, b, 0, 3);
            send.sendAll(b, 3);
            //���շ�������ݲ�������Ӧ�����飬Ȼ�����ж�һ�½��յ����ֽ��������3
            //�ٿ����ǲ��Ǻ�"end"ƥ�䣬���ƥ�䣬�ͽ�����ա�
            //����ֻ��һ���򵥵�ʾ�����ֻ�Ǵ���һ���ļ���������Ǵ������ļ���������ʵ��
            //�����ļ������������ݵĹ��ܣ���ô����Խ��ļ����Ƿ���Ŀ¼����Ϣȫ����������
            //�ֽ����顣���շ��ٰ���һ���Ĺ�����ֽ���������ȡ��Ϣ���ҵ�sendAllֻ��һ����
            //���ģ��
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(TestReceive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(TestReceive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
