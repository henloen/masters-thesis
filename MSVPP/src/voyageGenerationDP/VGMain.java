package voyageGenerationDP;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VGMain {
	
	
	private static ArrayList<Installation> installations;
	private static ArrayList<Vessel> vessels;
	private static double[][] distances;
	private static ArrayList<ArrayList<Vessel>> vesselSets;
	private static HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency;
	private static ArrayList<Voyage> voyageSet;
	private static HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel;
	private static HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation;
	private static HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration;
	private static HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack;
	private static IO io;
	private static long startTime, stopTime;
	private static String inputFileName = "data/voyageGeneration/input/Input data.xls",
			outputFileName = "data/voyageGeneration/output/"; //sets the folder, see the constructor of IO for the filename format
	//heuristic parameters
	private static int removeLongestArcs = 0, minInstallationsHeur = 0, maxArcsRemovedInstallation = 2;
	private static double capacityFraction = 0;
	
	public static void main(String[] args) {
		startTime = System.nanoTime();
		installationSetsByFrequency = new HashMap<Integer, ArrayList<Installation>>(); //initialize the set of installation for each frequency, Nf
		voyageSet = new ArrayList<Voyage>();
		voyageSetByVessel = new HashMap<Vessel, ArrayList<Voyage>>(); //initialize the voyage set, R_v in the mathematical model
		voyageSetByVesselAndInstallation = new HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>>(); //initialize the voyage set indexed by both vessel and installation, R_vi in the mathematical model
		voyageSetByVesselAndDuration = new HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>>(); //initialize the voyage set indexed by both vessel and duration, R_vl in the mathematical model
		voyageSetByVesselAndDurationAndSlack = new HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>>(); //initialize the voyage set indexed by vessel, duration and slack, R_vls in the mathematical model
		
		getData();
		generateVesselSets();
		for (ArrayList<Vessel> vesselSet : vesselSets) {
			generateVoyageSetsByVessel(vesselSet);
		}
		generateVoyageSet();
		filterByHeuristics(); //filters voyageSetByVessel by reducing the number of voyages
		generateVoyageSetsByVesselAndInstallation();
		generateVoyageSetsByVesselAndDuration();
		generateVoyageSetsByVesselAndDurationAndSlack();
		generateInstallationSetsByFrequency();
		
		//printVoyages(); //helper function to see voyages
		//printSizeOfSets(); //helper function that prints the size of the differnt sets
		
		stopTime = System.nanoTime();
		io.writeOutputToDataFile(installations, vessels, voyageSet, voyageSetByVessel, voyageSetByVesselAndInstallation, voyageSetByVesselAndDuration, voyageSetByVesselAndDurationAndSlack, installationSetsByFrequency, stopTime - startTime, removeLongestArcs, minInstallationsHeur, capacityFraction); //stopTime-startTime equals the execution time of the program
		
		SVPPProblemData problemData = new SVPPProblemData(installations, vessels, distances, vesselSets, installationSetsByFrequency, voyageSet, voyageSetByVessel, voyageSetByVesselAndInstallation, voyageSetByVesselAndDuration, voyageSetByVesselAndDurationAndSlack);
		//io.serializeProblemInstance(problemData);
	}	
	
	//get data from input file
	private static void getData() {
		io = new IO(inputFileName, outputFileName);
		installations = io.getInstallations();
		vessels = io.getVessels();
		distances = io.getDistances();
	}
	
	//generate sets of vessels with equal properties - vessels that can use the same voyages
	private static void generateVesselSets() {
		Set<Vessel> allVessels = new HashSet<Vessel>(vessels); //make a set containing all vessels
		vesselSets = new ArrayList<ArrayList<Vessel>>(); //make the double arraylist containing the vesselSets
		while (! allVessels.isEmpty()) {
			ArrayList<Vessel> tempSet = new ArrayList<Vessel>();
			Vessel nextVessel = allVessels.iterator().next();
			tempSet.add(nextVessel);
			for (Vessel vessel : allVessels) {
				if ( (nextVessel != vessel)
						&& (nextVessel.getSpeed() == vessel.getSpeed())
						&& (nextVessel.getFuelCostDepot() == vessel.getFuelCostDepot())
						&& (nextVessel.getFuelCostInstallation() == vessel.getFuelCostInstallation())
						&& (nextVessel.getFuelCostSailing() == vessel.getFuelCostSailing())) {
					tempSet.add(vessel);
				}
			}
			allVessels.removeAll(tempSet);
			Collections.sort(tempSet);//sort by capacity, largest capacity first;
			vesselSets.add(tempSet);
		}
	}
	
	private static void generateVoyageSetsByVessel(ArrayList<Vessel> vesselSet) {
		Vessel maxVessel = vesselSet.get(0); //the first vessel in a vesselset has the highest capacity
		Generator generator = new Generator(installations, maxVessel, distances, io.getMinDuration(), io.getMaxDuration(), io.getMaxNumberOfInstallations());
		ArrayList<Voyage> cheapestVoyages = generator.findCheapestVoyages();
		voyageSetByVessel.put(maxVessel, cheapestVoyages);
		
		for (int i = 1; i < vesselSet.size(); i++) {
			Vessel tempVessel = vesselSet.get(i);
			ArrayList<Voyage> tempVoyages = new ArrayList<Voyage>();
			for (Voyage voy : cheapestVoyages) {
				if (voy.getCapacityUsed() <= tempVessel.getCapacity()) {
					tempVoyages.add(voy);
				}
			}
			voyageSetByVessel.put(tempVessel, tempVoyages);
		}
				
	}
	
	private static void generateVoyageSet() {
		for (Vessel vessel : voyageSetByVessel.keySet()) {
			for (Voyage voyage : voyageSetByVessel.get(vessel)) {
				if (! voyageSet.contains(voyage)) {
					voyageSet.add(voyage);
				}
			}
		}
		Collections.sort(voyageSet);
	}
	
	private static void generateVoyageSetsByVesselAndInstallation() {
		for (Vessel vessel : voyageSetByVessel.keySet()) {
			HashMap<Installation, ArrayList<Voyage>> voyageSetByInstallation = new HashMap<Installation, ArrayList<Voyage>>();
			for (Installation i : installations) {
				voyageSetByInstallation.put(i, new ArrayList<Voyage>());
			}
			List<Voyage> voyages = voyageSetByVessel.get(vessel);//all voyages for a vessel 
			for (Voyage voyage : voyages) { //loop through all voyages for the vessel
				for (int i = 0; i < (voyage.getVisited().size()-1); i++) { //loop through all installations visited in the voyage except the last one (depot)
					Installation tempInst = installations.get(voyage.getVisited().get(i));
					ArrayList<Voyage> tempVoyageList = voyageSetByInstallation.get(tempInst); //get the current list of voyages for the combination of installation and vessel
					tempVoyageList.add(voyage); //add the new voyage to the list of voyages for that combination of installation and vessel
					voyageSetByInstallation.put(tempInst, tempVoyageList); //change the list of voyages for the combination of installation and vessel
				}
			}
			voyageSetByVesselAndInstallation.put(vessel, voyageSetByInstallation);
		}
	}
	
	private static void generateVoyageSetsByVesselAndDuration() {
		for (Vessel vessel : voyageSetByVessel.keySet()) {
			HashMap<Integer, ArrayList<Voyage>> voyageSetByDuration = new HashMap<Integer, ArrayList<Voyage>>();
			for (int i = io.getMinDuration(); i <= io.getMaxDuration(); i+=24) { //instantiate arraylist for all durations from min to max duration
				voyageSetByDuration.put(((i-8)/24), new ArrayList<Voyage>());
			}
			
			ArrayList<Voyage> voyages = voyageSetByVessel.get(vessel);//all voyages for a vessel 
			for (Voyage voyage : voyages) { //loop through all voyages for the vessel
				Integer tempDuration = (int) (voyage.getDepartureTime() - 8) / 24; //have to cast from double to int 
				ArrayList<Voyage> tempVoyageList = voyageSetByDuration.get(tempDuration); //get the current list of voyages for the combination of duration and vessel
				tempVoyageList.add(voyage); //add the new voyage to the list of voyages for that combination of installation and vessel
				voyageSetByDuration.put(tempDuration, tempVoyageList); //change the list of voyages for the combination of installation and vessel
			}
			voyageSetByVesselAndDuration.put(vessel, voyageSetByDuration);
		}
	}
	
	private static void generateVoyageSetsByVesselAndDurationAndSlack() {
		for (Vessel vessel : voyageSetByVesselAndDuration.keySet()) {
			HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>> voyageSetByDurationAndSlack = new HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>();
			for (Integer duration : voyageSetByVesselAndDuration.get(vessel).keySet()) {
				HashMap<Integer, ArrayList<Voyage>> voyageSetBySlack = new HashMap<Integer, ArrayList<Voyage>>();
				for (int i = 0; i < 24; i++) {
					voyageSetBySlack.put(i, new ArrayList<Voyage>());
				}
				ArrayList<Voyage> voyages = voyageSetByVesselAndDuration.get(vessel).get(duration); //all voyages for a vessel and a duration
				for (Voyage voyage : voyages) {
					Integer tempSlack = (int) voyage.getSlack();
					ArrayList<Voyage> tempVoyageList = voyageSetBySlack.get(tempSlack);
					/*if (tempVoyageList == null) {//if no arraylist exists for that slack
						tempVoyageList = new ArrayList<Voyage>();
					}*/
					tempVoyageList.add(voyage);
					voyageSetBySlack.put(tempSlack,tempVoyageList);
				}
				voyageSetByDurationAndSlack.put(duration, voyageSetBySlack);
			}
			voyageSetByVesselAndDurationAndSlack.put(vessel, voyageSetByDurationAndSlack);
		}
	}
	
	private static void generateInstallationSetsByFrequency() {
		int minFrequency = Integer.MAX_VALUE; //any frequency will be lower than this
		int maxFrequency = Integer.MIN_VALUE; //any frequency will be higher than this
		for (int i = 1; i < installations.size(); i++) { //starts at 1 to ignore the frequency of the depot
			Installation installation = installations.get(i);
			int frequency = installation.getFrequency();
			if (frequency < minFrequency) {
				minFrequency = frequency;
			}
			if (frequency > maxFrequency) {
				maxFrequency = frequency;
			}
		}
		for (int f = minFrequency; f <= maxFrequency; f++) {
			ArrayList<Installation> installationList = new ArrayList<>();
			for (int i = 1; i < installations.size(); i++) {//starts at 1 to ignore the frequency of the depot
				if (installations.get(i).getFrequency() == f) {
					installationList.add(installations.get(i));
				}
			}
			installationSetsByFrequency.put(f, installationList);
		}
	}
	
	private static void filterByHeuristics() {
		Heuristics heuristics = new Heuristics(distances, installations);
		if (removeLongestArcs > 0) {
			heuristics.removeLongestDistancePairs(removeLongestArcs, maxArcsRemovedInstallation, voyageSetByVessel);
		}
		if (minInstallationsHeur > 0) {
			heuristics.minInstallationsHeur(minInstallationsHeur, voyageSetByVessel);
		}
		if (capacityFraction > 0) {
			heuristics.minCapacityUsed(capacityFraction, voyageSetByVessel);
		}
	}
	
	private static void printVoyages() {
		for (Voyage v : voyageSet) {
			System.out.println("Voyage duration: " + v.getDepartureTime() + ", Slack: " + v.getSlack());
		}
		for (Vessel vessel : voyageSetByVesselAndDurationAndSlack.keySet()) {
			for (Integer duration : voyageSetByVesselAndDurationAndSlack.get(vessel).keySet()) {
				for (Integer slack: voyageSetByVesselAndDurationAndSlack.get(vessel).get(duration).keySet()) {
					ArrayList<Voyage> voyages = voyageSetByVesselAndDurationAndSlack.get(vessel).get(duration).get(slack);
					System.out.println("Size: " + voyages.size());
					for (Voyage voyage : voyages) {
						System.out.println("Voyage: " + voyage.getFullText());
					}
				}
			}
		}
	}
	
	private static void printSizeOfSets() {
		int sizeOfVoyageSet = voyageSet.size();
		int sizeOfVoyageSetByVessel = 0;
		int sizeOfVoyageSetByVesselAndDuration = 0;
		int sizeOfVoyageSetByVesselAndDurationAndSlack = 0;
		for (Vessel v : voyageSetByVessel.keySet()) {
			sizeOfVoyageSetByVessel += voyageSetByVessel.get(v).size();
		}
		for (Vessel v : voyageSetByVesselAndDuration.keySet()) {
			for (Integer duration : voyageSetByVesselAndDuration.get(v).keySet())
			sizeOfVoyageSetByVesselAndDuration += voyageSetByVesselAndDuration.get(v).get(duration).size();
		}
		for (Vessel v : voyageSetByVesselAndDurationAndSlack.keySet()) {
			for (Integer duration : voyageSetByVesselAndDurationAndSlack.get(v).keySet())
				for (Integer slack : voyageSetByVesselAndDurationAndSlack.get(v).get(duration).keySet()) {
					sizeOfVoyageSetByVesselAndDurationAndSlack += voyageSetByVesselAndDurationAndSlack.get(v).get(duration).get(slack).size();
				}
		}
		System.out.println("VoyageSet:"  + sizeOfVoyageSet);
		System.out.println("VoyageSetByVessel:"  + sizeOfVoyageSetByVessel);
		System.out.println("VoyageSetByVesselAndDuration:"  + sizeOfVoyageSetByVesselAndDuration);
		System.out.println("VoyageSetByVesselAndDurationAndSlack:"  + sizeOfVoyageSetByVesselAndDurationAndSlack);
	}
	
	
}
