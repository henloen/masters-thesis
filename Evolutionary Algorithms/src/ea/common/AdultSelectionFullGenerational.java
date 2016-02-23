package ea.common;

import java.util.ArrayList;

import ea.Individual;
import ea.Parameters;
import ea.Population;
import ea.protocols.AdultSelectionProtocol;

public class AdultSelectionFullGenerational implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, Parameters parameters) {
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		for (Individual child : children.getIndividuals()) {
			Individual adult = child.clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
