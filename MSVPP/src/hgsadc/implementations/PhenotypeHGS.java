package hgsadc.implementations;

import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.Phenotype;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public String getScheduleString() {
		String str = "";
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		ArrayList<Integer> days = new ArrayList<Integer>(giantTour.keySet());
		Collections.sort(days);
		for (Integer day : days) {
			str += "Day: " + day + " - ";
			ArrayList<Vessel> vessels = new ArrayList<Vessel>(giantTour.get(day).keySet());
			Collections.sort(vessels);
			for (int i = 0; i < vessels.size(); i++) {
				str += "vessel " + vessels.get(i).getNumber() + ": ";
				Voyage voyage = giantTour.get(day).get(vessels.get(i));
				if (voyage == null) {
					str += "-";
				}
				else {
					str += voyage.getInstallations()
							+ " cost: " + numberFormat.format(voyage.getCost())
							+ " dur: " + numberFormat.format(voyage.getDuration())
							+ " cap: " + numberFormat.format(voyage.getCapacityUsed());
				}
				if (i < vessels.size()-1) { //don't add a comma after the last vessel
					str += ", ";
				}
			}
			str += "\n";
		}
		return str;
	}

}
