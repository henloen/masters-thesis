package ea.protocols;

import java.util.ArrayList;

import ea.Individual;
import ea.Parameters;

public interface InitialPopulationProtocol {
	public ArrayList<Individual> createInitialPopulation();
}