package ea.protocols;

import ea.Parameters;
import ea.Population;

public interface AdultSelectionProtocol {
	public void selectAdults(Population children, Population adults, Parameters parameters);
}
