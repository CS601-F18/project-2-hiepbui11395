package cs601.project2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import cs601.project2.models.Review;
import cs601.project2.pubsub.AsyncOrderedDispatchBroker;
import cs601.project2.pubsub.AsyncUnorderedDispatchBroker;
import cs601.project2.pubsub.RemoteSubscriberProxy;
import cs601.project2.pubsub.SynchronousOrderedDispatchBroker;

public class Utils {
	public static Gson gson = new Gson();
	private static String configurationUrl = "config.xml";
	public static int POLLTIME = 1;
	public static int NUMOFQUEUE = 10;
	public static int NUMOFTHREADPOOL = 10;
	public static int AWAITTIME = 5;
	public static int BROKERPORT;
	public static String SUBSCRIBERIP;
	public static int SUBSCRIBERPORT;


	/**
	 * Get value from the config file
	 *
	 */
	public static void initValue() {
		NUMOFTHREADPOOL = Integer.parseInt(Utils.getXmlConfiguration("ThreadPool", "NumberOfThreadPool").get(0));
		AWAITTIME = Integer.parseInt(Utils.getXmlConfiguration("ThreadPool", "AwaitTime").get(0));
		NUMOFQUEUE = Integer.parseInt(Utils.getXmlConfiguration("BlockingQueue", "NumberOfQueue").get(0));
		POLLTIME = Integer.parseInt(Utils.getXmlConfiguration("BlockingQueue", "PollTime").get(0));
		BROKERPORT = Integer.parseInt(Utils.getXmlConfiguration("Broker", "Port").get(0));
		SUBSCRIBERIP = Utils.getXmlConfiguration("Subscriber", "Ip").get(0);
		SUBSCRIBERPORT = Integer.parseInt(Utils.getXmlConfiguration("Subscriber", "Port").get(0));
	}

	public static void executeCommand() {
		String command = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("List of command (1-5)\n"
				+ "    1/ Synchronous Ordered\n"
				+ "    2/ Asynchronous Ordered\n"
				+ "    3/ Asynchronous Unordered\n"
				+ "    4/ Remote Subscriber\n"
				+ "    5/ Exit\n");
		while(!command.equals("5"))
		{
			System.out.print("Enter your command (1-5): ");
			ArrayList<String> reviewFileNames = Utils.getXmlConfiguration("Review","FileName");
			if(reviewFileNames == null || reviewFileNames.isEmpty()) {
				System.out.println("Errors in configuration file, try again!");
				System.exit(1);
			}
			command = sc.nextLine().toLowerCase();
			switch(command) {
			case "1": 
				Utils.syncRunner(reviewFileNames);
				break;
			case "2":
				Utils.asyncRunner(reviewFileNames);
				break;
			case "3":
				Utils.asyncUnorderedRunner(reviewFileNames);
				break;
			case "4":
				Utils.remoteRunner(reviewFileNames);
				break;
			case "5":
				break;
			default:
				System.out.println("You may enter wrong command, please try again.\n");
			}
		}
		sc.close();
		System.out.println("Thank you for using the program!");
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


		long start = System.currentTimeMillis();
		//Run publisher
		ExecutorService threadPool = Executors.newFixedThreadPool(NUMOFTHREADPOOL);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(AWAITTIME, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
		ExecutorService threadPool = Executors.newFixedThreadPool(NUMOFTHREADPOOL);
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
		ExecutorService threadPool = Executors.newFixedThreadPool(NUMOFTHREADPOOL);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(AWAITTIME, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broker.shutdown();
		Utils.closeStream(ors.getBw());
		Utils.closeStream(nrs.getBw());
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start) + "\n");
	}

	/**
	 * Publish item asynchronously without order
	 * @param reviewFileNames input file name
	 */
	private static void remoteRunner(ArrayList<String> reviewFileNames) {
		System.out.println("Async Unordered Dispatch Broker - Running");
		long start = System.currentTimeMillis();

		SynchronousOrderedDispatchBroker<Review> broker = new SynchronousOrderedDispatchBroker<Review>();

		//Run subscriber
		OldReviewSubscriber ors = new OldReviewSubscriber();
		broker.subscribe(ors);

		RemoteSubscriberProxy remoteSubscriber = new RemoteSubscriberProxy();
		broker.subscribe(remoteSubscriber);
		
		//Run subscriber server to receive broker information
		Thread remoteSubscriberThread = new Thread(remoteSubscriber);
		remoteSubscriberThread.start();
		try {
			remoteSubscriberThread.join(2000);;
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}

		//Run publisher
		ExecutorService threadPool = Executors.newFixedThreadPool(NUMOFTHREADPOOL);
		for(String fileName : reviewFileNames) {
			threadPool.execute(new PublisherRunner(fileName, broker));
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(AWAITTIME, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broker.shutdown();
		Utils.closeStream(ors.getBw());
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end-start) + "\n");
	}

	public static void writeToFile(BufferedWriter bw, Object obj) {
		try {
			String line = gson.toJson(obj);
			bw.write(line+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeStream(BufferedWriter bw) {
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//http://www.java-samples.com/showtutorial.php?tutorialid=152
	public static ArrayList<String> getXmlConfiguration(String type, String info) {
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		ArrayList<String> filesName = new ArrayList<String>();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(configurationUrl);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		if(dom!=null) {
			Element configElement = dom.getDocumentElement();
			NodeList reviewNode = configElement.getElementsByTagName(type);
			if(reviewNode != null && reviewNode.getLength() > 0) {
				for(int i = 0 ; i < reviewNode.getLength();i++) {
					Element reviewEle = (Element)reviewNode.item(i);
					NodeList fileNameNode = reviewEle.getElementsByTagName(info);
					if(fileNameNode != null && fileNameNode.getLength() > 0) {
						Element fileNameEle = (Element)fileNameNode.item(0);
						String fileNameStr = fileNameEle.getFirstChild().getNodeValue();
						filesName.add(fileNameStr);
					} else {
						return null;
					}
				}
			}
			else {
				return null;
			}
		}
		return filesName;
	}
}
