package ea.onemax;

import java.util.ArrayList;
import java.util.Random;

import ea.Individual;
import ea.protocols.MutationOperator;;

public class MutationOneMax extends MutationOperator{


	protected void mutateIndividual(Individual individual){
		GenotypeOneMax genotype = (GenotypeOneMax) individual.getGenotype();
		String genotypeString = genotype.getGenotypeString();
		
		Random rand = new Random();
		int mutationIndex = rand.nextInt(genotypeString.length());
		
		String mutatedGenotypeString;
		char invertedBit = (genotypeString.charAt(mutationIndex) == '0') ? '1' : '0'; //if-sentence
		mutatedGenotypeString = genotypeString.substring(0, mutationIndex) + invertedBit + genotypeString.substring(mutationIndex+1);
		individual.setGenotype(new GenotypeOneMax(mutatedGenotypeString));
	}

}
