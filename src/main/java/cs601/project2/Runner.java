package cs601.project2;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cs601.project2.models.Review;
import cs601.project2.pubsub.AsyncOrderedDispatchBroker;
import cs601.project2.pubsub.AsyncUnorderedDispatchBroker;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class Runner {

	public static void main(String[] args) {
		ArrayList<String> reviewFileNames = Utils.getFilesFromConfiguration("Review","FileName");
		if(reviewFileNames == null || reviewFileNames.isEmpty()) {
			System.out.println("Errors in configuration file, try again!");
			System.exit(1);
		}
		Runner.syncRunner(reviewFileNames);
		Runner.asyncRunner(reviewFileNames);
		Runner.asyncUnorderedRunner(reviewFileNames);
		
	}

	
	/**
	 * Published item synchronously and in order
	 * @param reviewFileNames input file name
	 */
	private static void syncRunner(ArrayList<String> reviewFileNames) {
		System.out.println("Synchronous Ordered Dispatch Broker - Running");
		SynchronousOrderedDispatchBroker<Review> broker = new SynchronousOrderedDispatchBroker<Review>();
		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);
		

//		RemoteSubscriberProxy remoteSubscriber = new RemoteSubscriberProxy();
//		broker.subscribe(remoteSubscriber);
//		Thread remoteSubscriberThread = new Thread(remoteSubscriber);
//		remoteSubscriberThread.start();
//		try {
//			System.out.println("Waiting for publisher!");
//			Thread.sleep(5000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}


		long start = System.currentTimeMillis();
		//Run publisher
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		try {
//			remoteSubscriberThread.join();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		broker.shutdown();
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start) + "\n");
	}

	/**
	 * Published item asynchronously and in order
	 * @param reviewFileNames input file name
	 */
	private static void asyncRunner(ArrayList<String> reviewFileNames) {
		System.out.println("Async Ordered Dispatch Broker - Running");
		long start = System.currentTimeMillis();
		AsyncOrderedDispatchBroker<Review> broker = new AsyncOrderedDispatchBroker<Review>();

		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);

		//Run publisher
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		Thread threadBroker = new Thread(broker);
		threadBroker.start();
		try {
			threadPool.awaitTermination(5, TimeUnit.MINUTES);
			//Add null to end broker
			broker.shutdown();
			threadBroker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());

		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start) + "\n");
	}

	/**
	 * Publish item asynchronously without order
	 * @param reviewFileNames input file name
	 */
	private static void asyncUnorderedRunner(ArrayList<String> reviewFileNames) {
		System.out.println("Async Unordered Dispatch Broker - Running");
		long start = System.currentTimeMillis();

		AsyncUnorderedDispatchBroker<Review> broker = new AsyncUnorderedDispatchBroker<Review>();

		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		NewReviewSubscriber nrs = new NewReviewSubscriber();
		broker.subscribe(ors);
		broker.subscribe(nrs);

		//Run publisher
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broker.shutdown();
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start) + "\n");
	}
}
