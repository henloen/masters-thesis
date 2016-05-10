package hgsadc.protocols;

import java.util.ArrayList;

public interface DiversificationProtocol {

	boolean isDiversifyIteration();
	
	boolean isStoppingIteration();

	void resetDiversificationCounter();

	void addDiversification(int iteration);

	ArrayList<Integer> getDiversificationNumbers();

	void updateIterationsSinceImprovementCounter(boolean improvingSolutionFound);

}
