package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.AdultSelectionProtocol;
import ea.Individual;
import ea.Population;

public class AdultSelectionOverproduction implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, int nAdults) {
		ArrayList<Individual> childIndividuals = children.getIndividuals();
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		Collections.sort(childIndividuals, Collections.reverseOrder());
		for (int i=0; i<nAdults; i++){
			Individual adult = childIndividuals.get(i).clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
