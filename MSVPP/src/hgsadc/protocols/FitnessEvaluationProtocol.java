package hgsadc.protocols;

import hgsadc.Individual;
import hgsadc.Voyage;

import java.util.ArrayList;

public interface FitnessEvaluationProtocol {

	//biased fitness is calculated for all individuals
	public void updateBiasedFitness(ArrayList<Individual> individuals);
	
	//calculate the distance to all other individuals and add the individual to the hamming distance matrix
	public void addDiversityDistance(Individual individual);
	
	//remove the individual from the hamming distance matrix
	public void removeDiversityDistance(Individual individual);
	
	//returns the hammingdistance between two individuals
	public Double getHammingDistance(Individual individual1, Individual individual2);
	
	//used to calculate the penalized cost of an individual with the provided penalty parameters  
	public void setPenalizedCostIndividual(Individual individual, double durationViolationPenalty, double capacityViolationPenalty, double numberOfInstallationsPenalty);
	
	//used to calculate the penalized cost of an individual with the global penalty parameters  
	public void setPenalizedCostIndividual(Individual individual);
	
	//returns the penalized cost of the voyage, calculated using the global penalty parameters
	public double getPenalizedCost (Voyage voyage);
	
	//returns the penalized cost of the voyage, calculated using the provided penalty parameters
	public double getPenalizedCost (Voyage voyage, double durationViolationPenalty, double capacityViolationPenalty, double numberOfInstallationsPenalty);
	
	public double getDurationViolationPenalty();
	
	public double getCapacityViolationPenalty();
	
	public double getNumberOfInstallationsPenalty();
	
	public void setDurationViolationPenalty(double durationViolationPenalty);
	
	public void setCapacityViolationPenalty(double capacityViolationPenalty);
	
	public void setNumberOfInstallationsViolationPenalty(double numberOfInstallationsViolationPenalty);

	void setPenalizedCostPopulation(ArrayList<Individual> population);
	
	
}
