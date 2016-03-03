package ea.svpp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

// NOTE: THIS CLASS IS ONLY FOR TESTING PURPOSES

public class SVPPDeserializer {
	String inputFilePath = System.getProperty("user.dir") + "/data/ea/input/SVPP/";
	String problemInstance;
	String inputObjectFile;
	
	ProblemDataSVPP problemData;
	
	public SVPPDeserializer(String problemInstance) {
		this.problemInstance = problemInstance;
		inputObjectFile = inputFilePath + problemInstance + ".ser";
	}

	public void readObjectFromFile(){
		try {
			// read object from file
			FileInputStream fis = new FileInputStream(inputObjectFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			problemData = (ProblemDataSVPP) ois.readObject();
			ois.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		SVPPDeserializer s = new SVPPDeserializer("2016-02-24 3-0-16");
		s.readObjectFromFile();
		System.out.println("Problem data has been read from file.");
		
	}
	}
