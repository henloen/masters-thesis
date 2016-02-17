package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.AdultSelectionProtocol;
import ea.Individual;

public class AdultSelectionGenerationalMixing implements AdultSelectionProtocol {

	@Override
	public ArrayList<Individual> selectAdults(ArrayList<Individual> children, ArrayList<Individual> adults, int nAdults) {
		ArrayList<Individual> bothPopulations = new ArrayList<Individual>();
		bothPopulations.addAll(children);
		bothPopulations.addAll(adults);
		adults.clear();
		Collections.sort(bothPopulations, Collections.reverseOrder());
		for (int i=0; i<nAdults; i++){
			Individual adult = bothPopulations.get(i).clone();
			adults.add(adult);
		}
		return adults;
	}

}
