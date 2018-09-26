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
			System.out.println("Added");
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void run() {
		while(true) {
			T itemBeSent = queue.take();
			subscribers.forEach(subscriber -> subscriber.onEvent(itemBeSent));
		}
	}

}
