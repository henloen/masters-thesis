package ea.common;

import java.util.HashMap;

import ea.Individual;
import ea.Population;

public class ParentSelectionFitnessProportionate extends ParentSelectionAbstractRoulette {

	@Override
	public HashMap<Double[], Individual> createRouletteWheel(Population adults) {
		double sumFitness = adults.getSumFitness();
		HashMap<Double[], Individual> rouletteWheel = new HashMap<Double[], Individual>();
		double position = 0;
		for (Individual individual : adults.getIndividuals()) {
			double share = individual.getFitness() / sumFitness;
			Double[] range = new Double[]{position, position + share};
			rouletteWheel.put(range, individual);
			position += share;
		}
		return rouletteWheel;
	}

}
