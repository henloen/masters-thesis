package ea.protocols;

import java.util.ArrayList;

import ea.Individual;
import ea.Parameters;
import ea.Population;

public interface ParentSelectionProtocol {
	public ArrayList<ArrayList<Individual>> selectParents(Population adults, int nChildren, Parameters parameters);
}
