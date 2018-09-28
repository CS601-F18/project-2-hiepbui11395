package cs601.project2;

import java.io.BufferedWriter;
import java.io.IOException;

import com.google.gson.Gson;

import cs601.project2.models.Review;

public class Utils {
	public static Gson gson = new Gson();
	
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
}
