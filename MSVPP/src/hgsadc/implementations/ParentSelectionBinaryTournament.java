package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.Utilities;
import hgsadc.protocols.ParentSelectionProtocol;

public class ParentSelectionBinaryTournament implements ParentSelectionProtocol {

	@Override
	public ArrayList<Individual> selectParents(ArrayList<Individual> individuals) {
		ArrayList<Individual> parents = new ArrayList<Individual>();
		Individual parent1 = pickParent(individuals);
		Individual parent2 = pickParent(individuals);
		//does not check whether parent1 and parent2 are the same individual
		parents.add(parent1);
		parents.add(parent2);
		System.out.println("Parents: " + parents);
		return parents;
	}
	
	private Individual pickParent(ArrayList<Individual> individuals) {
		Individual parentCandidate1 = Utilities.pickRandomElementFromList(individuals);
		Individual parentCandidate2 = Utilities.pickRandomElementFromList(individuals);
		if (parentCandidate1.getBiasedFitness() <= parentCandidate2.getBiasedFitness()) {
			return parentCandidate1;
		}
		else {
			return parentCandidate2;
		}
	}

}
