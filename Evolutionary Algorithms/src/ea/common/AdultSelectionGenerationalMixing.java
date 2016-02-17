package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.AdultSelectionProtocol;
import ea.Individual;
import ea.Population;

public class AdultSelectionGenerationalMixing implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, int nAdults) {
		ArrayList<Individual> bothPopulations = new ArrayList<Individual>();
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		bothPopulations.addAll(children.getIndividuals());
		bothPopulations.addAll(adults.getIndividuals());
		Collections.sort(bothPopulations, Collections.reverseOrder());
		for (int i=0; i<nAdults; i++){
			Individual adult = bothPopulations.get(i).clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
