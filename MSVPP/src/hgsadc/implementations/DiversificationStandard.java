package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.protocols.DiversificationProtocol;

public class DiversificationStandard implements DiversificationProtocol {

	private int iterationsBeforeDiversify; // Number of iterations without improvement before diversifying
	private int iterationsWithoutImprovement;
	
	
	public DiversificationStandard(int iterationsBeforeDiversify) {
		this.iterationsBeforeDiversify = iterationsBeforeDiversify;
		this.iterationsWithoutImprovement = 0;
	}
	
	@Override
	public boolean isDiversifyIteration(){
		return iterationsWithoutImprovement >= iterationsBeforeDiversify;
	}
}
