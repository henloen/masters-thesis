package voyageGenerationDPold;

import java.util.ArrayList;
import java.util.HashMap;

public class Generator {
	
	
	private ArrayList<Installation> installations;
	private ArrayList<ArrayList<Label>> stages;
	private ArrayList<HashMap<Installation, HashMap<Integer, ArrayList<Label>>>> dominateLookupHashMaps;
	private ArrayList<Label> finalStates;
	private Vessel vessel;
	private int numberOfGoalNode, minDuration, maxDuration, maxNumberOfInstallations;
	private double[][] distances;
	
	@SuppressWarnings("unchecked")
	public Generator(ArrayList<Installation> installations, Vessel vessel, double[][] distances, int minDuration, int maxDuration, int maxNumberOfInstallations) {
		this.installations = (ArrayList<Installation>) installations.clone();
		this.vessel = vessel;
		this.distances = distances;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.maxNumberOfInstallations = maxNumberOfInstallations;
		
		Installation depot = this.installations.get(0);
		numberOfGoalNode = this.installations.size();
		this.installations.add(new Installation(depot.getName() + " Goal", depot.getOpeningHour(), depot.getClosingHour(), depot.getDemand(), 0, 0, numberOfGoalNode));
		stages = new ArrayList<ArrayList<Label>>();
		finalStates = new ArrayList<Label>();
		dominateLookupHashMaps = new ArrayList<HashMap<Installation, HashMap<Integer, ArrayList<Label>>>>();
	}
	
	public ArrayList<Voyage> findCheapestVoyages() {
		ArrayList<Voyage> cheapestVoyages = new ArrayList<Voyage>();
		//initialize and create initial stage
		for (int i = 0; i < installations.size(); i++) {
			stages.add(new ArrayList<Label>());
			dominateLookupHashMaps.add(new HashMap<Installation, HashMap<Integer, ArrayList<Label>>>());
		}
		Label initialLabel = new Label(1,0,0,16,0,installations.get(0),null); //departs at 16:00 and has no predecessor
		stages.get(0).add(initialLabel);
		//go through all stages and do all feasible extensions
		for (int i = 0; i < installations.size(); i++) {
			System.out.println("number of labels in stage " + i + ": " + stages.get(i).size());
			for (Label label : stages.get(i)) {
				extend(label, i);
			}
			System.out.println("done with stage " + i);
		}
		//
		for (Label finalState : finalStates) {
			cheapestVoyages.add(labelToVoyage(finalState));
		}
		return cheapestVoyages;
	}
	
	private void extend(Label label, int stage) {
		for (Installation installation: installations) {
			if (isVisited(label, installation)) {
				continue;
			}
			else if (isGoalNode(installation)) {
				Label newLabel = extendToFinalState(label, installation);
				if (newLabel != null) { //null if the extension is infeasible
					addToSet(newLabel, finalStates, dominateLookupHashMaps.get(stage+1));
				}
			}
			else {
				Label newLabel = extendToState(label, installation);
				if (newLabel != null) {//null if the extension is infeasible
					addToSet(newLabel, stages.get(stage+1), dominateLookupHashMaps.get(stage+1));
				}
			}
		}
	}
	
	private Label extendToFinalState(Label label, Installation depot) {
		Label newLabel;
		Installation currentInstallation = label.getCurrentInstallation();
		double currentDepartureTime = label.getDepartureTime();
		double currentCost = label.getCost();
		int visitedSum = label.getVisitedSum() + (int)Math.pow(2, depot.getNumber());
		double sailingTime = Math.ceil((getDistance(currentInstallation, depot)/vessel.getSpeed()));
		double arrivalTime = currentDepartureTime + sailingTime;
		double todaysOpeningHour = depot.getTodaysOpeningHour(arrivalTime);
		double timeVoyageFinished;
		double slack;
		if (arrivalTime <= todaysOpeningHour) {
			timeVoyageFinished = todaysOpeningHour;
		}
		else { //assume that the depot opens at the 
			timeVoyageFinished = todaysOpeningHour + 24;
		}
		if (timeVoyageFinished <= maxDuration && timeVoyageFinished >= minDuration) {
			double finalCost = currentCost + (sailingTime*vessel.getFuelCostSailing());
			slack = Math.floor(timeVoyageFinished - arrivalTime);
			newLabel = new Label(visitedSum, finalCost, label.getCapacityUsed(), timeVoyageFinished, slack, depot, label);
		}
		else {
			newLabel = null;
		}
		return newLabel;
	}
	
