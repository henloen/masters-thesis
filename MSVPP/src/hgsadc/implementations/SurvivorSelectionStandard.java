package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.SurvivorSelectionProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SurvivorSelectionStandard implements SurvivorSelectionProtocol {

	private ProblemData problemData;
	
	public SurvivorSelectionStandard(ProblemData problemData) {
		this.problemData = problemData;
	}
	
	@Override
	public void selectSurvivors(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		System.out.println("Select survivors");
		int populationSize = problemData.getHeuristicParameterInt("Population size");
		ArrayList<Individual> clones = getClones(subpopulation, fitnessEvaluationProtocol);
		//sorts the clones and the subpopulation so that the individuals with the highest biased fitness are first
		Collections.sort(clones, Collections.reverseOrder(Utilities.getBiasedFitnessComparator()));
		System.out.println("Clones: " + clones);
		Collections.sort(subpopulation, Collections.reverseOrder(Utilities.getBiasedFitnessComparator()));
		while (subpopulation.size() > populationSize) {
			if (clones.size() > 0) {
				System.out.println("remove clone");
				System.out.println(subpopulation);
				System.out.println(clones.get(0));
				removeFromSubpopulation(subpopulation, clones.remove(0), otherSubpopulation, fitnessEvaluationProtocol);
				System.out.println(subpopulation);
			}
			else {
				System.out.println("remove other");
				System.out.println(subpopulation);
				removeFromSubpopulation(subpopulation, subpopulation.get(0), otherSubpopulation , fitnessEvaluationProtocol);
				System.out.println(subpopulation);
			}
		}
	}
	
	private ArrayList<Individual> getClones (ArrayList<Individual> subpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		ArrayList<Individual> clones = new ArrayList<Individual>();
		for (int i = 0; i < subpopulation.size()-1; i++) {
			for (int j = i+1; j < subpopulation.size(); j++) {
				Individual individual = subpopulation.get(i);
				Individual otherIndividual = subpopulation.get(j);
				if ((fitnessEvaluationProtocol.getHammingDistance(individual, otherIndividual) == 0)
						&& !(clones.contains(individual) || clones.contains(otherIndividual))) {
					clones.add(otherIndividual);
				}
			}
		}
		return clones;
	}
	
	private void removeFromSubpopulation(ArrayList<Individual> subpopulation, Individual individual, ArrayList<Individual> otherSubpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		subpopulation.remove(individual);
		fitnessEvaluationProtocol.removeDiversityDistance(individual);
		fitnessEvaluationProtocol.updateBiasedFitness(Utilities.getAllElements(subpopulation, otherSubpopulation));
	}

}
