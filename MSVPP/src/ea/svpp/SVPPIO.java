package ea.svpp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;

import voyageGenerationDP.SVPPProblemData;

public class SVPPIO {
	String problemInstance;
	String baselineInstance;
	
	String inputFilePath = "data/input/";
	String baselineFilePath = "data/input/baseline";
	
	public SVPPProblemData problemData;
	
	private HashMap<String, String> problemSpecificParameters;
	
	public SVPPIO(HashMap<String, String> optionalParameterHashMap) {
		this.problemInstance = optionalParameterHashMap.get("Problem instance");
		this.baselineInstance = optionalParameterHashMap.get("Baseline instance");
	}
	
	public void deserializeSVPPProblemData(){
		String inputObjectFile = inputFilePath + problemInstance + ".ser";
		try {
			// read object from file
			FileInputStream fis = new FileInputStream(inputObjectFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			problemData = (SVPPProblemData) ois.readObject();
			ois.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, String> readProblemSpecificParameters(){
		readInputFile();
		readBaselineFile();
		
		return problemSpecificParameters;
	}

	private void readBaselineFile() {
		String baselineFile = baselineFilePath + baselineInstance + ".txt";
		try{
			Scanner sc = new Scanner(baselineFile);
			
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				
				if (line.substring(0, 13).equals("BaselineSigma")){
					String parameter = "BaselineSigma";
					String value = line.substring(13);
					
					String valueLine;
					
					do {
						valueLine = sc.nextLine();
						value += valueLine;
					}
					while(!valueLine.equals("]"));
					
					problemSpecificParameters.put(parameter, value);
				}
			}
		}catch (Exception e){
			System.out.println("Something went wrong when reading baseline file " + baselineFile);
		}
	}
 	private void readInputFile() {
		String inputFile = inputFilePath + problemInstance + ".txt";
		try {
			Scanner sc = new Scanner(inputFile);
			
			while (sc.hasNextLine()){
				String line = sc.nextLine();
				
				if (line.equals("") || line.substring(0, 1).equals("!") || line.substring(0, 1).equals(" ")){
					continue;
				}
				else {
					String[] splitLine = line.split(": ");
					String parameter = splitLine[0];
					String value = splitLine[1].trim();
					
					if (!value.equals("[")){
						problemSpecificParameters.put(parameter, value);
					}
					else {
						String valueLine;
						do {
							valueLine = sc.nextLine();
							value += valueLine;
						}
						while(!valueLine.equals("]"));
						
						problemSpecificParameters.put(parameter, value);
					}
				}
			}
		}	
		catch (Exception e){
			System.out.println("Something went wrong when reading input file " + inputFile);
		}
	}
	
	
	
}
