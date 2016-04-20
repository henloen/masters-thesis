package hgsadc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class Voyage {
	
	private double cost,capacityUsed, duration, durationViolation, capacityViolation, numberOfInstallationsViolation;
	private ArrayList<Installation> visitedInstallations; // Does not contain depot at end or start
	private ArrayList<Integer> installations;
	private Vessel vessel;
	private int departureDay;
	private Set<Integer> vesselDeparturePattern;
	
	//the constructor needs to be rewritten if time windows are introduced
	public Voyage(ArrayList<Integer> installations, Vessel vessel, Set<Integer> vesselDeparturePattern, Integer departureDay, ProblemData problemData) {
		this.departureDay = departureDay;
		this.vessel = vessel;
		this.vesselDeparturePattern = vesselDeparturePattern;
		this.installations = installations;
		Installation fromInstallation = problemData.getInstallationByNumber(0);//the depot is installation number 0
		ArrayList<Integer> installationsIncludingDepot = new ArrayList<Integer>(installations);
		installationsIncludingDepot.add(0);
		visitedInstallations = new ArrayList<Installation>();
		for (Integer installationNumber : installationsIncludingDepot) { //the depot is not contained in the list of installations, so a for each loop can be used
			Installation toInstallation = problemData.getInstallationByNumber(installationNumber);
			double sailingTime = problemData.getDistance(fromInstallation, toInstallation)/vessel.getSpeed();
			duration += sailingTime;
			capacityUsed += toInstallation.getDemandPerVisit();
			cost += (sailingTime*vessel.getFuelCostSailing());
			if (installationNumber != 0) {
				cost += (toInstallation.getServiceTime()*vessel.getFuelCostInstallation());
				duration += toInstallation.getServiceTime();
				visitedInstallations.add(toInstallation);
			}
			fromInstallation = toInstallation;
		}
		setViolations(problemData);
	}
	
	private void setViolations(ProblemData problemData) {
		int minNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Minimum number of installations"));
		int maxNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Maximum number of installations"));
		int minVoyageDuration = problemData.getMinVoyageDurationHours();
		int maxVoyageDuration = getMaxVoyageDuration(problemData);
		setDurationViolation(minVoyageDuration, maxVoyageDuration);
		setCapacityViolation();
		setNumberOfInstallationsViolation(minNumberOfInstallations, maxNumberOfInstallations);
	}
	
	private int getMaxVoyageDuration(ProblemData problemData) {
		int maxDurationParameter = problemData.getMaxVoyageDurationHours();
		int lengthOfPlanningPeriod = problemData.getLengthOfPlanningPeriod();
		ArrayList<Integer> list = new ArrayList<Integer>(vesselDeparturePattern);
		Collections.sort(list);
		int indexOfDay = list.indexOf(departureDay);
		int distanceToNext;
		if (indexOfDay < list.size()-1) {//if the departure day is not the last day of the pattern
			distanceToNext = list.get(indexOfDay+1) - list.get(indexOfDay);
		}
		else {
			distanceToNext = (lengthOfPlanningPeriod - list.get(list.size()-1)) + list.get(0);	
		}
		return Math.min(problemData.getMaxVoyageDurationHoursFromDays(distanceToNext), maxDurationParameter);
	}
	
	public void setDurationViolation(int minVoyageDuration, int maxVoyageDuration){
		durationViolation = Math.max(0, duration - maxVoyageDuration);
		//durationViolation = Math.max(0, Math.max(duration - maxVoyageDuration, minVoyageDuration - duration));
	}
	
	public void setCapacityViolation() {
		capacityViolation = Math.max(0, capacityUsed - vessel.getCapacity());
	}
	
	public void setNumberOfInstallationsViolation(int minNumberOfInstallations, int maxNumberOfInstallations) {
		int numberOfInstallations = visitedInstallations.size();
		numberOfInstallationsViolation = Math.max(0, Math.max(numberOfInstallations - maxNumberOfInstallations, minNumberOfInstallations - numberOfInstallations));
	}
	
	public ArrayList<Installation> getVisitedInstallations(){
		return visitedInstallations;
	}
	
	public double getCost() {
		return cost;
	}
	public double getCapacityUsed() {
		return capacityUsed;
	}
	public double getDuration() {
		return duration;
	}
	
	public Vessel getVessel(){
		return vessel;
	}
	public Set<Integer> getVesselDeparturePattern() {
		return vesselDeparturePattern;
	}
 	
	public double getDurationViolation() {
		return durationViolation;
	}

	public double getCapacityViolation() {
		return capacityViolation;
	}

	public double getNumberOfInstallationsViolation() {
		return numberOfInstallationsViolation;
	}
	
	public int getDepartureDay() {
		return departureDay;
	}
	
	public ArrayList<Integer> getInstallations() {
		return installations;
	}

	/*
	 * Changed from the one used in voyageGenerationDP.
	 * It is NOT assumed that a voyage starts at 16:00, so the adjustment is made here in the Voyage class, rather than in
	 * the voyage construction. Therefore 8 hours is added to the duration to calculate the number of days, and not subtracted,
	 * as it is in voyageGenerationDP.
	 */
	public int getDurationDays(){
		return  (int) Math.ceil((getDuration() + 8) / 24);
	}
	
	public String toString() {
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		String string = "cost: " + numberFormat.format(cost) + "\t capacityUsed: " + numberFormat.format(capacityUsed) + "\t duration (hours): " + duration + "\t visited: ";
		for (int i = 0; i < visitedInstallations.size(); i++) {
			string += visitedInstallations.get(i).getNumber();
			if (i != (visitedInstallations.size() - 1)) {
				string+="-";
			}
		}
		return string;
	}
}
