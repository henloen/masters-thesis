package ea.common;

import java.util.ArrayList;

import ea.Individual;
import ea.protocols.CrossoverOperator;
import ea.protocols.MutationOperator;
import ea.protocols.ReproductionProtocol;

public class ReproductionStandard implements ReproductionProtocol {

	private CrossoverOperator crossoverOperator;
	private MutationOperator mutationOperator;
	
	public ReproductionStandard(CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
	}


	@Override
	public ArrayList<Individual> reproduction(ArrayList<ArrayList<Individual>> parents, double crossoverRate, double mutationRate) {
		ArrayList<Individual> offspring = crossoverOperator.crossover(parents, crossoverRate);
		mutationOperator.mutate(offspring, mutationRate);
		return offspring;
	}

}
