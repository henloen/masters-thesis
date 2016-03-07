package ea.onemax;

import java.util.ArrayList;

import ea.Genotype;
import ea.Individual;
import ea.Parameters;
import ea.protocols.InitialPopulationProtocol;

public class InitialPopulationOneMax implements InitialPopulationProtocol {

	public ArrayList<Individual> createInitialPopulation(Parameters parameters) {
		int genotypeLength = Integer.parseInt(parameters.getOptionalParameters().get("Bitstring length"));
		
		ArrayList<Individual> initialPopulation = new ArrayList<Individual>();
		for (int i=0; i<parameters.getnChildren(); i++) {
			Genotype genotype = new GenotypeOneMax(genotypeLength); //sets the initial genotype to a random boolean string of length genotypeLength
			Individual child = new Individual(genotype);
			initialPopulation.add(child);
		}
		return initialPopulation;
	}

	@Override
	public ArrayList<Individual> createInitialPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

}
