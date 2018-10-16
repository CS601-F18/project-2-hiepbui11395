package cs601.project2.pubsub;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cs601.project2.Utils;

public class AsyncUnorderedDispatchBroker<T> implements Broker<T> {
	private ArrayList<Subscriber<T>> subscribers;
	private ExecutorService threadPool;
	
	

	public AsyncUnorderedDispatchBroker() {
		this.subscribers = new ArrayList<Subscriber<T>>();
		this.threadPool = Executors.newFixedThreadPool(Utils.NUMOFTHREADPOOL);
	}

	public synchronized void publish(T item) {
		threadPool.execute(new Runnable() {
			public void run() {
//				synchronized(subscribers) {
					T itemBeSent = item;
					subscribers.forEach(subscriber -> subscriber.onEvent(itemBeSent));
//				}
		    }
		});
	}

	public void subscribe(Subscriber<T> subscriber) {
		synchronized(subscribers) {
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(Utils.AWAITTIME, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(Subscriber<T> subscriber : subscribers) {
			if(subscriber instanceof RemoteSubscriberProxy) {
				((RemoteSubscriberProxy)subscriber).closeSocket();
			}
		}
	}

}
