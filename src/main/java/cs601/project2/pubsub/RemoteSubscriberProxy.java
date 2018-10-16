package cs601.project2.pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

import cs601.project2.Utils;
import cs601.project2.models.Review;

public class RemoteSubscriberProxy implements Subscriber<Review>, Runnable{

	Gson gson = new Gson();
	final String EOT = "EOT";
	
	private ArrayList<Socket> socketList = new ArrayList<Socket>();
	private ArrayList<PrintWriter> printWriterList = new ArrayList<PrintWriter>();

	
	private void parseAddress(String message) {
		String[] lines = message.split("&");
		String ip = "";
		int port = 0;
		for(String line : lines) {
			String[] set = line.split(":");
			if(set[0].equals("IP")) {
				ip = set[1];
			} else if(set[0].equals("PORT")) {
				port = Integer.parseInt(set[1]);
			}
		}
		
		//Init socket and PrintWriter to send to Remote Broker
		try {
			Socket socket = new Socket(ip, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			socketList.add(socket);
			printWriterList.add(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//Work as a server to receive data from remote broker
	private void runServer() {
		System.out.println("Subscriber server: Running ");
		try (
				ServerSocket server = new ServerSocket(Utils.SUBSCRIBERPORT);
				Socket socker = server.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(socker.getInputStream()));
				) {
			String line = br.readLine();
			while(line!=null && !line.trim().equals(EOT)) {
				parseAddress(line);
				line = br.readLine();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Subscriber server: Shutdown ");
	}
	


	@Override
	public void onEvent(Review item) {
		String itemJson = gson.toJson(item);

		for(PrintWriter out : printWriterList) {
			out.println(itemJson);
		}
	}
	
	public void closeSocket() {
		for(PrintWriter out : printWriterList) {
			out.println(EOT);
		}
		for(Socket socket : this.socketList) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		
		this.runServer();
		
		
	}
}
