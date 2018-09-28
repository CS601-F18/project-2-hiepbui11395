package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.AsyncOrderedDispatchBroker;
import cs601.project2.pubsub.AsyncUnorderedDispatchBroker;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class Runner {
	public static void main(String[] args) {
		Runner.asyncRunner(args[1], args[3]);
	}
	
	private static void syncRunner(String url1, String url2) {
		long start = System.currentTimeMillis();
		SynchronousOrderedDispatchBroker<Review> broker = new SynchronousOrderedDispatchBroker<Review>();
		
		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);
		
		//Run publisher
		Thread threadPublisher1 = new Thread(new PublisherRunner(url1, broker));
		Thread threadPublisher2 = new Thread(new PublisherRunner(url2, broker));
		//
		threadPublisher1.start();
		threadPublisher2.start();		
		try {
			threadPublisher1.join();
			threadPublisher2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start));
	}
	
	private static void asyncRunner(String url1, String url2) {
		long start = System.currentTimeMillis();
		AsyncOrderedDispatchBroker<Review> broker = new AsyncOrderedDispatchBroker<Review>();
		
		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);
		
		Thread threadPublisher1 = new Thread(new PublisherRunner(url1, broker));
		Thread threadPublisher2 = new Thread(new PublisherRunner(url2, broker));
		Thread threadBroker = new Thread(broker);
		threadBroker.start();
		threadPublisher1.start();
		threadPublisher2.start();	
		try {
			threadPublisher1.join();
			threadPublisher2.join();
			//Add null to end broker
			broker.shutdown();
			threadBroker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start));
	}
	
	private static void asyncUnorderedRunner(String url1, String url2) {
		long start = System.currentTimeMillis();
		
		AsyncUnorderedDispatchBroker<Review> broker = new AsyncUnorderedDispatchBroker<Review>();
		
		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);
		
		//Run publisher
		Thread threadPublisher1 = new Thread(new PublisherRunner(url1, broker));
		Thread threadPublisher2 = new Thread(new PublisherRunner(url2, broker));
		threadPublisher1.start();
		threadPublisher2.start();	
		try {
			threadPublisher1.join();
			threadPublisher2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broker.shutdown();
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start));
	}
}
