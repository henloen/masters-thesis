package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface ReproductionProtocol {
	public ArrayList<Individual> reproduce(ArrayList<ArrayList<Individual>>parents, double crossoverRate, double mutationRate);
}
