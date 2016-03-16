package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.Individual;
import ea.Parameters;
import ea.Population;
import ea.protocols.AdultSelectionProtocol;

public class AdultSelectionOverproduction implements AdultSelectionProtocol {

	@Override
	public void selectAdults(Population children, Population adults, Parameters parameters) {
		ArrayList<Individual> childIndividuals = children.getIndividuals();
		ArrayList<Individual> adultIndividuals = new ArrayList<Individual>();
		if (parameters.isMaximizeFitness()){
			Collections.sort(childIndividuals, Collections.reverseOrder());
		}
		else {
			Collections.sort(childIndividuals);
		}
		for (int i=0; i<parameters.getnAdults(); i++){
			Individual adult = childIndividuals.get(i).clone();
			adultIndividuals.add(adult);
		}
		adults.setIndividuals(adultIndividuals);
	}

}
