package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface FitnessEvaluationProtocol {

	//used to calculate the penalized cost of a individual  
	public void setPenalizedCost(Individual individual);
	
	//used to calculate the penalized cost of a population
	public void setPenalizedCost(ArrayList<Individual> individuals);
	
	//biased fitness is calculated based on all the other individuals
	public void updateBiasedFitness(ArrayList<Individual> individuals);
	
	//calculate the distance to all other individuals, and update the hamming distance matrix
	public void addDistance(Individual individual);
	
	public double getCapacityViolationPenalty();
	
	public void setCapacityViolationPenalty(double penalty);
	
	public double getDurationViolationPenalty();
	
	public void setDurationViolationPenalty(double penalty);
	
	
}
