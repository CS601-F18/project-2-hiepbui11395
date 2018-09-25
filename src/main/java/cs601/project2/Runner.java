package cs601.project2;

public class Runner {
	public static void main(String[] args) {
		Thread threadPublisher1 = new Thread(new PublisherRunner(args[1]));
		Thread threadPublisher2 = new Thread(new PublisherRunner(args[3]));
		Thread threadSubscriber1 = new Thread(new NewReviewSubscriber());
		Thread threadSubscriber2 = new Thread(new OldReviewSubscriber());
//		threadPublisher1.start();
//		threadPublisher2.start();
		threadSubscriber1.start();
		threadSubscriber2.start();
		
		try {
//			threadPublisher1.join();
//			threadPublisher2.join();
			threadSubscriber1.join();
			threadSubscriber2.join();
			Thread.sleep(1000);	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		threadPublisher1.start();
		threadPublisher2.start();
		try {
			threadPublisher1.join();
			threadPublisher2.join();
//			threadSubscriber1.join();
//			threadSubscriber2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
