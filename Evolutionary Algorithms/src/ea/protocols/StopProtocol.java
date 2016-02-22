package ea.protocols;

import ea.Parameters;
import ea.Population;

public interface StopProtocol {
	
	public boolean stoppingCriterion(Population adults, int generationNumber, Parameters parameters);

}
