package hgsadc.implementations;

import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.Phenotype;

import java.util.HashMap;

public class PhenotypeHGS implements Phenotype {
	
	private HashMap<Integer, HashMap<Vessel, Voyage>> giantTour; //the integer is the day of departure
	
	public PhenotypeHGS(HashMap<Integer, HashMap<Vessel, Voyage>> giantTour) {
		this.giantTour = giantTour;
	}

	public double getScheduleCost() {
		double cost = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				cost += giantTour.get(day).get(vessel).getCost();
			}
		}
		return cost;
	}

	public double getCapacityViolation() {
		double capacityViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				capacityViolation += Math.max(0, voyage.getCapacityUsed() - vessel.getCapacity());
			}
		}
		return capacityViolation;
	}

	public double getDurationViolation(int minDuration, int maxDuration) {
		double durationViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				durationViolation += Math.max(voyage.getDurationHours() - maxDuration, minDuration - voyage.getDurationHours());
			}
		}
		return durationViolation;
	}
	
	public double getNumberOfInstallationsViolation(int minInstallations, int maxInstallations) {
		double numberOfInstallationsViolation = 0;
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				int numberOfInstallations = voyage.getVisitedInstallations().size();
				numberOfInstallationsViolation += Math.max(numberOfInstallations - maxInstallations, minInstallations - numberOfInstallations);
			}
		}
		return numberOfInstallationsViolation;
	}
	
	public String toString() {
		return "Schedule cost: " + getScheduleCost();
	}
}
