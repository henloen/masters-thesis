package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface ReproductionProtocol {

	Individual crossover(ArrayList<Individual> parents);

	public int getNumberOfCrossoverRestarts();

}
