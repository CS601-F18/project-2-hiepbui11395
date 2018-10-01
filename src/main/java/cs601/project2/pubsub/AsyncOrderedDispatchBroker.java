package cs601.project2.pubsub;

import java.util.ArrayList;

public class AsyncOrderedDispatchBroker<T> implements Broker<T>, Runnable {
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();
	final BrokerBlockingQueue<T> queue = new BrokerBlockingQueue<T>(20);

	public synchronized void publish(T item) {
		queue.put(item);
	}

	public void subscribe(Subscriber<T> subscriber) {
		synchronized(subscribers) {
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		this.publish(null);
	}
	
	@Override
	public void run() {
		T itemBeSent;
		while((itemBeSent = queue.take()) != null) {
			T result = itemBeSent;
			subscribers.forEach(subscriber -> subscriber.onEvent(result));
		}
	}

}
