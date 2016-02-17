package ea.common;

import java.util.ArrayList;

import ea.AdultSelectionProtocol;
import ea.Individual;

public class AdultSelectionFullGenerational implements AdultSelectionProtocol {

	@Override
	public ArrayList<Individual> selectAdults(ArrayList<Individual> children, ArrayList<Individual> adults, int nAdults) {
		adults.clear();
		for (Individual child : children) {
			Individual adult = child.clone();
			adults.add(adult);
		}
		return adults;
	}

}
