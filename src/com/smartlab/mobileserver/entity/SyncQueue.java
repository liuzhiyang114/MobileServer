package com.smartlab.mobileserver.entity;

public class SyncQueue {

	public SyncQueue(int size) {
	       _array = new Object[size];
	       _size = size;
	       _oldest = 0;
	       _next = 0;
	    }

	    public synchronized void put(Object o) {
	       while (full()) {
	           try {
	              wait();
	           } catch (InterruptedException ex) {
	              System.out.println("put ex:"+ex.toString());
	           }
	       }
	       _array[_next] = o;
	       _next = (_next + 1) % _size;
	       notify();
	    }

	    public synchronized Object get() {
	       while (empty()) {
	           try {
	              wait();
	           } catch (InterruptedException ex) {
	              System.out.println("get ex:"+ex.toString());
	           }
	       }
	       Object ret = _array[_oldest];
	       _oldest = (_oldest + 1) % _size;
	       notify();
	       return ret;
	    }

	    protected boolean empty() {
	       return _next == _oldest;
	    }

	    protected boolean full() {
	       return (_next + 1) % _size == _oldest;
	    }

	    protected Object [] _array;
	    protected int _next;
	    protected int _oldest;
	    protected int _size;
}
