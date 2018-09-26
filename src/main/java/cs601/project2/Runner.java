package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.AsyncOrderedDispatchBroker;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class Runner {
	public static void main(String[] args) {
		Runner.asyncRunner(args[1], args[3]);
	}
	
	private static void syncRunner(String url1, String url2) {
		SynchronousOrderedDispatchBroker<Review> syncBroker = new SynchronousOrderedDispatchBroker<Review>();
		Thread threadPublisher1 = new Thread(new PublisherRunner(url1, syncBroker));
		Thread threadPublisher2 = new Thread(new PublisherRunner(url2, syncBroker));
		Thread threadOldSubscriber = new Thread(new OldReviewSubscriber(syncBroker));
		Thread threadNewSubscriber = new Thread(new NewReviewSubscriber(syncBroker));
		
		threadOldSubscriber.start();
		threadNewSubscriber.start();
		try {
			threadOldSubscriber.join();
			threadNewSubscriber.join();
			Thread.sleep(1000);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//
		threadPublisher1.start();
		threadPublisher2.start();		
		try {
			threadPublisher1.join();
			threadPublisher2.join();
			Thread.sleep(1000);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void asyncRunner(String url1, String url2) {
		AsyncOrderedDispatchBroker<Review> asyncBroker = new AsyncOrderedDispatchBroker<Review>();
		Thread threadPublisher1 = new Thread(new PublisherRunner(url1, asyncBroker));
		Thread threadPublisher2 = new Thread(new PublisherRunner(url2, asyncBroker));
		Thread threadOldSubscriber = new Thread(new OldReviewSubscriber(asyncBroker));
		Thread threadNewSubscriber = new Thread(new NewReviewSubscriber(asyncBroker));
		Thread threadBroker = new Thread(asyncBroker);
		threadBroker.start();
		threadOldSubscriber.start();
		threadNewSubscriber.start();
		threadPublisher1.start();
		threadPublisher2.start();	
		try {
			threadBroker.join();
			threadOldSubscriber.join();
			threadNewSubscriber.join();
			Thread.sleep(1000);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//	
		try {
			threadPublisher1.join();
			threadPublisher2.join();
			Thread.sleep(1000);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
