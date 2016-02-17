package ea.common;

import java.util.ArrayList;

import ea.AdultSelectionProtocol;
import ea.Individual;
import ea.Population;

public class AdultSelectionFullGenerational implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, int nAdults) {
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		for (Individual child : children.getIndividuals()) {
			Individual adult = child.clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
