package ea.onemax;

import java.util.ArrayList;

import ea.Individual;
import ea.protocols.FitnessEvaluationProtocol;

public class FitnessOneMax implements FitnessEvaluationProtocol {

	@Override
	public ArrayList<Individual> evaluateFitness(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			setFitnessIndividual(individual);
		}
		return individuals;
	}
	
	private void setFitnessIndividual(Individual individual) {
		PhenotypeOneMax phenotypeOneMax = (PhenotypeOneMax) individual.getPhenotype(); 
		String phenotypeString = phenotypeOneMax.getPhenotypeString();
		double oneCount = 0;
		for (int i = 0; i < phenotypeString.length(); i++) {
			if (phenotypeString.charAt(i) == '1') {
				oneCount++;
			}
		}
		individual.setFitness(oneCount / phenotypeString.length());
	}

}
