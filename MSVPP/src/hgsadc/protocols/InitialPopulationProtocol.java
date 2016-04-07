package hgsadc.protocols;

import hgsadc.Individual;

public interface InitialPopulationProtocol {

	public Individual createIndividual();

	public int getNumberOfConstructionHeuristicRestarts();
}
