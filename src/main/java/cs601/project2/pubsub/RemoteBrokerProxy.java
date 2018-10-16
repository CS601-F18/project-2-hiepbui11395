package cs601.project2.pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import cs601.project2.Utils;
import cs601.project2.models.Review;

public class RemoteBrokerProxy<T> implements Broker<T> {

	Gson gson = new Gson();

	private ArrayList<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();
	ExecutorService threadPool = Executors.newFixedThreadPool(Utils.NUMOFTHREADPOOL);
	final static String EOT = "EOT";


	@Override
	public void publish(T item) {
		threadPool.execute(new Runnable() {
			public void run() {
				T itemBeSent = item;
				subscribers.forEach(subscriber -> subscriber.onEvent(itemBeSent));
			}
		});
	}

	@Override
	public void subscribe(Subscriber<T> subscriber) {
		synchronized(subscribers) {
			subscribers.add(subscriber);
		}
	}

	@Override
	public void shutdown() {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	//Work as a server to receive data from remote subscriber
	@SuppressWarnings("unchecked")
	public void runServer() {
		System.out.println("Broker Server: Running");
		try (
				ServerSocket server = new ServerSocket(Utils.BROKERPORT);
				Socket socker = server.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(socker.getInputStream()));
				) {
			String line = br.readLine();
			while(line!=null && !line.trim().equals(EOT)) {
				Review review = gson.fromJson(line, Review.class);
				this.publish((T) review);
				line = br.readLine();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Broker Server: Shutdown");
	}

	//Work as a client to send information to the remote subscriber
	public void runClient() {
		System.out.println("Broker Client: Send information to remote subscriber!");
		try (
//				Socket socket = new Socket("10.0.1.150", PORTSUBSCRIBER);
				Socket socket = new Socket(Utils.SUBSCRIBERIP, Utils.SUBSCRIBERPORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
				){
			out.println("IP:" + InetAddress.getLocalHost().getHostAddress() 
					+ "&PORT:" + Utils.BROKERPORT);
			//print the end of transmission token
			out.println("EOT");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Broker Client: Shutdown");
	}
}
