package hgsadc;

public class Installation {
	
	private String name;
	private int number, frequency;
	private double demand, openingHour, closingHour, serviceTime;
	
	public Installation(String name, double openingHour,
			double closingHour, double demand, int frequency, double serviceTime, int number) {
		this.name = name;
		this.serviceTime = serviceTime;
		this.openingHour = openingHour;
		this.closingHour = closingHour;
		this.number = number;
		this.demand = demand;
		this.frequency = frequency;
	}

	public double getDemandPerVisit(){
		return demand/frequency;
	}
	
	public String getName() {
		return name;
	}

	public double getDemand() {
		return demand;
	}

	public int getFrequency() {
		return frequency;
	}

	public double getServiceTime() {
		return serviceTime;
	}

	public double getOpeningHour() {
		return openingHour;
	}


	public double getClosingHour() {
		return closingHour;
	}

	public int getNumber() {
		return number;
	}
	
	public String toString() {
		return "Name: " + name + ", number: " + number + ", service time: " + serviceTime + ", opening hour: " + openingHour +
				", closing hour: " + closingHour + ", demand: " + demand + ", frequency: " + frequency;
	}

}
