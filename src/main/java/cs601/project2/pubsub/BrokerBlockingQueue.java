package cs601.project2.pubsub;

import java.util.concurrent.TimeUnit;

public class BrokerBlockingQueue<T> {
	private T[] items;
	private int start;
	private int end;
	private int size;
	
	public BrokerBlockingQueue(int size) {
		super();
		this.items = (T[]) new Object[size];
		this.start = 0;
		this.end = -1;
		this.size = 0;
	}
	
	public synchronized void put(T item) {
		while(size == items.length) {
			try {
				this.wait();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		int next = (end+1)%items.length;
		items[next] = item;
		end = next;
		size++;
		if(size == 1) {
			this.notifyAll();
		}
	}
	
	public synchronized T take() {
		while(size == 0) {
			try {
				this.wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		T item = items[start];
		start = (start+1)%items.length;
		size--;
		if(size == items.length-1) {
			this.notifyAll();
		}
		return item;
	}
	
	public synchronized T poll(long timeOut, TimeUnit unit) {
		while(size == 0) {
			try {
				long start = System.currentTimeMillis();
				this.wait(TimeUnit.MILLISECONDS.convert(timeOut, unit));
				long endTime = System.currentTimeMillis() - start;
				if ( endTime >= TimeUnit.MILLISECONDS.convert(timeOut, unit) ) {
					return null;
				};
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		T item = items[start];
		start = (start+1)%items.length;
		size--;
		if(size == items.length-1) {
			this.notifyAll();
		}
		return item;
	}
}
