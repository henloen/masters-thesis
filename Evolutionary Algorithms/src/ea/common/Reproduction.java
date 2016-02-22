package ea.common;

import java.util.ArrayList;

import ea.Individual;
import ea.protocols.GeneticOperatorProtocol;
import ea.protocols.ReproductionProtocol;

public class Reproduction implements ReproductionProtocol {

	private GeneticOperatorProtocol geneticOperatorProtocol;
	
	public Reproduction(GeneticOperatorProtocol geneticOperatorProtocol) {
		this.geneticOperatorProtocol = geneticOperatorProtocol;
	}
	
	@Override
	public ArrayList<Individual> reproduction(ArrayList<ArrayList<Individual>> parents, double crossoverRate, double mutationRate) {
		ArrayList<Individual> offspring = geneticOperatorProtocol.crossover(parents, crossoverRate);
		geneticOperatorProtocol.mutate(offspring, mutationRate);
		return offspring;
	}

}
