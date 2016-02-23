package ea.protocols;

import java.util.ArrayList;
import java.util.Random;

import ea.Individual;

public abstract class MutationOperator {
	
	protected abstract void mutateIndividual(Individual individual);
	
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
	
}
