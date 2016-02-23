package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.Individual;
import ea.Parameters;
import ea.Population;
import ea.protocols.AdultSelectionProtocol;

public class AdultSelectionGenerationalMixing implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, Parameters parameters) {
		ArrayList<Individual> bothPopulations = new ArrayList<Individual>();
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		bothPopulations.addAll(children.getIndividuals());
		bothPopulations.addAll(adults.getIndividuals());
		Collections.sort(bothPopulations, Collections.reverseOrder());
		for (int i=0; i<parameters.getnAdults(); i++){
			Individual adult = bothPopulations.get(i).clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
