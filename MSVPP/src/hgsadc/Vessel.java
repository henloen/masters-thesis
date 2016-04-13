package hgsadc;

public class Vessel implements Comparable<Vessel> {
	
	private String name;
	private int capacity, speed, unitFuelCost, timeCharterCost, numberOfDaysAvailable, number;
	private double fuelConsumptionSailing, fuelConsumptionDepot, fuelConsumptionInstallation;
	
	
	public Vessel(String name, int capacity, int speed, int unitFuelCost,
			double fuelConsumptionSailing, double fuelConsumptionDepot,
			double fuelConsumptionInstallation, int timeCharterCost, int numberOfDaysAvailable,
			int number) {
		this.name = name;
		this.capacity = capacity;
		this.speed = speed;
		this.unitFuelCost = unitFuelCost;
		this.fuelConsumptionSailing = fuelConsumptionSailing;
		this.fuelConsumptionDepot = fuelConsumptionDepot;
		this.fuelConsumptionInstallation = fuelConsumptionInstallation;
		this.timeCharterCost = timeCharterCost;
		this.numberOfDaysAvailable = numberOfDaysAvailable;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public double getFuelCostSailing() {
		return fuelConsumptionSailing*unitFuelCost;
	}
	
	public double getFuelCostDepot() {
		return fuelConsumptionDepot*unitFuelCost;
	}	

	public double getFuelCostInstallation() {
		return fuelConsumptionInstallation*unitFuelCost;
	}
	
	public int getTimeCharterCost() {
		return timeCharterCost;
	}

	public int getNumberOfDaysAvailable() {
		return numberOfDaysAvailable;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int compareTo(Vessel otherVessel) {
		return this.number - otherVessel.getNumber();
	}
	
	public String toString() {
		return "Vessel "+number;
	}
	
	public String fullText() {
		return "Name: " + name + ", number: " + number + ", capacity: " + capacity + ", speed: " + speed + ", unit fuel cost: " + unitFuelCost + 
				", fuel consumption sailing: " + fuelConsumptionSailing + ", fuel consumption depot: " + fuelConsumptionDepot +
				", fuel consumption installation: " + fuelConsumptionInstallation + ", time charter cost: " + timeCharterCost + 
				", number of days available: " + numberOfDaysAvailable;
	}

}
