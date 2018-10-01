package cs601.project2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import cs601.project2.models.Review;
import cs601.project2.pubsub.Subscriber;

public class NewReviewSubscriber implements Subscriber<Review> {
	int count = 0;
	private BufferedWriter bw;
	
	public BufferedWriter getBw() {
		return bw;
	}
	
	public NewReviewSubscriber() {
		Path path = Paths.get("NewReviews.json");
		try {
			Files.deleteIfExists(path);
			bw = new BufferedWriter(new FileWriter(path.getFileName().toString(), true));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}  
	}

	@Override
	public void onEvent(Review item) {
		if(item.getUnixReviewTime() > 1362268800) {
			Utils.writeToFile(bw, item);
		}
	}

}
