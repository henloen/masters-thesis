package ea.onemax;

import java.util.ArrayList;
import java.util.Random;

import ea.Individual;
import ea.protocols.GeneticOperatorProtocol;

public class GeneticOperatorOneMax implements GeneticOperatorProtocol {

	@Override
	public ArrayList<Individual> crossover(ArrayList<ArrayList<Individual>> parents, double crossoverRate) {
		ArrayList<Individual> offspring = new ArrayList<Individual>();
		Random rand = new Random();
		for (ArrayList<Individual> couple : parents) {
			double randomValue = rand.nextDouble();
			if (randomValue < crossoverRate) {
				offspring.addAll(crossoverCouple(couple));
			}
			else {
				offspring.addAll(couple);
			}
		}
		return offspring;
	}
	
	private ArrayList<Individual> crossoverCouple(ArrayList<Individual> couple) {
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

	@Override
	public ArrayList<Individual> mutate(ArrayList<Individual> offspring, double mutationRate) {
		Random rand = new Random();
		for (Individual individual : offspring) {
			double randomValue = rand.nextDouble();
			if (randomValue < mutationRate) {
				mutateIndividual(individual);
			}
		}
		return offspring;
	}
	
	private void mutateIndividual(Individual individual){
		GenotypeOneMax genotype = (GenotypeOneMax) individual.getGenotype();
		String genotypeString = genotype.getGenotypeString();
		
		Random rand = new Random();
		int mutationIndex = rand.nextInt(genotypeString.length());
		
		String mutatedGenotypeString;
		char invertedBit = (genotypeString.charAt(mutationIndex) == '0') ? '1' : '0'; //if-sentence
		mutatedGenotypeString = genotypeString.substring(0, mutationIndex) + invertedBit + genotypeString.substring(mutationIndex+1);
		individual.setGenotype(new GenotypeOneMax(mutatedGenotypeString));
	}

}
