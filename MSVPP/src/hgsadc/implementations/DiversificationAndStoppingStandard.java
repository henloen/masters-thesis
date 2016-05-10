package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.protocols.DiversificationProtocol;

public class DiversificationAndStoppingStandard implements DiversificationProtocol {

	private final int iterationsBeforeDiversify, iterationsBeforeStopping; // Number of iterations without improvement before diversifying
	private int diversificationIterationsWithoutImprovement, iterationsWithoutImprovement;
	private ArrayList<Integer> diversificationNumbers;
	private long startTime, maxTime;
	
	public DiversificationAndStoppingStandard(int iterationsBeforeDiversify, int iterationsBeforeStopping, int maxTime) {
		this.iterationsBeforeDiversify = iterationsBeforeDiversify;
		this.iterationsBeforeStopping = iterationsBeforeStopping;
		this.diversificationIterationsWithoutImprovement = 0;
		this.iterationsWithoutImprovement = 0;
		this.diversificationNumbers = new ArrayList<Integer>();
		
		startTime = System.nanoTime()/1000000000;
		
		if (maxTime <= 0){
			this.maxTime = Long.MAX_VALUE;
		}
		else {
			this.maxTime = maxTime;
		}
	}
	
	public void incrementCounters(){
		diversificationIterationsWithoutImprovement++;
		iterationsWithoutImprovement++;
	}
	
	@Override
	public boolean isDiversifyIteration(){
		return diversificationIterationsWithoutImprovement >= iterationsBeforeDiversify;
	}
	
	@Override
	public boolean isStoppingIteration() {
		long currentTime = System.nanoTime()/1000000000;
		long elapsedTime = currentTime - startTime;
		return iterationsWithoutImprovement >= iterationsBeforeStopping || elapsedTime >= maxTime;
	}

	@Override
	public void resetDiversificationCounter() {
		diversificationIterationsWithoutImprovement = 0;
	}
	
	private void resetStoppingCounter() {
		iterationsWithoutImprovement = 0;
	}
	
	@Override
	public void addDiversification(int iteration) {
		diversificationNumbers.add(iteration);
	}

	@Override
	public ArrayList<Integer> getDiversificationNumbers() {
		return diversificationNumbers;
	}

	@Override
	public void updateIterationsSinceImprovementCounter(boolean improvingSolutionFound) {
		if (improvingSolutionFound){
			resetStoppingCounter();
			resetDiversificationCounter();
		}
		else {
			incrementCounters();
		}
		
	}
	
}