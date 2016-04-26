package ea.svpp;

import java.util.HashSet;
import java.util.Set;

import ea.Individual;
import ea.Population;
import ea.protocols.GenoToPhenoProtocol;
import voyageGenerationDPold.Vessel;

public class GenoToPhenoSVPP implements GenoToPhenoProtocol {

	@Override
	public void convertGenoToPheno(Population individuals) {
		for (Individual individual : individuals.getIndividuals()){
			convertIndividualGenoToPheno(individual);
		}
	}
	
	private void convertIndividualGenoToPheno(Individual individual){
		PhenotypeSVPP phenotype = new PhenotypeSVPP((GenotypeSVPP) individual.getGenotype());
		individual.setPhenotype(phenotype);
	}
	/*
	private void convertIndividualGenoToPheno(Individual individual){
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		Set<Integer> charteredPSVs = new HashSet<>();
		Set<int[]> voyagesSailed = new HashSet<>();
		
		for (Vessel vessel : genotype.getCharteredVessels()){
			charteredPSVs.add(vessel.getName());
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
				Voyage voyage = genotype.getDeparture(vessel, day);
				if (voyage != null){
					
				}
			}
		}
		
		
		
		int[][] schedule = genotype.getSchedule();
		
		for (int PSV = 0; PSV < genotype.NUMBER_OF_PSVS; PSV++) {
			for (int day = 0; day < genotype.NUMBER_OF_DAYS; day++){
				int voyage = schedule[PSV][day];
				
				if (voyage > 0){
					charteredPSVs.add(PSV); // Perhaps unnecessary to add every time? Probably pretty cheap, though. Yes, constant time
					
					int[] voyageSailed = new int[3];
					voyageSailed[0] = PSV;
					voyageSailed[1] = voyage;
					voyageSailed[2] = day;
					
					voyagesSailed.add(voyageSailed);
				}
			}
		}
		PhenotypeSVPP phenotype = new PhenotypeSVPP(charteredPSVs, voyagesSailed);
		individual.setPhenotype(phenotype);
	}
	*/
}
