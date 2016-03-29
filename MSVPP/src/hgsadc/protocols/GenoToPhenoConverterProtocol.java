package hgsadc.protocols;

import hgsadc.Individual;

import java.util.ArrayList;

public interface GenoToPhenoConverterProtocol {

	public void convertGenotypeToPhenotype(ArrayList<Individual> population);
	
}
