package cs601.project2.pubsub;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cs601.project2.Utils;

public class AsyncOrderedDispatchBroker<T> implements Broker<T>, Runnable {
	private ArrayList<Subscriber<T>> subscribers;
	private BrokerBlockingQueue<T> queue;
	private boolean finished = false;
	
	

	public AsyncOrderedDispatchBroker() {
		subscribers = new ArrayList<Subscriber<T>>();
		queue = new BrokerBlockingQueue<T>(Utils.NUMOFQUEUE);
	}

	public void publish(T item) {
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
		while(((itemBeSent = queue.poll(Utils.POLLTIME, TimeUnit.SECONDS))!= null) || !finished) {
			if(itemBeSent!=null) {
				T result = itemBeSent;
				subscribers.forEach(subscriber -> subscriber.onEvent(result));
			}
		}
	}

}
