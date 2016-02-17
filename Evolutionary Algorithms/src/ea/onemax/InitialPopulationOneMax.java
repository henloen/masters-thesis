package ea.onemax;

import java.util.ArrayList;

import ea.Genotype;
import ea.Individual;
import ea.protocols.InitialPopulationProtocol;

public class InitialPopulationOneMax implements InitialPopulationProtocol {

	//parameters specific to the OneMax problem are hardcoded
	private int genotypeLength = 20; 
	
	
	@Override
	public ArrayList<Individual> createInitialPopulation(int nChildren) {
		ArrayList<Individual> initialPopulation = new ArrayList<Individual>();
		for (int i=0; i<nChildren; i++) {
			Genotype genotype = new GenotypeOneMax(genotypeLength); //sets the initial genotype to a random boolean string of length genotypeLength
			Individual child = new Individual(genotype);
			initialPopulation.add(child);
		}
		return initialPopulation;
	}

}
