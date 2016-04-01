package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.protocols.ReproductionProtocol;

public class ReproductionStandard implements ReproductionProtocol {

	@Override
	public Individual crossover(ArrayList<Individual> parents) {
		return parents.get(0);
	}

}
