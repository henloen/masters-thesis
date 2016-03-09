package ea.svpp;

import java.util.HashMap;
import java.util.Set;

import ea.Individual;
import ea.protocols.MutationOperator;
import voyageGenerationDP.Installation;
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
		Vessel vessel1 = UtilitiesSVPP.pickRandomElementFromList(problemData.vessels);
		Vessel vessel2;
		do {
			vessel2 = UtilitiesSVPP.pickRandomElementFromList(problemData.vessels);
		} while (vessel1 == vessel2);

		HashMap<Vessel, Voyage[]> newSchedule = new HashMap<>();
		
		copyVoyages(vessel1, vessel2, genotype.getScheduleForPSV(vessel1), newSchedule);
		copyVoyages(vessel2, vessel1, genotype.getScheduleForPSV(vessel2), newSchedule);
		
		// Copy voyages which are not swapped
		for (Vessel otherVessel : genotype.getCharteredVessels()){
			
			if (otherVessel == vessel1 || otherVessel == vessel2) continue;
			else {
				Voyage[] scheduleForVessel = genotype.getScheduleForPSV(otherVessel);
				newSchedule.put(otherVessel, scheduleForVessel);
			}
		}
		
		GenotypeSVPP newGenotype = new GenotypeSVPP(newSchedule);
		individual.setGenotype(newGenotype); // Note: This also sets phenotype to null and fitness to 0
	}

	public void copyVoyages(Vessel vesselToCopy, Vessel vesselThatCopies, Voyage[] voyagesToCopy, HashMap<Vessel,Voyage[]> newSchedule){
		// Copies voyages(installation visits) from vesselToCopy to vesselThatCopies
		
		Voyage[] newVesselSchedule = new Voyage[GenotypeSVPP.NUMBER_OF_DAYS];
		for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
			Voyage voyage = voyagesToCopy[day];
			
			if (voyage != null) {
				
				Set<Installation> visitedSet = UtilitiesSVPP.getSetOfVisitedInstallations(voyage);
				Voyage voyageForVessel2 = problemData.voyageByVesselAndInstallationSet.get(vesselThatCopies).get(visitedSet);
				
				newVesselSchedule[day] = voyageForVessel2;
				// TODO What if PSV2 cannot sail the voyage?
			}
		}
		newSchedule.put(vesselThatCopies, newVesselSchedule);
	}

	
}
