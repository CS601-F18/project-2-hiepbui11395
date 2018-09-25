package cs601.project2.pubsub;

import java.util.ArrayList;

public final class SynchronousOrderedDispatchBroker<T> implements Broker<T> {

	private static SynchronousOrderedDispatchBroker<?> synchronousOrderedDispatchBroker;
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();

	private SynchronousOrderedDispatchBroker() {

	}

	public static SynchronousOrderedDispatchBroker<?> getInstance() {
		if(synchronousOrderedDispatchBroker == null) {
			synchronized (SynchronousOrderedDispatchBroker.class) {
				if(synchronousOrderedDispatchBroker == null) {
					synchronousOrderedDispatchBroker = new SynchronousOrderedDispatchBroker<Object>();
				}
			}
		}
		return synchronousOrderedDispatchBroker;
	}

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
