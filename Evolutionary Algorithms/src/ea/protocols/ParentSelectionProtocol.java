package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface ParentSelectionProtocol {
	public ArrayList<ArrayList<Individual>> selectParents(ArrayList<Individual> adults, int nChildren);
}
