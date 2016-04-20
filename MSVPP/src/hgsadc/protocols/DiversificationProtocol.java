package hgsadc.protocols;

import java.util.ArrayList;

public interface DiversificationProtocol {

	boolean isDiversifyIteration();
	
	boolean isStoppingIteration();

	void resetDiversificationCounter();

	void incrementCounter();

	void updateCounterSinceLastImprovement(double newBestSolution);

	void addDiversification(int iteration);

	ArrayList<Integer> getDiversificationNumbers();

}
