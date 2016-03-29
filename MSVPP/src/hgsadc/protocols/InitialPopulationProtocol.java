package hgsadc.protocols;

import hgsadc.Individual;
import hgsadc.ProblemData;

import java.util.ArrayList;

public interface InitialPopulationProtocol {

	public ArrayList<Individual> createInitialPopulation();
}
