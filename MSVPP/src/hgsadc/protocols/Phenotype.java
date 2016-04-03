package hgsadc.protocols;

import hgsadc.Vessel;
import hgsadc.Voyage;

import java.util.HashMap;

public interface Phenotype {
	
	public double getScheduleCost();
	
	public void setScheduleCost(double scheduleCost);

	public double getDurationViolation();

	public void setDurationViolation(double durationViolation);

	public double getCapacityViolation();
	
	public void setCapacityViolation(double capacityViolation);

	public double getNumberOfInstallationsViolation();
	
	public void setNumberOfInstallationsViolation(double numberOfInstallationsViolation);
	
	public HashMap<Integer, HashMap<Vessel, Voyage>> getGiantTour();
	
	public boolean isFeasible();

	boolean isCapacityFeasible();

	boolean isDurationFeasible();

	boolean isNumberOfInstallationsFeasible();
	
}
