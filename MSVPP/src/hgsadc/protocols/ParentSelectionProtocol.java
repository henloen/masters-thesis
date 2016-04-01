package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface ParentSelectionProtocol {

	public ArrayList<Individual> selectParents(ArrayList<Individual> individuals);

}
