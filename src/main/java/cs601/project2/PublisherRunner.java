package cs601.project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import cs601.project2.models.Review;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class PublisherRunner implements Runnable{
	private String url;

	public PublisherRunner(String url) {
		super();
		this.url = url;
	}

	@Override
	public void run() {
		System.out.println("Publishing");
		Gson gson = new Gson();
		Path path = Paths.get(url);
		String line = "";
		SynchronousOrderedDispatchBroker<Review> broker = 
				(SynchronousOrderedDispatchBroker<Review>)SynchronousOrderedDispatchBroker.getInstance();
		try(BufferedReader br = Files.newBufferedReader(path, Charset.forName("ISO-8859-1"))){
			while((line=br.readLine()) != null) {
				Review review = gson.fromJson(line, Review.class);
				broker.publish(review);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}