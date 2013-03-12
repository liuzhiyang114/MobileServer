/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;

/**
 *
 * @author Administrator
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.Date;
public class ReceiveImage {
    JFrame frame;
    JScrollPane contentPane;
    JPanel imagePane;
    BufferedImage image=null;
    public ReceiveImage(){
        frame=new JFrame();
        imagePane=new JPanel(){
            public void paint(Graphics g){
                int width=this.getWidth();
                int height=this.getHeight();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                if(image!=null){
                    g.drawImage(image, 0, 0, null);
                }
            }
        };
        contentPane=new JScrollPane(imagePane);
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        imagePane.setPreferredSize(dimension);
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(400,200);
        frame.setSize(400,400);
        frame.setVisible(true);
        receiveAndShow();
    }
    public void receiveAndShow(){
        ReceiveLittle receiveLittle=new ReceiveLittle("localhost",10000,12000);
        byte buffer[]=new byte[1024*1024];
        while(true){
            int length=receiveLittle.receiveAll(buffer);
            try{
                Date begin=new Date();
                ByteArrayInputStream bis=new ByteArrayInputStream(buffer,0,length);
                image=ImageIO.read(bis);
                imagePane.repaint();
                Date end=new Date();
                System.out.println("һ�����,ʱ��:"+(end.getTime()-begin.getTime()));
            }catch(Exception e){}
        }
    }
    public static void main(String args[]){
        new ReceiveImage();
    }
}
