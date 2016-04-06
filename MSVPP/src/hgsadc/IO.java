package hgsadc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;

public class IO {
	
	private String inputFileName;
	private HashMap<String, String> problemInstanceParameters;
	private HashMap<Integer, Integer> depotCapacity;
	private HashMap<String, String> heuristicParameters;
	private int datasetSheet;
	ArrayList<Installation> installations;
	ArrayList<Vessel> vessels;
	HashMap<Installation, HashMap<Installation, Double>> distances;
	
	private HashMap<String, Installation> installationsByName; 
	
	/*note that it's assumed that the distance matrix is fully dense, i.e. you can't send in the
	 *  installation subset 1,2 and 4 because it would load the distances for 1, 2 and 3.
	 *  This can be fixed by editing getDistancesData(), but is not implemented at the moment
	 */
	
	public IO(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public ProblemData readData() {
		//the index arguments of readParameters() refer to positions in the input file: (column,row) and is 0-indexed
		problemInstanceParameters =  readParameters(1,2);
		depotCapacity = readDepotCapacity(8,2);
		heuristicParameters = readParameters(1, 15);
		datasetSheet = Integer.parseInt(problemInstanceParameters.get("Dataset sheet"));
		if (datasetSheet == 1) {
			installations = readInstallations(1,3);
			vessels = readVessels(1,23);
			distances = readDistances(1,32);
		}
		else if (datasetSheet == 2) {
			installations = readInstallations(1,3);
			vessels = readVessels(1,35);
			distances = readDistances(1,45);
		}
		return new ProblemData(problemInstanceParameters, depotCapacity, heuristicParameters, installations, vessels, distances);
	}
	
	private HashMap<String, String> readParameters(int column, int startRow) {
		int nameColumn = column; //0-indexed
		int valueColumn = column+1; //0-indexed
		HashMap<String, String> parameterHashmap = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(inputFileName));
			Sheet sheet = workbook.getSheet(0); //the parameters are expected in the first sheet
			int i = 0;
			while (sheet.getCell(valueColumn,startRow+i).getContents() != "") {
				String name = sheet.getCell(nameColumn,startRow+i).getContents();
				String value = sheet.getCell(valueColumn, startRow+i).getContents();
				parameterHashmap.put(name, value);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when reading the parameters from Excel");
		}
		return parameterHashmap;
	}
	
	private HashMap<Integer, Integer> readDepotCapacity(int column, int startRow) {
		HashMap<Integer, Integer> depotCapacities = new HashMap<Integer, Integer>();
		HashMap<String, String> stringCapacities = readParameters(column, startRow);
		for (String day : stringCapacities.keySet()) {
			depotCapacities.put(dayToInt(day), Integer.parseInt(stringCapacities.get(day)));
		}
		return depotCapacities;
	}
	
	private ArrayList<Installation> readInstallations(int startColumn, int startRow) {
		ArrayList<Installation> installations = new ArrayList<Installation>();
		ArrayList<ArrayList<String>> installationData = readTable(startColumn, startRow);
		int instNumber = 0; //start at 0, the depot is installation 0
		for (int i=0; i<getNumberOfInstallations()+1; i++) { //+1 because of the depot
			Installation installation = convertStringsToInstallation(installationData.get(i), instNumber);
			installations.add(installation);
			instNumber++;
		}
		return installations;
	}
	
	private ArrayList<Vessel> readVessels(int startColumn, int startRow) {
		ArrayList<Vessel> vessels = new ArrayList<Vessel>();
		ArrayList<ArrayList<String>> vesselData = readTable(startColumn, startRow);
		int vesselNumber = 1;
		for (ArrayList<String> row : vesselData) {
			Vessel vessel = convertStringsToVessel(row, vesselNumber);
			vessels.add(vessel);
			vesselNumber++;
		}
		return vessels;
	}
	
