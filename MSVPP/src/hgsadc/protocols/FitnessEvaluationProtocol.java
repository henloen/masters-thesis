package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface FitnessEvaluationProtocol {

	//used to calculate the penalized cost of a individual  
	public void setPenalizedCost(Individual individual);
	
	//used to calculate the penalized cost of a population
	public void setPenalizedCost(ArrayList<Individual> individuals);
	
	//biased fitness is calculated for all individuals
	public void updateBiasedFitness(ArrayList<Individual> individuals);
	
	//calculate the distance to all other individuals and add the individual to the hamming distance matrix
	public void addDiversityDistance(Individual individual);
	
	public double getCapacityViolationPenalty();
	
	public void setCapacityViolationPenalty(double penalty);
	
	public double getDurationViolationPenalty();
	
	public void setDurationViolationPenalty(double penalty);
	
	
}
