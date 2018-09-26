package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.Broker;
import cs601.project2.pubsub.Subscriber;

public class OldReviewSubscriber implements Subscriber<Review>, Runnable {
	private int count = 0;
	private Broker<Review> broker;
	
	public OldReviewSubscriber(Broker<Review> broker) {
		this.broker = broker;
	}
	
	@Override
	public void onEvent(Review item) {
		if(item.getUnixReviewTime() < 1362268800) {
			count++;
			System.out.println("Old-" + this.count + ": "  + item.toString());
		}
	}

	@Override
	public void run() {
		this.broker.subscribe(this);
		System.out.println("Old review subscribed");
	}
}