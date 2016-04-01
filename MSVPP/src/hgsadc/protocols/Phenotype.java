package hgsadc.protocols;

public interface Phenotype {
	
	public double getScheduleCost();
	
	public void setScheduleCost(double scheduleCost);

	public double getDurationViolation();

	public void setDurationViolation(double durationViolation);

	public double getCapacityViolation();
	
	public void setCapacityViolation(double capacityViolation);

	public double getNumberOfInstallationsViolation();
	
	public void setNumberOfInstallationsViolation(double numberOfInstallationsViolation);
	
	public boolean isFeasible();
	
}
