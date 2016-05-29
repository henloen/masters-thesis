package hgsadc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import hgsadc.implementations.DayVesselCell;
import hgsadc.implementations.Dominator;

public class ProblemData {
	
	private int lengthOfPlanningPeriod;
	private HashMap<String, String> problemInstanceParameters, heuristicParameters;
	private HashMap<Integer, Integer> depotCapacity, minimumSlackForRobustness; 
	private ArrayList<Installation> installations, customerInstallations;
	private ArrayList<Vessel> vessels;
	private HashMap<Installation, HashMap<Installation, Double>> distances;
	private PatternGenerator patternGenerator;
	private HashMap<Integer, Installation> installationsByNumber;
	private HashMap<Integer, ArrayList<Installation>> installationsByFrequency;
	private HashMap<Integer, Vessel> vesselsByNumber;
	private HashMap<Integer, Set<Set<Integer>>> installationDeparturePatterns, vesselDeparturePatterns;
	private HashSet<DayVesselCell> allDayVesselCells;
	private HashMap<Integer, Set<Integer>> baselineInstallationPattern;
	
	public Dominator dominationCriteria; // Defines domination criteria. If single-objective, this is equal to null
	
	public ProblemData(HashMap<String, String> problemInstanceParameters,
			HashMap<Integer, Integer> depotCapacity,
			HashMap<String, String> heuristicParameters,
			ArrayList<Installation> installations, ArrayList<Vessel> vessels,
			HashMap<Installation, HashMap<Installation, Double>> distances,
			HashMap<Integer, Integer> minimumSlackForRobustness) {
		this.problemInstanceParameters = problemInstanceParameters;
		this.depotCapacity = depotCapacity;
		this.heuristicParameters = heuristicParameters;
		this.installations = installations;
		this.vessels = vessels;
		this.distances = distances;
		setCustomerInstallations();
		this.patternGenerator = new PatternGenerator(customerInstallations);
		setInstallationsByNumber(); //generate hashmap to easily look up installations by number
		setInstallationsByFrequency(); //generate hashmap to easily look up installations by frequency
		setVesselsByNumber(); //generate hashmap to easily look up vessels by number
		lengthOfPlanningPeriod = depotCapacity.size();
		allDayVesselCells = generateAllDayVesselCells();
		this.minimumSlackForRobustness = minimumSlackForRobustness;
		
		selectDominationCriteria();
		
		String objectives = heuristicParameters.get("Objectives");
		if (objectives.equals("Cost+Persistence")){
			baselineInstallationPattern = generateBaselineInstallationPattern(problemInstanceParameters.get("BaselineDeparturePattern"));
		}
	}
	
	public HashMap<Integer, Integer> getMinimumSlackForRobustness() {
		return minimumSlackForRobustness;
	}
	
	public Set<Installation> getInstallationsWithFrequency(int frequency){
		Set<Installation> instsWithFrequency = new HashSet<>();
		for (Installation inst : getCustomerInstallations()){
			if (inst.getFrequency() == frequency){
				instsWithFrequency.add(inst);
			}
		}
		return instsWithFrequency;
	}
	
	private HashMap<Integer, Set<Integer>> generateBaselineInstallationPattern(String baselineString) {
		HashMap<Integer, Set<Integer>> baseline = new HashMap<>();
		
//		System.out.println("Generating baselineInstallationPattern.......");
//		System.out.println("BaselineString: " + baselineString);
		
		String[] lines = baselineString.split("\n");
		for (Installation installation : getCustomerInstallations()){ 
			// NOTE: Baseline may have more or fewer installations than the current problem
			int instNum = installation.getNumber();
			
			if (instNum >= lines.length){ // Installation is not in baseline
				continue;
			}
			String[] splitLine = lines[instNum].split(" ");
			Set<Integer> installationPattern = new HashSet<>();
			
			for (int day = 0; day < getLengthOfPlanningPeriod(); day++){
				String dayInstallationDeparture = splitLine[day];
				
				if (dayInstallationDeparture.equals("1")){
					installationPattern.add(day);
				}
			}
			baseline.put(instNum, installationPattern);
		}
		
//		System.out.println("\nResulting baseline: " + baseline);
		
		return baseline;
	}
	
