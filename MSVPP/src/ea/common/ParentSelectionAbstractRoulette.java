package ea.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ea.Individual;
import ea.Parameters;
import ea.Population;
import ea.protocols.ParentSelectionProtocol;

public abstract class ParentSelectionAbstractRoulette implements ParentSelectionProtocol {

	@Override
	public ArrayList<ArrayList<Individual>> selectParents(Population adults, int nChildren, Parameters parameters) {
		HashMap<Double[], Individual> rouletteWheel = createRouletteWheel(adults, parameters);
		return playRoulette(rouletteWheel, nChildren);
	}
	
	public abstract HashMap<Double[], Individual> createRouletteWheel(Population adults, Parameters parameters);  
	
	private ArrayList<ArrayList<Individual>> playRoulette(HashMap<Double[], Individual> rouletteWheel, int nChildren) {
		ArrayList<ArrayList<Individual>> parents = new ArrayList<ArrayList<Individual>>();
		for (int i=0; i<nChildren/2; i++) {
			ArrayList<Individual> couple = new ArrayList<Individual>();
			for (int j=0; j<2; j++) {
				Random rand = new Random();
				double value = rand.nextDouble();
				for (Double[] range : rouletteWheel.keySet()) {
					if (value >= range[0] && value < range[1]) {
						couple.add(rouletteWheel.get(range));
						break;
					}
				}
			}
			parents.add(couple);
		}
		return parents;
	}

}
