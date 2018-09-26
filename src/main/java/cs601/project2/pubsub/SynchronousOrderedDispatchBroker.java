package cs601.project2.pubsub;

import java.util.ArrayList;

public class SynchronousOrderedDispatchBroker<T> implements Broker<T> {
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();


	public void publish(T item) {
		// TODO A newly published item will be synchronously delivered to all subscribers. 
		// The publish method will not return to the publisher until all subscribers have completed the onEvent method.
		synchronized(subscribers) {
			subscribers.forEach(subscriber -> subscriber.onEvent(item));
		}
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

}