	public HashMap<Integer, Set<Integer>> getBaselineInstallationPattern(){
		return this.baselineInstallationPattern;
	}

	private HashSet<DayVesselCell> generateAllDayVesselCells() {
		HashSet<DayVesselCell> allDayVesselCells = new HashSet<>();
		for (int day = 0; day < lengthOfPlanningPeriod; day++){
			for (int vessel = 1; vessel <= vessels.size(); vessel++) {
				DayVesselCell cell = new DayVesselCell(day, vessel);
				allDayVesselCells.add(cell);
			}
		}
		return allDayVesselCells;
	}

	public void generatePatterns() {
		lengthOfPlanningPeriod = Integer.parseInt(problemInstanceParameters.get("Length of planning period"));
		int minDuration = Integer.parseInt(problemInstanceParameters.get("Minimum duration"));
		int maxDuration = Integer.parseInt(problemInstanceParameters.get("Maximum duration"));
		installationDeparturePatterns = patternGenerator.generateInstallationDeparturePatterns(lengthOfPlanningPeriod);
		vesselDeparturePatterns = patternGenerator.generateVesselDeparturePatterns(minDuration, maxDuration, lengthOfPlanningPeriod);
	}
	
	public void removeVessels(int removeVessels) {
		for (int i = 0; i < removeVessels; i++) {
			Vessel removedVessel = vessels.remove(vessels.size()-1);
			vesselsByNumber.remove(Integer.valueOf(removedVessel.getNumber()));
		}
	}

	public int getLengthOfPlanningPeriod() {
		return lengthOfPlanningPeriod;
	}
	
	public HashMap<String, String> getProblemInstanceParameters() {
		return problemInstanceParameters;
	}

	public HashMap<Integer, Integer> getDepotCapacity() {
		return depotCapacity;
	}

	public HashMap<String, String> getHeuristicParameters() {
		return heuristicParameters;
	}

	public ArrayList<Installation> getInstallations() {
		return installations;
	}

	public ArrayList<Vessel> getVessels() {
		return vessels;
	}

	public HashMap<Installation, HashMap<Installation, Double>> getDistances() {
		return distances;
	}
	
	public HashMap<Integer, Set<Set<Integer>>> getInstallationDeparturePatterns() {
		return installationDeparturePatterns;
	}

	public HashMap<Integer, Set<Set<Integer>>> getVesselDeparturePatterns() {
		return vesselDeparturePatterns;
	}

	public double getDistance(Installation fromInstallation, Installation toInstallation) {
		return distances.get(fromInstallation).get(toInstallation);
	}
	
	public int getHeuristicParameterInt(String parameterName) {
		return Integer.parseInt(heuristicParameters.get(parameterName));
	}
	
	public double getHeuristicParameterDouble(String parameterName) {
		return Utilities.parseDouble(heuristicParameters.get(parameterName));
	}
	
	public int getMinVoyageDurationHours() {
		int minDays = Integer.parseInt(problemInstanceParameters.get("Minimum duration"));
		return getMaxVoyageDurationHoursFromDays(minDays-1);
	}
	
	public int getMaxVoyageDurationHours() {
		int maxDays = Integer.parseInt(problemInstanceParameters.get("Maximum duration"));
		return getMaxVoyageDurationHoursFromDays(maxDays);
	}
	
	public int getMaxInstallationsPerVoyage(){
		return Integer.parseInt(getProblemInstanceParameters().get("Maximum number of installations"));
	}
	
	public int getMaxVoyageDurationHoursFromDays(int days) {
		return (days * 24) - 8;
	}
	
	public ArrayList<Installation> getCustomerInstallations() {
		return customerInstallations;
	}
	
	private void setCustomerInstallations() {
		customerInstallations = new ArrayList<Installation>(installations);
		Installation depot = null;
		for (Installation inst : installations) {
			if (inst.getNumber() == 0) {
				depot = inst;
			}
		}
		customerInstallations.remove(depot);
	}
	
	public double getAverageDemand(){
		double totalDemand = 0;
		for (Installation installation : customerInstallations) {
			totalDemand += installation.getDemand();
		}
		return totalDemand/installations.size();
	}
	
