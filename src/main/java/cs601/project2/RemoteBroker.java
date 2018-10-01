package cs601.project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cs601.project2.pubsub.Broker;
import cs601.project2.pubsub.Subscriber;

public class RemoteBroker<T> implements Broker<T>{

	final static String EOT = "EOT";
	final static int PORTSUBSCRIBER = 1025;
	final static int PORTBROKER = 1024;
	
	@Override
	public void publish(T item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(Subscriber<T> subscriber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	private static void runServer() {
		try (
				ServerSocket server = new ServerSocket(PORTBROKER);
				Socket socker = server.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(socker.getInputStream()));
				) {
			String message = "";
			String line = br.readLine();
			while(line!=null && !line.trim().equals(EOT)) {
				message += line + "\n";
				line = br.readLine();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void runClient() {
		try (
				Socket socket = new Socket(InetAddress.getLocalHost(), PORTSUBSCRIBER);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
				){

		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