	private HashMap<Installation, HashMap<Installation, Double>> readDistances(int startColumn, int startRow) {
		ArrayList<ArrayList<String>> distanceData = readTable(startColumn, startRow);
		HashMap<Installation, HashMap<Installation, Double>> distances = new HashMap<Installation, HashMap<Installation,Double>>();
		ArrayList<String> headerRow = distanceData.get(0);
		setInstallationsByName();
		for (int i = 1; i<getNumberOfInstallations()+2; i++){ //+1 because of the depot and +1 because of the header row
			HashMap<Installation, Double> installationDistances = new HashMap<Installation, Double>();
			for (int j = 1; j<getNumberOfInstallations()+2; j++) {
				Installation toInstallation = getInstallationByName(headerRow.get(j));
				installationDistances.put(toInstallation, Utilities.parseDouble(distanceData.get(i).get(j)));
			}
			distances.put(getInstallationByName(distanceData.get(i).get(0)), installationDistances);
		}
		return distances;
	}
	
	private ArrayList<ArrayList<String>> readTable(int startColumn, int startRow) {
		ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(inputFileName));
			Sheet sheet = workbook.getSheet(datasetSheet); //the installation, vessel and distance data are expected in the first sheet
			int i = 0;
			while (sheet.getCell(startColumn,startRow+i).getContents() != "") { //while the next row is not empty
				ArrayList<String> row = new ArrayList<String>();
				int j = 0;
				while (sheet.getCell(startColumn+j, startRow+i).getContents() != "") {//while the cell to the right still has content
					row.add(sheet.getCell(startColumn+j, startRow+i).getContents());
					j++;
				}
				table.add(row);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when reading the problem instance data from Excel");
		}
		return table;
	}
	
	private Installation convertStringsToInstallation(ArrayList<String> row, int number) {
		double loadFactor = Utilities.parseDouble(problemInstanceParameters.get("Load factor"));
		boolean timeWindows = Boolean.parseBoolean(problemInstanceParameters.get("Time windows"));
		
		String name = row.get(0);
		double openingHour;
		double closingHour;
		if (timeWindows) {
			openingHour = Utilities.parseDouble(row.get(1));
			closingHour = Utilities.parseDouble(row.get(2));
		}
		else {
			openingHour = 0;
			closingHour = 24;
		}
		double demand = Integer.parseInt(row.get(3)) * loadFactor;
		int frequency = Integer.parseInt(row.get(4));
		double serviceTime = Utilities.parseDouble(row.get(5));
		return new Installation(name, openingHour, closingHour, demand, frequency, serviceTime, number);
	}
	
	private Vessel convertStringsToVessel(ArrayList<String> row, int number) {
		String name = row.get(0);
		int capacity = Integer.parseInt(row.get(1));
		int speed = Integer.parseInt(row.get(2));
		int unitFuelCost  = Integer.parseInt(row.get(3));
		double fuelConsumptionSailing = Utilities.parseDouble(row.get(4));
		double fuelConsumptionDepot = Utilities.parseDouble(row.get(5));
		double fuelConsumptionInstallation = Utilities.parseDouble(row.get(6));
		int timeCharterCost= Integer.parseInt(row.get(7));
		int numberOfDaysAvailable = Integer.parseInt(row.get(8));
		return new Vessel(name, capacity, speed, unitFuelCost, fuelConsumptionSailing, fuelConsumptionDepot, fuelConsumptionInstallation, timeCharterCost, numberOfDaysAvailable, number);
	}
	
	private Installation getInstallationByName(String name) {
		return installationsByName.get(name);
	}
	
	private void setInstallationsByName(){
		installationsByName = new HashMap<String, Installation>();
		for (Installation installation : installations) {
			installationsByName.put(installation.getName(), installation);
		}
	}
	
	private int getNumberOfInstallations() {
		return Integer.parseInt(problemInstanceParameters.get("Problem size"));
	}
	
	private int dayToInt(String dayString) {
		int day;
		switch (dayString) {
			case "Monday": day = 0;
				break;
			case "Tuesday": day = 1;
				break;
			case "Wednesday": day = 2;
				break;
			case "Thursday": day = 3;
				break;
			case "Friday": day = 4;
				break;
			case "Saturday": day = 5;
				break;
			case "Sunday": day = 6;
				break;
			default: day = -1;
				break;
		}
		return day;
	}
		
	
}
