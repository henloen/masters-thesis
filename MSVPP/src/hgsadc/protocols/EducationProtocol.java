package hgsadc.protocols;

import hgsadc.Individual;

public interface EducationProtocol {

	public void educate(Individual individual);

	public void repairEducate(Individual individual, int penaltyMultiplier);
	
}
