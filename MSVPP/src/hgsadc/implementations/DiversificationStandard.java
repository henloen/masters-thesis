package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.protocols.DiversificationProtocol;

public class DiversificationStandard implements DiversificationProtocol {

	private final int iterationsBeforeDiversify, iterationsBeforeStopping; // Number of iterations without improvement before diversifying
	private int diversificationIterationsWithoutImprovement, iterationsWithoutImprovement;
	private double bestSolution;
	private ArrayList<Integer> diversificationNumbers;
	
	public DiversificationStandard(int iterationsBeforeDiversify, int iterationsBeforeStopping) {
		this.iterationsBeforeDiversify = iterationsBeforeDiversify;
		this.iterationsBeforeStopping = iterationsBeforeStopping;
		this.diversificationIterationsWithoutImprovement = 0;
		this.iterationsWithoutImprovement = 0;
		this.diversificationNumbers = new ArrayList<Integer>();
		bestSolution = Double.MAX_VALUE;
	}
	
	@Override
	public void incrementCounter(){
		diversificationIterationsWithoutImprovement++;
		iterationsWithoutImprovement++;
	}
	
	@Override
	public boolean isDiversifyIteration(){
		return diversificationIterationsWithoutImprovement >= iterationsBeforeDiversify;
	}
	
	@Override
	public boolean isStoppingIteration() {
		return iterationsWithoutImprovement >= iterationsBeforeStopping;
	}

	@Override
	public void resetDiversificationCounter() {
		diversificationIterationsWithoutImprovement = 0;
	}
	
	private void resetStoppingCounter() {
		iterationsWithoutImprovement = 0;
	}
	
	@Override
	public void updateCounterSinceLastImprovement(double newBestSolution){
		//System.out.println("Current best solution : " + bestSolution);
		//System.out.println("New best solution: " + newBestSolution);
		if (newBestSolution < bestSolution){
			//System.out.println("New solution is better");
			bestSolution = newBestSolution;
			resetDiversificationCounter();
			resetStoppingCounter();
		}
		incrementCounter();
		//System.out.println("Iterations since last improvement: " + iterationsWithoutImprovement);
	}

	@Override
	public void addDiversification(int iteration) {
		diversificationNumbers.add(iteration);
	}

	@Override
	public ArrayList<Integer> getDiversificationNumbers() {
		return diversificationNumbers;
	}
	
}