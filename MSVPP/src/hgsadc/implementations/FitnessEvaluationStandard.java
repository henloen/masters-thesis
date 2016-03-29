package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.util.ArrayList;
import java.util.HashMap;

public class FitnessEvaluationStandard implements FitnessEvaluationProtocol {
	
	private ProblemData problemData;
	private double durationViolationPenalty, capacityViolationPenalty, ncloseProp;
	private HashMap<Individual, HashMap<Individual, Integer>> hammingDistances;

	public FitnessEvaluationStandard(ProblemData problemData) {
		this.problemData = problemData;
		this.hammingDistances = new HashMap<Individual, HashMap<Individual,Double>>();
		this.durationViolationPenalty = problemData.getHeuristicParameterDouble("Duration constraint violation penalty");
		this.capacityViolationPenalty = problemData.getHeuristicParameterDouble("Capacity constraint violation penalty");
		this.ncloseProp = problemData.getHeuristicParameterDouble("Proportion of individuals considered for distance evaluation"); 
	}

	@Override
	public void setPenalizedCost(Individual individual) {
		PhenotypeHGS phenotype = (PhenotypeHGS) individual.getPhenotype();
		double penalizedCost = phenotype.getScheduleCost()
				+ durationViolationPenalty*phenotype.getDurationViolation(problemData.getMinVoyageDurationHours(), problemData.getMaxVoyageDurationHours()) 
				+ capacityViolationPenalty*phenotype.getCapacityViolation();
		individual.setPenalizedCost(penalizedCost);
	}

	@Override
	public void setPenalizedCost(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			setPenalizedCost(individual);
		}
	}
	
	@Override
	public void addDistance(Individual individual) {
		HashMap<Individual, Integer> individualHammingDistances = new HashMap<Individual, Integer>();
		for (Individual existingIndividual : hammingDistances.keySet()) {
			HashMap<Individual, Integer> exisitingIndividualDistances = hammingDistances.get(existingIndividual);
			int hammingDistance = getHammingDistance(individual, existingIndividual);
			exisitingIndividualDistances.put(individual, hammingDistance);
			individualHammingDistances.put(existingIndividual, hammingDistance);
			hammingDistances.put(existingIndividual, exisitingIndividualDistances);
		}
		hammingDistances.put(individual, individualHammingDistances);
	}
	
	private int getHammingDistance(Individual individual1, Individual individual2) {
		
	}
	
	@Override
	public void updateBiasedFitness(ArrayList<Individual> individuals) {
		int nclose = (int) (individuals.size() * ncloseProp);
		
	}
	
	@Override
	public double getCapacityViolationPenalty() {
		return capacityViolationPenalty;
	}

	@Override
	public void setCapacityViolationPenalty(double penalty) {
		this.capacityViolationPenalty = penalty;
	}

	@Override
	public double getDurationViolationPenalty() {
		return durationViolationPenalty;
	}

	@Override
	public void setDurationViolationPenalty(double penalty) {
		this.durationViolationPenalty = penalty;
	}

}
