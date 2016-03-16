package ea.common;

import java.util.HashMap;

import ea.Individual;
import ea.Parameters;
import ea.Population;

public class ParentSelectionFitnessProportionate extends ParentSelectionAbstractRoulette {

	@Override
	public HashMap<Double[], Individual> createRouletteWheel(Population adults, Parameters parameters) {
		double sumFitness = adults.getSumFitness();
		HashMap<Double[], Individual> rouletteWheel = new HashMap<Double[], Individual>();
		double position = 0;
		double share;
		double minSumFitness = (adults.getIndividuals().size() - 1) * sumFitness;
		for (Individual individual : adults.getIndividuals()) {
			if (parameters.isMaximizeFitness()) {
				share = individual.getFitness() / sumFitness;
			}
			else {
				share = (sumFitness - individual.getFitness()) / minSumFitness;
			}
			Double[] range = new Double[]{position, position + share};
			rouletteWheel.put(range, individual);
			position += share;
		}
		return rouletteWheel;
	}

}
