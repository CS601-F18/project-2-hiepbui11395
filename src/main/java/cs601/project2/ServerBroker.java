package cs601.project2;

import cs601.project2.models.Review;
import cs601.project2.pubsub.RemoteBrokerProxy;

public class ServerBroker {
	public static void main(String[] args) {
		RemoteBrokerProxy<Review> broker = new RemoteBrokerProxy<Review>();
		Thread brokerThread = new Thread(broker);
		brokerThread.start();
		try {
			brokerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
