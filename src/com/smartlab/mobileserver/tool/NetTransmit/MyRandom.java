/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;

/**
 *
 * @author Administrator
 */
public class MyRandom {
    int randomValue;
    public MyRandom(int start){
        randomValue=start;
    }
    public int getNextRandom(){
        return randomValue++;
    }
    public static void main(String args[]){
        MyRandom random=new MyRandom(0);
        for(int i=0;i<100;i++){
            System.out.print(random.getNextRandom()+"  ");
            if(i!=0&&i%20==0){
                System.out.println();
            }
        }
    }
}
