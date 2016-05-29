package hgsadc.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;

public class FitnessEvaluationMultiObjective extends FitnessEvaluationStandard{
	
	private Dominator dominationCriteria;
	
	public FitnessEvaluationMultiObjective(ProblemData problemData) {
		super(problemData);
		dominationCriteria = problemData.dominationCriteria;
	}

	@Override
	public void updateBiasedFitness(ArrayList<Individual> individuals) {
		updateDiversityContribution(individuals);
		updateDiversityContributionRank(individuals);
		if (dominationCriteria.minimizeCost) updatePenalizedCostRank(individuals);
		if (dominationCriteria.maximizePersistence) updatePersistenceRank(individuals);
		if (dominationCriteria.maximizeRobustness) updateRobustnessRank(individuals);
		calculateBiasedFitness(individuals);
	}

	private void updateRobustnessRank(ArrayList<Individual> individuals) {
		Collections.sort(individuals, Utilities.getRobustnessComparator());
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			individual.setRobustnessRank(i+1);
		}
	}

	private void updatePersistenceRank(ArrayList<Individual> individuals) {
		Collections.sort(individuals, Utilities.getPersistenceComparator());
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			individual.setPersistenceRank(i+1);
		}
	}
	
	@Override
	protected void calculateBiasedFitness(ArrayList<Individual> individuals){
		int nIndividuals = individuals.size(); 
		double nElite = (nIndividuals * nEliteProp);
		for (Individual individual : individuals) {
			double biasedFitness = (1 - (nElite/nIndividuals)) * individual.getDiversityRank();
			
			if (dominationCriteria.minimizeCost) biasedFitness += individual.getCostRank();
			if (dominationCriteria.maximizePersistence) biasedFitness += individual.getPersistenceRank();
			if (dominationCriteria.maximizeRobustness) biasedFitness += individual.getRobustnessRank();
			individual.setBiasedFitness(biasedFitness);
		}
	}
	
}