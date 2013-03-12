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
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import java.io.*;
public class SendImage {
    public SendImage(){
        try {
            SendLittle sendLittle = new SendLittle("localhost",12000,10000);
            Robot robot = new Robot();
            Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle rect=new Rectangle(0,0,dimension.width,dimension.height);
            while(true){
                BufferedImage buffer=robot.createScreenCapture(rect);
                ByteArrayOutputStream bos=new ByteArrayOutputStream(1024*1024);
                ImageIO.write(buffer, "jpg", bos);
                byte array[]=bos.toByteArray();
                sendLittle.sendAll(array, array.length);
            }
        } catch (Exception ex) {
            Logger.getLogger(SendImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String args[]){
        new SendImage();
    }
}
