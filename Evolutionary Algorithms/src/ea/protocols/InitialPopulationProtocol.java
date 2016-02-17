package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface InitialPopulationProtocol {
	public ArrayList<Individual> createInitialPopulation(int nChildren);
}