package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface ReproductionProtocol {
	public ArrayList<Individual> reproduction(ArrayList<Individual> individuals);
}
