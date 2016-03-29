package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface DiversificationProtocol {

	void diversify(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation);

}
