package cs601.project2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class Utils {
	public static Gson gson = new Gson();
	private static String configurationUrl = "config.xml";
	
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
	public static ArrayList<String> getFilesFromConfiguration(String type, String info) {
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
