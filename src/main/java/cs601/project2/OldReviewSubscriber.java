package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.Subscriber;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class OldReviewSubscriber implements Subscriber<Review> {
	public static void main(String[] args) {
		OldReviewSubscriber subscriber = new OldReviewSubscriber();
		SynchronousOrderedDispatchBroker<Review> broker = 
				(SynchronousOrderedDispatchBroker<Review>)SynchronousOrderedDispatchBroker.getInstance();
		broker.subscribe(subscriber);
	}

	@Override
	public void onEvent(Review item) {
		// TODO Auto-generated method stub
		
	}
}