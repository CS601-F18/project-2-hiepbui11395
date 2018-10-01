package cs601.project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cs601.project2.pubsub.Subscriber;

public class RemoteSubscriberProxy implements Subscriber{
	
	final static String EOT = "EOT";
	final static int PORTSUBSCRIBER = 1025;
	final static int PORTBROKER = 1024;

	ArrayList<RemoteBroker> broker = new ArrayList<RemoteBroker>();
	public static void main(String[] args) {

	}

	private static void runServer() {
		try (
				ServerSocket server = new ServerSocket(PORTSUBSCRIBER);
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
				Socket socket = new Socket(InetAddress.getLocalHost(), PORTBROKER);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
				){

		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void onEvent(Object item) {
		// TODO Auto-generated method stub
		
	}
}
