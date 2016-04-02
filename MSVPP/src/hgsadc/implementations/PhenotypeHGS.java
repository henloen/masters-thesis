package hgsadc.implementations;

import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.Phenotype;

import java.util.HashMap;

public class PhenotypeHGS implements Phenotype {
	
	private HashMap<Integer, HashMap<Vessel, Voyage>> giantTour; // the integer is the day of departure
	private double scheduleCost, capacityViolation, durationViolation, numberOfInstallationsViolation;
	
	public PhenotypeHGS(HashMap<Integer, HashMap<Vessel, Voyage>> giantTour) {
		this.giantTour = giantTour;
	}
	
	public HashMap<Integer, HashMap<Vessel, Voyage>> getGiantTour() {
		return giantTour;
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
	
	public void setScheduleCost(double scheduleCost) {
		this.scheduleCost = scheduleCost;
	}
	
	public void setDurationViolation(double durationViolation) {
		this.durationViolation = durationViolation;
	}

	public void setCapacityViolation(double capacityViolation) {
		this.capacityViolation = capacityViolation;
	}

	public void setNumberOfInstallationsViolation(double numberOfInstallationsViolation) {
		this.numberOfInstallationsViolation = numberOfInstallationsViolation;
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