	private Label extendToState(Label label, Installation installation) {
		if (label.getVisitedInstallations().size() > maxNumberOfInstallations) {
			return null;
		}
		Installation currentInstallation = label.getCurrentInstallation();
		double currentDepartureTime = label.getDepartureTime();
		double currentCost = label.getCost();
		double sailingTime = Math.ceil((getDistance(currentInstallation, installation)/vessel.getSpeed()));
		double arrivalTime = currentDepartureTime + sailingTime;
		double todaysOpeningHour = installation.getTodaysOpeningHour(arrivalTime);
		double todaysClosingHour = installation.getTodaysClosingHour(arrivalTime);
		double waitingTime = 0;
		int visitedSum = label.getVisitedSum() + (int)Math.pow(2, installation.getNumber());
		double tomorrowsOpeningHour = todaysOpeningHour+24; //assumes same opening hours every day
		if (arrivalTime < todaysOpeningHour) {
			waitingTime = todaysOpeningHour - arrivalTime;
		}
		else if (arrivalTime >= todaysClosingHour) {
			waitingTime = tomorrowsOpeningHour - arrivalTime;
		}
		//assumption: no installations have a service time greater than one working day
		else if (arrivalTime + installation.getServiceTime() > todaysClosingHour) {
			waitingTime = tomorrowsOpeningHour - todaysClosingHour; //add a night
		}
		double nextDepartureTime = arrivalTime + installation.getServiceTime() + waitingTime;
		double nextCapacityUsed = label.getCapacityUsed() + installation.getDemandPerVisit();
		//check that the solution is feasible
		if (nextDepartureTime <= maxDuration && nextCapacityUsed <= vessel.getCapacity()) {
			double nextCost = currentCost + (sailingTime*vessel.getFuelCostSailing()) + ((waitingTime+installation.getServiceTime())*vessel.getFuelCostInstallation());
			return new Label(visitedSum, nextCost, nextCapacityUsed, nextDepartureTime, 0, installation, label);
		}
		else {
			return null;
		}
	}
	
	private void addToSet(Label label, ArrayList<Label> set, HashMap<Installation, HashMap<Integer, ArrayList<Label>>> dominateLookupHashMap) {
		ArrayList<Label> checkLabels = getCheckingLabels(label, dominateLookupHashMap);
		for (Label otherLabel : checkLabels) {
			if (dominates(otherLabel, label)) {
				return;
			}
		}
		
		//add it to the set if it's not dominated by any existing states
		set.add(label);
		//add to dominateLookupHashMap as well
		checkLabels.add(label);
		
		ArrayList<Label> dominatedLabels = new ArrayList<Label>();
		for (Label otherLabel : checkLabels) {
			if (dominates(label, otherLabel)) {
				dominatedLabels.add(otherLabel);
			}
		}
		//remove any dominated labels from the set
		set.removeAll(dominatedLabels);
		//remove from the dominateLookupHashMap as well, and since all dominated labels have the sqme current installation and visitedSum, we already have the hashmaps needed
		checkLabels.removeAll(dominatedLabels);
		//update the dominateLookupHashMap
		setCheckingLabels(label, checkLabels, dominateLookupHashMap);
	}
	
	private ArrayList<Label> getCheckingLabels(Label label, HashMap<Installation, HashMap<Integer, ArrayList<Label>>> dominateLookupHashMap){
		HashMap<Integer, ArrayList<Label>> sameCurrentInstallation = dominateLookupHashMap.get(label.getCurrentInstallation());
		ArrayList<Label> checkLabels;
		if (sameCurrentInstallation == null) {
			checkLabels = new ArrayList<Label>();
		}
		else {
			checkLabels = sameCurrentInstallation.get(label.getVisitedSum());
			if (checkLabels == null) {
				checkLabels = new ArrayList<Label>();
			}
		}
		return checkLabels;
	}
	
	private void setCheckingLabels(Label label, ArrayList<Label> checkLabels, HashMap<Installation, HashMap<Integer, ArrayList<Label>>> dominateLookupHashMap) {
		HashMap<Integer, ArrayList<Label>> sameCurrentInstallation = dominateLookupHashMap.get(label.getCurrentInstallation());
		if (sameCurrentInstallation == null) {
			sameCurrentInstallation = new HashMap<Integer, ArrayList<Label>>();
		}
		sameCurrentInstallation.put(label.getVisitedSum(), checkLabels);
		dominateLookupHashMap.put(label.getCurrentInstallation(), sameCurrentInstallation);
	}
	
	private boolean dominates(Label label, Label otherLabel) {
		if (label == otherLabel) { //a label can't dominate itself
			return false;
		}
		return ((label.getCost() <= otherLabel.getCost())
				&& (label.getDepartureTime() <= otherLabel.getDepartureTime())
			);
	}
	
	private boolean isVisited(Label label, Installation installation) {
		return (label.getVisitedSum() % Math.pow(2, (installation.getNumber()+1)) >= Math.pow(2, (installation.getNumber())));
	}
	
	private boolean isGoalNode(Installation installation) {
		return installation.getNumber() == numberOfGoalNode;
	}
	
	private Voyage labelToVoyage(Label label) {
		return new Voyage(label.getCost(), label.getCapacityUsed(), label.getDepartureTime(), label.getSlack(), label.getVisitedInstallationNumbers());
	}
	
	private double getDistance(Installation i1, Installation i2) {
		int ins1 = i1.getNumber();
		int ins2 = i2.getNumber();
		if (ins1 == numberOfGoalNode) {
			ins1 = 0;
		}
		if (ins2 == numberOfGoalNode) {
			ins2 = 0;
		}
		return distances[ins1][ins2];
	}

}
