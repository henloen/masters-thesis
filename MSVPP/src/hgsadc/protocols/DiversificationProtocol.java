package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface DiversificationProtocol {

	boolean isDiversifyIteration();

	void resetCounter();

	void incrementCounter();

	void updateCounterSinceLastImprovement(double newBestSolution);

	int getNumberOfDiversifications();

	void addDiversification();

}
