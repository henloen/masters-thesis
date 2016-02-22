package ea.onemax;

import ea.Individual;
import ea.Parameters;
import ea.Population;
import ea.protocols.StopProtocol;

public class StopOneMax implements StopProtocol {

	@Override
	public boolean stoppingCriterion(Population adults, int generationNumber, Parameters parameters) {
		Individual bestIndividual = adults.getBestIndividual();
		return parameters.getnGenerations() < generationNumber || (bestIndividual.getFitness() == 1);
	}

}
