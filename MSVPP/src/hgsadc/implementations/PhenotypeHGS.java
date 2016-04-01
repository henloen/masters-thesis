package hgsadc.implementations;

import hgsadc.ProblemData;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.Phenotype;

import java.util.HashMap;

public class PhenotypeHGS implements Phenotype {
	
	private HashMap<Integer, HashMap<Vessel, Voyage>> giantTour; //the integer is the day of departure
	private double scheduleCost, capacityViolation, durationViolation, numberOfInstallationsViolation;
	
	public PhenotypeHGS(HashMap<Integer, HashMap<Vessel, Voyage>> giantTour, HashMap<Integer, HashMap<Integer, Integer>> maxVoyageDurations, ProblemData problemData) {
		this.giantTour = giantTour;
		int minNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Minimum number of installations"));
		int maxNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Maximum number of installations"));
		int minVoyageDurationHours = problemData.getMinVoyageDurationHours();
		//maxVoyageDuration needs to be calculated dynamically
		
		scheduleCost = calculateScheduleCost();
		capacityViolation = calculateCapacityViolation();
		durationViolation = calculateDurationViolation(minVoyageDurationHours, maxVoyageDurations);
		numberOfInstallationsViolation = calculateNumberOfInstallationsViolation(minNumberOfInstallations, maxNumberOfInstallations);
		/* 
		 *the first key of maxVoyageDuration is a vessel number,
		 *the second a day and the value is the maximum duration for that vessel departing on that day
		 */
	}
	

	public double getScheduleCost() {
		return scheduleCost;
	}

	public double getCapacityViolation() {
		return capacityViolation;
	}

	public double getDurationViolation() {
		return durationViolation;
	}

	public double getNumberOfInstallationsViolation() {
		return numberOfInstallationsViolation;
	}

	private double calculateScheduleCost() {
		double cost = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					cost += giantTour.get(day).get(vessel).getCost();
				}
			}
		}
		return cost;
	}

	private double calculateCapacityViolation() {
		double capacityViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					capacityViolation += Math.max(0, voyage.getCapacityUsed() - vessel.getCapacity());
				}
			}
		}
		return capacityViolation;
	}

	private double calculateDurationViolation(int minVoyageDurationHours, HashMap<Integer, HashMap<Integer, Integer>> maxVoyageDurations) {
		double durationViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					int maxVoyageDurationHours = maxVoyageDurations.get(vessel.getNumber()).get(day);
					durationViolation += Math.max(0, Math.max(voyage.getDurationHours() - maxVoyageDurationHours, minVoyageDurationHours - voyage.getDurationHours()));;
				}
			}
		}
		return durationViolation;
	}
	
	private double calculateNumberOfInstallationsViolation(int minNumberOfInstallations, int maxNumberOfInstallations) {
		double numberOfInstallationsViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					int numberOfInstallations = voyage.getVisitedInstallations().size();
					numberOfInstallationsViolation += Math.max(0, Math.max(numberOfInstallations - maxNumberOfInstallations, minNumberOfInstallations - numberOfInstallations));
				}
			}
		}
		return numberOfInstallationsViolation;
	}
	
	public boolean isFeasible() {
		return ((capacityViolation == 0)
				&& (durationViolation == 0)
				&& (numberOfInstallationsViolation == 0));
	}
	
	public String toString() {
		return "Schedule cost: " + getScheduleCost();
	}
}
