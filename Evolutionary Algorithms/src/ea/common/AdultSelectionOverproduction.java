package ea.common;

import java.util.ArrayList;
import java.util.Collections;

import ea.AdultSelectionProtocol;
import ea.Individual;

public class AdultSelectionOverproduction implements AdultSelectionProtocol {

	@Override
	public ArrayList<Individual> selectAdults(ArrayList<Individual> children, ArrayList<Individual> adults, int nAdults) {
		adults.clear();
		Collections.sort(children, Collections.reverseOrder());
		for (int i=0; i<nAdults; i++){
			Individual adult = children.get(i).clone();
			adults.add(adult);
		}
		return adults;
	}

}
