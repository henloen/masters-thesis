package hgsadc.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;

public class FitnessEvaluationMultiObjective extends FitnessEvaluationStandard{

	public FitnessEvaluationMultiObjective(ProblemData problemData) {
		super(problemData);
	}

	@Override
	public void updateBiasedFitness(ArrayList<Individual> individuals) {
		updateDiversityContribution(individuals);
		updatePenalizedCostRank(individuals);
		updateDiversityContributionRank(individuals);
		updatePersistenceRank(individuals);
		calculateBiasedFitness(individuals);
	}

	private void updatePersistenceRank(ArrayList<Individual> individuals) {
		Collections.sort(individuals, Utilities.getPersistenceComparator());
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			individual.setPersistenceRank(i+1);
		}
		
	}
	private void calculateBiasedFitness(ArrayList<Individual> individuals){
		int nIndividuals = individuals.size(); 
		double nElite = (nIndividuals * nEliteProp);
		for (Individual individual : individuals) {
			double biasedFitness = individual.getCostRank() + 10*individual.getPersistenceRank() + (1 - (nElite/nIndividuals)) * individual.getDiversityRank();
			individual.setBiasedFitness(biasedFitness);
		}
	}
	
}