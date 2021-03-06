package cs601.project2;

import java.util.ArrayList;

import cs601.project2.models.Review;
import cs601.project2.pubsub.RemoteBrokerProxy;

public class RemoteBroker {
	public static void main(String[] args) {
		Utils.initValue();
		ArrayList<String> reviewFileNames = Utils.getXmlConfiguration("Review","FileName");
		if(reviewFileNames == null || reviewFileNames.isEmpty()) {
			System.out.println("Errors in configuration file, try again!");
			System.exit(1);
		}
		
		RemoteBrokerProxy<Review> remoteBroker = new RemoteBrokerProxy<Review>();
		NewReviewSubscriber subscriber = new NewReviewSubscriber();
		remoteBroker.subscribe(subscriber);
 
		Thread server = new Thread(new Runnable() {

			@Override
			public void run() {
				remoteBroker.runServer();
			}

		});
		Thread client = new Thread(new Runnable() {

			@Override
			public void run() {
				remoteBroker.runClient();
			}

		});
		server.start();
		client.start();
		try {
			server.join();
			client.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		remoteBroker.shutdown();
		Utils.closeStream(subscriber.getBw());
	}
}
