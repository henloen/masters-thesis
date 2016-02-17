package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface FitnessEvaluationProtocol {
	
	public ArrayList<Individual> evaluateFitness(ArrayList<Individual> individuals);

}
