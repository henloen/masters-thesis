package hgsadc.implementations;

public class DayVesselCell {
	
	public final int day;
	public final int vessel;
	
	public DayVesselCell(int day, int vessel) {
		if (day < 0 || vessel < 1){
			throw new IllegalArgumentException("Day: " + day + ", PSV: " + vessel + " is an invalid DayVesselCell");
		}
		
		this.day = day;
		this.vessel = vessel;
	}

	@Override
	public int hashCode() {
		return day*10 + vessel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DayVesselCell){
			return ((DayVesselCell) obj).day == this.day && ((DayVesselCell) obj).vessel == this.vessel;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + day + ", " + vessel + ")";
	}
	
	
	
	
}
