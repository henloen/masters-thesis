package voyageGenerationDP;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Voyage implements Comparable<Voyage>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private double cost,capacityUsed, departureTime, slack;
	private int number;
	private ArrayList<Integer> visited;
	private ArrayList<Installation> visitedInstallations; // Does not contain depot at end or start
	private static int numberOfVoyages= 0;
	
	public Voyage(double cost, double capacityUsed, double departureTime, double slack, ArrayList<Integer> visited) {
		super();
		this.cost = cost;
		this.capacityUsed = capacityUsed;
		this.departureTime = departureTime;
		this.slack = slack;
		this.visited = visited;
		
		visitedInstallations = new ArrayList<>();
		for (int i = 1; i < visited.size()-1; i++) { // Does not contain depot at end or start
			int instNum = visited.get(i);
			visitedInstallations.add(VGMain.installations.get(instNum));
		}
		numberOfVoyages++;
		this.number = numberOfVoyages;
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
	public int getNumber() {
		return number;
	}
	public double getSlack() {
		return slack;
	}
	public ArrayList<Integer> getVisited() {
		return visited;
	}
	
	public static int getNumberOfVoyages() {
		return numberOfVoyages;
	}
	public int getDuration(){
		return (int) (getDepartureTime() - 8) / 24;
	}
	
	public String getFullText() {
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		String string = "number: " + number + "\t cost: " + numberFormat.format(cost) + "\t capacityUsed: " + numberFormat.format(capacityUsed) + "\t departureTime: " + departureTime + "\t slack: " + slack + "\t visited: ";
		for (int i = 0; i < visited.size(); i++) {
			string += visited.get(i);
			if (i != (visited.size() - 1)) {
				string+="-";
			}
		}
		return string;
	}
	
	public String toString() {
		return ""+number;
	}

	public int compareTo(Voyage o) {
		return this.number - o.number;
	}

}
