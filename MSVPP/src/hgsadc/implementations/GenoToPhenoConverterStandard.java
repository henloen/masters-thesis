package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GenoToPhenoConverterStandard implements
		GenoToPhenoConverterProtocol {

	private ProblemData problemData;
	
	public GenoToPhenoConverterStandard(ProblemData problemData) {
		this.problemData = problemData;
	}
	
	public void convertGenotypeToPhenotype(Individual individual) {
		GenotypeHGS genotype = (GenotypeHGS) individual.getGenotype();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = genotype.getGiantTourChromosome();
		HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome = genotype.getVesselDeparturePatternChromosome();
		HashMap<Integer, HashMap<Vessel, Voyage>> giantTour = new HashMap<Integer, HashMap<Vessel,Voyage>>();
		for (Integer day : giantTourChromosome.keySet()) {
			HashMap<Vessel, Voyage> giantTourDay = new HashMap<Vessel, Voyage>();
			for (Integer vesselNumber : giantTourChromosome.get(day).keySet()) {
				ArrayList<Integer> installations = giantTourChromosome.get(day).get(vesselNumber);
				Vessel vessel = problemData.getVesselByNumber(vesselNumber);
				Voyage voyage;
				if (installations.size() == 0) {
					voyage = null;
				}
				else {
					voyage = new Voyage(installations, vessel, vesselDeparturePatternChromosome.get(vesselNumber), day, problemData);
				}
				giantTourDay.put(vessel, voyage);
			}
			giantTour.put(day, giantTourDay);
		}
		PhenotypeHGS phenotype = new PhenotypeHGS(giantTour);
		individual.setPhenotype(phenotype);
	}
}
