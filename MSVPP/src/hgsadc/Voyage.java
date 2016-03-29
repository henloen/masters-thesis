package hgsadc;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Voyage {
	
	private double cost,capacityUsed, durationHours;
	private ArrayList<Installation> visitedInstallations; // Does not contain depot at end or start
	
	public Voyage(double cost, double capacityUsed, double durationHours, ArrayList<Installation> visitedInstallations) {
		this.cost = cost;
		this.capacityUsed = capacityUsed;
		this.durationHours = durationHours;
		this.visitedInstallations = visitedInstallations;
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
	public double getDurationHours() {
		return durationHours;
	}
	
	/*
	 * Changed from the one used in voyageGenerationDP.
	 * It is NOT assumed that a voyage starts at 16:00, so the adjustment is made here in the Voyage class, rather than in
	 * the voyage construction. Therefore 8 hours is added to the duration to calculate the number of days, and not subtracted,
	 * as it is in voyageGenerationDP.
	 */
	public int getDurationDays(){
		return  (int) Math.ceil((getDurationHours() + 8) / 24);
	}
	
	public String toString() {
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		String string = "cost: " + numberFormat.format(cost) + "\t capacityUsed: " + numberFormat.format(capacityUsed) + "\t duration (hours): " + durationHours + "\t visited: ";
		for (int i = 0; i < visitedInstallations.size(); i++) {
			string += visitedInstallations.get(i).getNumber();
			if (i != (visitedInstallations.size() - 1)) {
				string+="-";
			}
		}
		return string;
	}
}
