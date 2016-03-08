package ea.svpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ea.Individual;
import ea.protocols.MutationOperator;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public class MutationSwapVoyagesBetweenPSVs extends MutationOperator {
	
	ProblemDataSVPP problemData;
	
	public MutationSwapVoyagesBetweenPSVs(ProblemDataSVPP problemData){
		this.problemData = problemData;
	}
	
	@Override
	protected void mutateIndividual(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		// Pick two random PSVs
		int PSV1number = new Random().nextInt(GenotypeSVPP.NUMBER_OF_PSVS);
		int PSV2number;
		do {
			PSV2number = new Random().nextInt(GenotypeSVPP.NUMBER_OF_PSVS);
		} while (PSV1number == PSV2number);

		int[][] newSchedule = new int[GenotypeSVPP.NUMBER_OF_PSVS][GenotypeSVPP.NUMBER_OF_DAYS];
		copyVoyages(PSV1number, PSV2number, genotype.getSchedule(), newSchedule);
		copyVoyages(PSV2number, PSV1number, genotype.getSchedule(), newSchedule);
		
		
		
		GenotypeSVPP newGenotype = new GenotypeSVPP(newSchedule);
		individual.setGenotype(newGenotype); // Note: This also sets phenotype to null and fitness to 0
	}

/* Create a set of all unchartered PSVs
		Set<Integer> uncharteredPSVs = new HashSet<Integer>();
		for (int i = 0; i < numberOfAvailablePSVs; i++) {
			if (!phenotype.getCharteredPSVs().contains(i)){
				uncharteredPSVs.add(i);
			}
		}
 */

	public void copyVoyages(int PSV1num, int PSV2num, int[][] oldSchedule, int[][] newSchedule){
		// Copies voyages(installation visits) from PSV1 to PSV2.

		Vessel PSV2 = problemData.vessels.get(PSV2num);
		for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
			int voyageNumber = oldSchedule[PSV1num][day];
			
			if (voyageNumber != 0){
				Voyage voyage = problemData.voyageSet.get(voyageNumber-1);
				
				Set<Integer> visitedSet = UtilitiesSVPP.getSetOfVisitedInstallations(voyage);
				Voyage voyageForPSV2 = problemData.voyageByVesselAndInstallationSet.get(PSV2).get(visitedSet);

				// TODO What if PSV2 cannot sail the voyage?
				
				newSchedule[PSV2num][day] = voyageForPSV2.getNumber();
			}
		}
	}

	
}
