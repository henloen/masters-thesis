package ea.common;

import ea.Parameters;
import ea.Population;
import ea.protocols.StopProtocol;

public class StopNGenerations implements StopProtocol {

	@Override
	public boolean stoppingCriterion(Population adults, int generationNumber, Parameters parameters) {
		if (generationNumber >= parameters.getnGenerations()){
			return true;
		}
		else {
			return false;
		}
	}

}
