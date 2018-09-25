package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.Subscriber;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class OldReviewSubscriber implements Subscriber<Review>, Runnable {

	@Override
	public void onEvent(Review item) {
		System.out.println("Old: "  + item.toString());
	}

	@Override
	public void run() {
		OldReviewSubscriber subscriber = new OldReviewSubscriber();
		SynchronousOrderedDispatchBroker<Review> broker = 
				(SynchronousOrderedDispatchBroker<Review>)SynchronousOrderedDispatchBroker.getInstance();
		broker.subscribe(subscriber);
		System.out.println("Old review subscribed");
	}
}