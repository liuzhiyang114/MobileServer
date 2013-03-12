/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartlab.mobileserver.tool.NetTransmit;

/**
 *
 * @author Administrator
 */
public class CircleStack {
    int buffer[];
    int index;
    int size;
    boolean full;
    public CircleStack(int size){
        buffer=new int[size];
        for(int i=0;i<size;i++){
            buffer[i]=0;
        }
        index=0;
        this.size=size;
        full=false;
    }
    public void add(int value){
       buffer[index++]=value;
       if(index==size){
           index=0;
           full=true;
       }
    }
    public int getLast(){
        int index1;
        if(index>0){
            index1=index-1;
            return buffer[index1];
        }else if(full){
            index1=size-1;
            return buffer[index1];
        }
        return -1;
    }
    public boolean inIt(int value){
        if(full){
            for(int i=0;i<size;i++){
                if(buffer[i]==value){
                    return true;
                }
            }
        }else{
            for(int i=0;i<index;i++){
                if(buffer[i]==value){
                    return true;
                }
            }
        }
        return false;
    }
    public static void main(String args[]){
        CircleStack stack=new CircleStack(100);
        for(int i=0;i<120;i++){
            stack.add(i);
        }
        System.out.println(stack.getLast());
        System.out.println(stack.inIt(20));
        System.out.println(stack.inIt(50));
    }
}
