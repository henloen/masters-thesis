package ea.svpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ea.Individual;
import ea.protocols.MutationOperator;

public class SwapPSVMutation extends MutationOperator {
	
	@Override
	protected void mutateIndividual(Individual individual) {
		// TODO Auto-generated method stub
		SVPPPhenotype phenotype = (SVPPPhenotype) individual.getPhenotype();
		SVPPGenotype genotype = (SVPPGenotype) individual.getGenotype();
		
		int numberOfAvailablePSVs = SVPPGenotype.NUMBER_OF_PSVS;
		
		int PSVToRemove = getRandomIntegerFromSet(phenotype.getCharteredPSVs());

		// Create a set of all unchartered PSVs
		Set<Integer> uncharteredPSVs = new HashSet<Integer>();
		for (int i = 0; i < numberOfAvailablePSVs; i++) {
			if (!phenotype.getCharteredPSVs().contains(i)){
				uncharteredPSVs.add(i);
			}
		}
		
		// Pick random unchartered PSV to swap voyages with
		int PSVToAdd = getRandomIntegerFromSet(uncharteredPSVs);
		
	}

	public Integer getRandomIntegerFromSet(Set<Integer> set){
		int indexToPick = new Random().nextInt(set.size());
		int i = 0;
		for (Integer element : set){
			if (i == indexToPick){
				return element;
			}
			i++;
		}
		return null;
	}
	
}
