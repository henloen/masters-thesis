package hgsadc.protocols;

public interface Phenotype {
	
	public double getScheduleCost();

	public double getDurationViolation();

	public double getCapacityViolation();

	public double getNumberOfInstallationsViolation();
	
	public boolean isFeasible();

}
