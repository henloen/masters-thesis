package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.protocols.DiversificationProtocol;

public class DiversificationStandard implements DiversificationProtocol {

	private final int iterationsBeforeDiversify; // Number of iterations without improvement before diversifying
	private int iterationsWithoutImprovement;
	private double bestSolution;
	private ArrayList<Integer> diversificationNumbers;
	
	public DiversificationStandard(int iterationsBeforeDiversify) {
		this.iterationsBeforeDiversify = iterationsBeforeDiversify;
		this.iterationsWithoutImprovement = 0;
		this.diversificationNumbers = new ArrayList<Integer>();
		bestSolution = Double.MAX_VALUE;
	}
	
	@Override
	public void incrementCounter(){
		iterationsWithoutImprovement++;
	}
	
	@Override
	public boolean isDiversifyIteration(){
		return iterationsWithoutImprovement >= iterationsBeforeDiversify;
	}

	@Override
	public void resetCounter() {
		iterationsWithoutImprovement = 0;
	}
	
	@Override
	public void updateCounterSinceLastImprovement(double newBestSolution){
		//System.out.println("Current best solution : " + bestSolution);
		//System.out.println("New best solution: " + newBestSolution);
		if (newBestSolution < bestSolution){
			//System.out.println("New solution is better");
			bestSolution = newBestSolution;
			resetCounter();
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