package ea.onemax;

import ea.Individual;
import ea.Population;
import ea.protocols.FitnessEvaluationProtocol;

public class FitnessOneMax implements FitnessEvaluationProtocol {

	@Override
	public void evaluateFitness(Population population) {
		for (Individual individual : population.getIndividuals()) {
			setFitnessIndividual(individual);
		}
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
