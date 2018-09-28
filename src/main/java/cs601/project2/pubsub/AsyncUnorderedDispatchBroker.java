package cs601.project2.pubsub;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncUnorderedDispatchBroker<T> implements Broker<T> {
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();
	ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
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
			System.out.println("Added");
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
