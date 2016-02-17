package ea.protocols;

import java.util.ArrayList;

import ea.Individual;

public interface LocalSearchProtocol {
	ArrayList<Individual> improveIndividuals(ArrayList<Individual> individuals);
}
