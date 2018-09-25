package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.Subscriber;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class NewReviewSubscriber implements Subscriber<Review>, Runnable {

	@Override
	public void onEvent(Review item) {
		System.out.println("New:" + item.toString());
	}

	@Override
	public void run() {
		NewReviewSubscriber subscriber = new NewReviewSubscriber();
		SynchronousOrderedDispatchBroker<Review> broker = 
				(SynchronousOrderedDispatchBroker<Review>)SynchronousOrderedDispatchBroker.getInstance();
		broker.subscribe(subscriber);

		System.out.println("New review subscribed");
	}
}
