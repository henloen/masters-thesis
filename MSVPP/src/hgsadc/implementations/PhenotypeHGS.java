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

	@Override
	public double getScheduleCost() {
		return scheduleCost;
	}

	@Override
	public double getCapacityViolation() {
		return capacityViolation;
	}

	@Override
	public double getDurationViolation() {
		return durationViolation;
	}

	@Override
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

	@Override
	public boolean isFeasible() {
		return (isCapacityFeasible() && isDurationFeasible() && isNumberOfInstallationsFeasible());
	}

	@Override
	public boolean isCapacityFeasible(){
		return capacityViolation == 0;
	}

	@Override
	public boolean isDurationFeasible(){
		return durationViolation == 0;
	}

	@Override
	public boolean isNumberOfInstallationsFeasible(){
		return numberOfInstallationsViolation == 0;
	}
	
	public String toString() {
		return "Schedule cost: " + getScheduleCost();
	}

}
