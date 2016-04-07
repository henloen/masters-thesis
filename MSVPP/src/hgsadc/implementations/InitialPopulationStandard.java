package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.protocols.InitialPopulationProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InitialPopulationStandard implements InitialPopulationProtocol {

	private ProblemData problemData;
	private int numberOfPatternRestarts, numberOfDepotRestarts;
	
	public InitialPopulationStandard(ProblemData problemData) {
		this.problemData = problemData;
		numberOfPatternRestarts = 0;
		numberOfDepotRestarts = 0;
	}
	
	public  Individual createIndividual(){
		HashMap<Integer, Set<Integer>> installationDepartureChromosome = createInstallationDepartureChromosome(); //the integer is the installation number and the Set<Integer> is the departure days of the installation
		HashMap<Integer, Set<Integer>> vesselDepartureChromosome = createVesselDepartureChromosome(installationDepartureChromosome); //the integer is the vessel number and the Set<Integer> is the departure days of the vessel
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = createGiantTourChromosome(installationDepartureChromosome, vesselDepartureChromosome);
		Individual individual = new Individual(new GenotypeHGS(installationDepartureChromosome, vesselDepartureChromosome, giantTourChromosome));
		//printIndividual(individual);
		return individual;
	}
	
	private void printIndividual(Individual individual) {
		System.out.println("Individual " + individual);
		System.out.println(individual.getGenotype());
		System.out.println("Number of pattern restarts: " + numberOfPatternRestarts);
		System.out.println("Number of depot restarts: " + numberOfDepotRestarts + "\n");
	}
	
	public int getNumberOfConstructionHeuristicRestarts() {
		return numberOfDepotRestarts + numberOfPatternRestarts;
	}
	
	private HashMap<Integer, Set<Integer>> createInstallationDepartureChromosome() {
		HashMap<Integer, Set<Integer>> individualInstallationDeparturePatterns = new HashMap<Integer, Set<Integer>>();
		for (Installation installation : problemData.getCustomerInstallations()) {//for each installation, choose a random installation pattern based on the frequency
			Set<Set<Integer>> possibleInstallationDeparturePatterns = problemData.getInstallationDeparturePatterns().get(installation.getFrequency());
			Set<Integer> randomInstallationDeparturePattern = Utilities.pickRandomElementFromSet(possibleInstallationDeparturePatterns);
			individualInstallationDeparturePatterns.put(installation.getNumber(), randomInstallationDeparturePattern);
		}
		return individualInstallationDeparturePatterns;
	}
	
	private HashMap<Integer, Set<Integer>> createVesselDepartureChromosome(HashMap<Integer, Set<Integer>> installationDepartureChromosome) {
		HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns = new HashMap<Integer, Set<Integer>>();
		Set<Integer> daysWithDeparture = getDaysWithDeparture(installationDepartureChromosome); //get all days that installations require a departure
		for (Vessel vessel : problemData.getVessels()) { //for each vessel, choose a random vessel pattern that fits with the installationDepartureChromosome 
			Set<Integer> randomVesselDeparturePattern = pickRandomVesselDeparturePattern(daysWithDeparture, individualVesselDeparturePatterns);
			if (randomVesselDeparturePattern==null) { //workaround until a smarter heuristic is created
				numberOfPatternRestarts++;
				return createVesselDepartureChromosome(installationDepartureChromosome);
			}
			individualVesselDeparturePatterns.put(vessel.getNumber(), randomVesselDeparturePattern);
		}
		if (isDepotConstraintViolated(individualVesselDeparturePatterns)) {
			numberOfDepotRestarts++;
			return createVesselDepartureChromosome(installationDepartureChromosome);
		}
		return individualVesselDeparturePatterns;
	}
	
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> createGiantTourChromosome(HashMap<Integer, Set<Integer>> installationDepartureChromosome, HashMap<Integer, Set<Integer>> vesselDepartureChromosome) {
		HashMap<Integer, Set<Integer>> reversedInstallationChromosome = Utilities.getReversedHashMap(installationDepartureChromosome); //the key is a period/day and the Set<Integer> is a set of installation numbers
		HashMap<Integer, Set<Integer>> reversedVesselChromosome = Utilities.getReversedHashMap(vesselDepartureChromosome); //the key is a period/day, and the Set<Integer> is a set of vessel numbers
		
		int nDays = problemData.getLengthOfPlanningPeriod();
		int nVessels = problemData.getVessels().size();
		//initializing
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = GenotypeHGS.generateEmptyGiantTourChromosome(nDays, nVessels);
		
		//allocating
		for (Integer day : reversedInstallationChromosome.keySet()) {
			Set<Integer> installationsToAllocate = reversedInstallationChromosome.get(day);
			Set<Integer> availableVessels = reversedVesselChromosome.get(day);
			HashMap<Integer, ArrayList<Integer>> vesselAllocations = giantTourChromosome.get(day); 
			for (Integer installation : installationsToAllocate) {
				Integer randomVessel = Utilities.pickRandomElementFromSet(availableVessels);
				ArrayList<Integer> existingInstallations = vesselAllocations.get(randomVessel);
				existingInstallations.add(installation);
				vesselAllocations.put(randomVessel, existingInstallations);
			}
			giantTourChromosome.put(day, vesselAllocations);
		}
		
		//shuffle sequence of visits
		for (Integer day : giantTourChromosome.keySet()) {
			for (Integer vessel : giantTourChromosome.get(day).keySet()) {
				ArrayList<Integer> installationsToVisit = giantTourChromosome.get(day).get(vessel);
				Collections.shuffle(installationsToVisit);
			}
		}
		
		return giantTourChromosome;
	}

	private Set<Integer> pickRandomVesselDeparturePattern(Set<Integer> daysWithDeparture, HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns) {
		Set<Integer> unvisitedDays = getUnvisitedDays(daysWithDeparture, individualVesselDeparturePatterns); //the set of days that installations require a departure and no vessel depart on
		int alreadyVisitedDaysAvailable = getAlreadyVisitedDaysAvailable(individualVesselDeparturePatterns, unvisitedDays); //the number of days that already have visits that the vessel pattern can contain
		Set<Integer> possibleNumberOfDepartures = getPossibleNumberOfDepartures(unvisitedDays, individualVesselDeparturePatterns, alreadyVisitedDaysAvailable); //the set of number of departures that a valid vessel pattern can have, e.g. if it's the last vessel and there are still 3 unvisited days, the vessel pattern must have 3 depatures
		int unvisitedDaysThatNeedsVisit = Collections.min(possibleNumberOfDepartures);
		int numberOfVesselDepartures = Utilities.pickRandomElementFromSet(possibleNumberOfDepartures); //select a random number from the set of possible number of departures
		Set<Set<Integer>> allPatternsForNumberOfDepartures = problemData.getVesselDeparturePatterns().get(numberOfVesselDepartures);
		Set<Set<Integer>> possibleVesselDeparturePatterns = getPossibleVesselPatterns(allPatternsForNumberOfDepartures, individualVesselDeparturePatterns, unvisitedDays, unvisitedDaysThatNeedsVisit ); //get all vessel patterns with the selected number of departures that visit enough of the unvisited installations, e.g. if it's the last vessel the pattern has to visit all unvisited installations
		if (possibleVesselDeparturePatterns.size()==0) { //workaround until a smarter heuristic is fixed
			System.out.println("No possible patterns, start over!");
			return null;
		}
		return Utilities.pickRandomElementFromSet(possibleVesselDeparturePatterns);
	}
	

	private Set<Integer> getPossibleNumberOfDepartures(Set<Integer> unvisitedDays, HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns, int alreadyVisitedDaysAvailable) {
		int maxDeparturesInPattern = Collections.max(problemData.getVesselDeparturePatterns().keySet()); //the maximum number of departures found in any vessel pattern
		int minimumNumberOfDepartures = Math.max(0,maxDeparturesInPattern - alreadyVisitedDaysAvailable); //e.g. if the patterns have max 3 visits and only 1 day that is already visited can be a part of the pattern, the minimum pattern size is 2 (since there are 2 unvisited days)
		Set<Integer> allNumberOfDepartures = problemData.getVesselDeparturePatterns().keySet();
		Set<Integer> possibleNumberOfDepartures = new HashSet<Integer>();
		for (Integer numberOfDepartures : allNumberOfDepartures) {
			if (numberOfDepartures >= minimumNumberOfDepartures) {
				possibleNumberOfDepartures.add(numberOfDepartures);
			}
		}
		return possibleNumberOfDepartures;
	}
	
	private Set<Set<Integer>> getPossibleVesselPatterns(Set<Set<Integer>> allPatternsForNumberOfDepartures, HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns, Set<Integer> unvisitedDays, int unvisitedDatsThatNeedsVisit) {
		Set<Set<Integer>> possibleVesselPatterns = new HashSet<Set<Integer>>();
		for (Set<Integer> pattern : allPatternsForNumberOfDepartures) {
			Set<Integer> unvistedDaysInPattern = new HashSet<Integer>(pattern);
			unvistedDaysInPattern.retainAll(unvisitedDays); //a set containing the days in the pattern that does not already have departures by other vessels 
			if (unvistedDaysInPattern.size() >= unvisitedDatsThatNeedsVisit) {//the pattern is added if it departs on enough of the unvisited days 
				possibleVesselPatterns.add(pattern);
			}
		}
		return possibleVesselPatterns;
	}
	
	private int getAlreadyVisitedDaysAvailable(HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns, Set<Integer> unvisitedDays) {
		int vesselsLeft = problemData.getVessels().size() - individualVesselDeparturePatterns.keySet().size(); //number of unchartered vessels
		int maxDeparturesInPattern = Collections.max(problemData.getVesselDeparturePatterns().keySet()); //the maximum number of departures found in any vessel pattern
		int alreadyVisitedDaysAvailable = (vesselsLeft*maxDeparturesInPattern - unvisitedDays.size()); //if there is 1 unchartered vessel and the patterns have maximum 3 departures and there are 2 days without departures, at most 1*3 - 2 = 1 day that already has a departure can be part of the new pattern
		return alreadyVisitedDaysAvailable;
	}
	
	private Set<Integer> getUnvisitedDays(Set<Integer> daysWithDeparture, HashMap<Integer, Set<Integer>> vesselDepartureChromosome) {
		Set<Integer> vesselVisitedDays = new HashSet<Integer>();
		for (Integer vesselNumber : vesselDepartureChromosome.keySet()) {
			vesselVisitedDays.addAll(vesselDepartureChromosome.get(vesselNumber));
		}
		Set<Integer> unvisitedDays = new HashSet<Integer>(daysWithDeparture);
		unvisitedDays.removeAll(vesselVisitedDays);
		return unvisitedDays;
	}

	private Set<Integer> getDaysWithDeparture(HashMap<Integer, Set<Integer>> installationDepartureChromosome) {
		Set<Integer> departureDays = new HashSet<Integer>();
		for (Integer installationNumber : installationDepartureChromosome.keySet()) {
			departureDays.addAll(installationDepartureChromosome.get(installationNumber));
		}
		return departureDays;
	}
	
	private boolean isDepotConstraintViolated(HashMap<Integer, Set<Integer>> individualVesselDeparturePatterns) {
		HashMap<Integer, Integer> departureCount = new HashMap<Integer, Integer>();
		for (int vesselNumber : individualVesselDeparturePatterns.keySet()) {
			Set<Integer> selectedPattern = individualVesselDeparturePatterns.get(vesselNumber);
			for (Integer day : selectedPattern) {
				Integer dayCount = departureCount.get(day);
				if (dayCount == null) {
					departureCount.put(day, 1);
				}
				else {
					departureCount.put(day, dayCount + 1);
				}
			}
		}
		HashMap<Integer, Integer> depotCapacity = problemData.getDepotCapacity();
		for (Integer day : departureCount.keySet()) {
			if (departureCount.get(day) > depotCapacity.get(day)) {
				return true;
			}
		}
		return false;
	}

	

}