	public double getAverageDistanceBetweenInstallations(){
		double totalDistance = 0;
		int nDistances = 0;
		for (Installation inst1 : distances.keySet()){
			HashMap<Installation, Double> distancesFromInst1 = distances.get(inst1);
			for (Installation inst2 : distancesFromInst1.keySet()){
				totalDistance += distancesFromInst1.get(inst2);
			}
			nDistances += distancesFromInst1.size();
		}
		return totalDistance / nDistances;
	}
	
	public Installation getInstallationByNumber(Integer number) {
		return installationsByNumber.get(number);
	}
	
	private void setInstallationsByNumber() {
		installationsByNumber = new HashMap<Integer, Installation>();
		for (Installation installation : installations) {
			installationsByNumber.put(installation.getNumber(), installation);
		}
	}
	
	public HashMap<Integer, ArrayList<Installation>> getInstallationsByFrequency() {
		return installationsByFrequency;
	}
	
	private void setInstallationsByFrequency() {
		installationsByFrequency = new HashMap<Integer, ArrayList<Installation>>();
		for (Installation installation : customerInstallations) {
			Integer frequency = Integer.valueOf(installation.getFrequency());
			ArrayList<Installation> installations = installationsByFrequency.get(frequency);
			if (installations == null) {
				installations = new ArrayList<Installation>();
				installationsByFrequency.put(frequency, installations);
			}
			installations.add(installation);
		}
	}
	
	public Vessel getVesselByNumber(Integer vesselNumber) {
		return vesselsByNumber.get(vesselNumber);
	}
	
	public void setVesselsByNumber() {
		vesselsByNumber = new HashMap<Integer, Vessel>();
		for (Vessel vessel : vessels) {
			vesselsByNumber.put(vessel.getNumber(), vessel);
		}
	}
	
	public HashSet<DayVesselCell> getAllDayVesselCells() {
		return allDayVesselCells;
	}

	public Set<Integer> getDays() {
		return depotCapacity.keySet();
	}
	
	public void printProblemData(){
		System.out.println("Problem instance parameters");
		for (String name : problemInstanceParameters.keySet()) {
			System.out.println(name + ": " + problemInstanceParameters.get(name));
		}
		System.out.println();
		System.out.println("Depot capacity");
		for (Integer day : depotCapacity.keySet()) {
			System.out.println(day + ": " + depotCapacity.get(day));
		}
		System.out.println();
		System.out.println("Heuristic parameters");
		for (String name : heuristicParameters.keySet()) {
			System.out.println(name + ": " + heuristicParameters.get(name));
		}
		System.out.println();
		System.out.println("Installations");
		for (Installation installation : installations) {
			System.out.println(installation);
		}
		System.out.println();
		System.out.println("Vessels");
		for (Vessel vessel : vessels) {
			System.out.println(vessel.fullText());
		}
		System.out.println();
		System.out.println("Distance matrix");
		for (int i = 0; i < installations.size(); i++) {
			System.out.print(installations.get(i).getName() + ": ");
			for (int j = 0; j < installations.size(); j++) {
				System.out.print(getDistance(installations.get(i), installations.get(j)) + ", ");
			}
			System.out.println("");
		}
		System.out.println();
		System.out.println("Installation patterns");
		for (Integer freq : installationDeparturePatterns.keySet()) {
			Set<Set<Integer>> patterns = installationDeparturePatterns.get(freq);
			for (Set<Integer> pattern : patterns) {
				System.out.println(pattern);
			}
		}
		System.out.println();
		System.out.println("Vessel patterns");
		for (Integer numberOfDepartures: vesselDeparturePatterns.keySet()) {
			Set<Set<Integer>> patterns = vesselDeparturePatterns.get(numberOfDepartures);
			for (Set<Integer> pattern : patterns) {
				System.out.println(pattern);
			}
		}
		System.out.println();
	}
	
	private void selectDominationCriteria() {
		String dominationCriteriaString = getHeuristicParameters().get("Objectives");
		switch (dominationCriteriaString){
			case "Cost" : dominationCriteria = null;
				break;
			case "Cost+Persistence" :
				dominationCriteria = new Dominator(true, true);
				break;
			case "Cost+Robustness" :
				dominationCriteria = new Dominator(true, false, true);
				break;
			case "Cost+Persistence+Robustness" :
				dominationCriteria = new Dominator(true, true, true);
				break;
			default :
				dominationCriteria = null;
				break;
		}
	}
}
