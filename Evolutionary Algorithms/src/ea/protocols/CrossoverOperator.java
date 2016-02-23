package ea.protocols;

import java.util.ArrayList;
import java.util.Random;

import ea.Individual;

public abstract class CrossoverOperator {
	
	protected abstract ArrayList<Individual> crossoverCouple(ArrayList<Individual> couple);
	
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
}
