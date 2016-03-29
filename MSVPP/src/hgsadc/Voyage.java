package hgsadc;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Voyage {
	
	private double cost,capacityUsed, departureTime, slack;
	private ArrayList<Installation> visitedInstallations; // Does not contain depot at end or start
	
	public Voyage(double cost, double capacityUsed, double departureTime, double slack, ArrayList<Installation> visitedInstallations) {
		this.cost = cost;
		this.capacityUsed = capacityUsed;
		this.departureTime = departureTime;
		this.slack = slack;
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
	public double getDepartureTime() {
		return departureTime;
	}
	
	public int getDuration(){
		return (int) (getDepartureTime() - 8) / 24;
	}
	
	public String toString() {
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		String string = "cost: " + numberFormat.format(cost) + "\t capacityUsed: " + numberFormat.format(capacityUsed) + "\t departureTime: " + departureTime + "\t slack: " + slack + "\t visited: ";
		for (int i = 0; i < visitedInstallations.size(); i++) {
			string += visitedInstallations.get(i).getNumber();
			if (i != (visitedInstallations.size() - 1)) {
				string+="-";
			}
		}
		return string;
	}
}
