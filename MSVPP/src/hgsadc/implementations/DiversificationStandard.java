package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.protocols.DiversificationProtocol;

public class DiversificationStandard implements DiversificationProtocol {

	private int iterationsBeforeDiversify; // Number of iterations without improvement before diversifying
	private int iterationsWithoutImprovement;
	private double bestSolution;
	private int numberOfDiversifications;
	
	public DiversificationStandard(int iterationsBeforeDiversify) {
		this.iterationsBeforeDiversify = iterationsBeforeDiversify;
		this.iterationsWithoutImprovement = 0;
		this.numberOfDiversifications = 0;
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
		if (newBestSolution <= bestSolution){
			bestSolution = newBestSolution;
			resetCounter();
		}
	}

	@Override
	public int getNumberOfDiversifications() {
		return numberOfDiversifications;
	}
	@Override
	public void addDiversification() {
		this.numberOfDiversifications++;
	}
	
}