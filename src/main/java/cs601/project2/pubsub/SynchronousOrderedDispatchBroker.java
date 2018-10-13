package cs601.project2.pubsub;

import java.util.ArrayList;

public class SynchronousOrderedDispatchBroker<T> implements Broker<T> {
	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();

	public void publish(T item) {
		synchronized(subscribers) {
			subscribers.forEach(subscriber -> subscriber.onEvent(item));
		}
	}

	public void subscribe(Subscriber<T> subscriber) {
		synchronized(subscribers) {
			subscribers.add(subscriber);
		}
	}

	public void shutdown() {
		for(Subscriber<T> subscriber : subscribers) {
			if(subscriber instanceof RemoteSubscriberProxy) {
				((RemoteSubscriberProxy)subscriber).closeSocket();
			}
		}
	}

}
