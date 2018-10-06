package cs601.project2.pubsub;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AsyncOrderedDispatchBroker<T> implements Broker<T>, Runnable {
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();
	final BrokerBlockingQueue<T> queue = new BrokerBlockingQueue<T>(20);
	private boolean finished = false;

	public synchronized void publish(T item) {
		queue.put(item);
	}

	public void subscribe(Subscriber<T> subscriber) {
		synchronized(subscribers) {
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		finished = true;
	}
	
	@Override
	public void run() {
		T itemBeSent = null;
		while(((itemBeSent = queue.poll(1, TimeUnit.SECONDS))!= null) || !finished) {
			if(itemBeSent!=null) {
				T result = itemBeSent;
				subscribers.forEach(subscriber -> subscriber.onEvent(result));
			}
		}
//		while((itemBeSent = queue.take()) != null) {
//			T result = itemBeSent;
//			subscribers.forEach(subscriber -> subscriber.onEvent(result));
//		}
	}

}
