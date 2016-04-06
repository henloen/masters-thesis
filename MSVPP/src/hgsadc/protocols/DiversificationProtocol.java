package hgsadc.protocols;

import java.util.ArrayList;

public interface DiversificationProtocol {

	boolean isDiversifyIteration();

	void resetCounter();

	void incrementCounter();

	void updateCounterSinceLastImprovement(double newBestSolution);

	void addDiversification(int iteration);

	ArrayList<Integer> getDiversificationNumbers();

}
