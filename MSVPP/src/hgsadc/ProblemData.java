package hgsadc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ProblemData {
	
	private HashMap<String, String> problemInstanceParameters, heuristicParameters;
	private HashMap<Integer, Integer> depotCapacity; 
	private ArrayList<Installation> installations, customerInstallations;
	private ArrayList<Vessel> vessels;
	private HashMap<Installation, HashMap<Installation, Double>> distances;
	private PatternGenerator patternGenerator;
	
	private HashMap<Integer, Set<Set<Integer>>> installationDeparturePatterns, vesselDeparturePatterns;

	public ProblemData(HashMap<String, String> problemInstanceParameters,
			HashMap<Integer, Integer> depotCapacity,
			HashMap<String, String> heuristicParameters,
			ArrayList<Installation> installations, ArrayList<Vessel> vessels,
			HashMap<Installation, HashMap<Installation, Double>> distances) {
		this.problemInstanceParameters = problemInstanceParameters;
		this.depotCapacity = depotCapacity;
		this.heuristicParameters = heuristicParameters;
		this.installations = installations;
		this.vessels = vessels;
		this.distances = distances;
		setCustomerInstallations();
		this.patternGenerator = new PatternGenerator(customerInstallations);
	}
	
	public boolean isFeasible(Individual individual) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void generatePatterns() {
		int lengthOfPlanningPeriod = Integer.parseInt(problemInstanceParameters.get("Length of planning period"));
		int minDuration = Integer.parseInt(problemInstanceParameters.get("Minimum duration"));
		int maxDuration = Integer.parseInt(problemInstanceParameters.get("Maximum duration"));
		installationDeparturePatterns = patternGenerator.generateInstallationDeparturePatterns(lengthOfPlanningPeriod);
		vesselDeparturePatterns = patternGenerator.generateVesselDeparturePatterns(minDuration, maxDuration, lengthOfPlanningPeriod);
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
			System.out.println(vessel);
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
}
