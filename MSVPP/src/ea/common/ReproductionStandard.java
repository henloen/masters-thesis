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
	public ArrayList<Individual> reproduce(ArrayList<ArrayList<Individual>> parents, double crossoverRate, double mutationRate) {
		ArrayList<Individual> offspring;
		if (crossoverOperator != null){
			offspring = crossoverOperator.crossover(parents, crossoverRate);			
		}
		else {
			offspring = copyGeneration(parents);
		}
		
		if (mutationOperator != null){
			mutationOperator.mutate(offspring, mutationRate);			
		}
		
		return offspring;
	}
	
	private ArrayList<Individual> copyGeneration(ArrayList<ArrayList<Individual>> parents){
		ArrayList<Individual> offspring = new ArrayList<>();
		for (ArrayList<Individual> couple : parents) {
			offspring.addAll(couple);
		}
		return offspring;
	}

}
