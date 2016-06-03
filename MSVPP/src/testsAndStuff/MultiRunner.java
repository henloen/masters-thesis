package testsAndStuff;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hgsadc.HGSmain;
import jxl.Sheet;
import jxl.Workbook;

public class MultiRunner {

	String inputFile = "data/hgs/input/MultiObjectiveAutomated.xls";
	
	public static void main(String[] args) {
		MultiRunner runner = new MultiRunner();
		runner.run();
	}
	
	private void run() {
		ArrayList<HashMap<String, String>> problemInstances = readProblemInstances(1, 2);
		System.out.println(toStringAllTestInstances(problemInstances));
		
		for (HashMap<String, String> problemHashMap : problemInstances){
			int nRuns = Integer.parseInt(problemHashMap.get("Runs"));
			String[] problemArray = hashMapToStringArray(problemHashMap);
			System.out.println("=====================\nSolving problem: " + toStringProblem(problemHashMap) + "\n");
			for (int i = 0; i < nRuns; i++){
				System.out.println("Run " + (i+1));
				HGSmain.main(problemArray);
			}
		}
	}

	
	private String toStringAllTestInstances(ArrayList<HashMap<String, String>> problemInstances) {
		String str = "Solving " + problemInstances.size() + " problems:";
		for (HashMap<String, String> problem : problemInstances){
			str += "\n" + problem.get("Runs") + " runs: " + toStringProblem(problem);
		}
		return str;
	}

	private String[] hashMapToStringArray(HashMap<String, String> hashMap){
		String[] array = new String[hashMap.size()];
		
		int i = 0;
		for (Map.Entry<String, String> entry : hashMap.entrySet()) {
			String str = entry.getKey() + "=" + entry.getValue();
			array[i] = str;
			i++;
		}
		return array;
	}
	
	private ArrayList<HashMap<String, String>> readProblemInstances(int startCol, int startRow) {
		ArrayList<HashMap<String, String>> problemInstances = new ArrayList<>();
		
		try {
			Workbook workbook = Workbook.getWorkbook(new File(inputFile));
			Sheet sheet = workbook.getSheet(0); //the parameters are expected in the first sheet
			System.out.println("Opening workbook");
			ArrayList<String> headers = readHeaders(sheet, startCol, startRow); 
			
			int i = 1;
			System.out.println(sheet.getCell(startCol, startRow+i).getContents());
			while (sheet.getCell(startCol, startRow+i).getContents() != "") {
				HashMap<String, String> changeParametersForInstance = new HashMap<String, String>();
				for (int j = 0; j < headers.size(); j++){
					String value = sheet.getCell(startCol+j, startRow+i).getContents();
					String parameter = headers.get(j);
					changeParametersForInstance.put(parameter, value);
				}
				problemInstances.add(changeParametersForInstance);
				i++;
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when reading the parameters from Excel");
		}
		return problemInstances;
	}

	private ArrayList<String> readHeaders(Sheet sheet, int startCol, int row) {
		ArrayList<String> headers = new ArrayList<>();
		String cell = sheet.getCell(startCol, row).getContents();
		
		int i = 0;
		while (cell != "") {
			headers.add(cell);
			i++;
			cell = sheet.getCell(startCol+i, row).getContents();
		}
		return headers;
	}
	
	public String toStringProblem(HashMap<String, String> problem){
		String str = "Problem size: " + problem.get("Problem size");
		str += " - " + problem.get("Variation case");
		
		return str;
	}

}
