package ea.onemax;

import java.util.ArrayList;
import java.util.Random;

import ea.Individual;
import ea.protocols.CrossoverOperator;

public class CrossoverOneMax extends CrossoverOperator{

	protected ArrayList<Individual> crossoverCouple(ArrayList<Individual> couple) {
		ArrayList<Individual> children = new ArrayList<Individual>();
		
		GenotypeOneMax motherGenotype = (GenotypeOneMax) couple.get(0).getGenotype();
		String motherGenotypeString = motherGenotype.getGenotypeString();
		GenotypeOneMax fatherGenotype = (GenotypeOneMax) couple.get(1).getGenotype();
		String fatherGenotypeString = fatherGenotype.getGenotypeString();
		
		Random rand = new Random();
		int crossoverIndex = rand.nextInt(motherGenotypeString.length()-1);
		String child1GenotypeString = motherGenotypeString.substring(0, crossoverIndex) + fatherGenotypeString.substring(crossoverIndex);
		String child2GenotypeString = fatherGenotypeString.substring(0, crossoverIndex) + motherGenotypeString.substring(crossoverIndex);
		Individual child1 = new Individual(new GenotypeOneMax(child1GenotypeString));
		Individual child2 = new Individual(new GenotypeOneMax(child2GenotypeString));
		children.add(child1);
		children.add(child2);
		return children;
	}

}
