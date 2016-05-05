package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;
import java.util.HashMap;

public interface SurvivorSelectionProtocol {

	public void selectSurvivors(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol);
	public ArrayList<Individual> getClones(ArrayList<Individual> subpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol);

}
