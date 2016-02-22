package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface GeneticOperatorProtocol {
	
	public ArrayList<Individual> crossover(ArrayList<ArrayList<Individual>> parents, double crossoverRate);
	public ArrayList<Individual> mutate(ArrayList<Individual> population, double mutationRate);
	
}
