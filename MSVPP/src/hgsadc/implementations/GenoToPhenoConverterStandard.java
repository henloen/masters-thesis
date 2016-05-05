package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.Phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GenoToPhenoConverterStandard implements
		GenoToPhenoConverterProtocol {

	protected ProblemData problemData;
	
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
		setViolations(phenotype, giantTour);
		individual.setPhenotype(phenotype);
	}
	
	private void setViolations(Phenotype phenotype, HashMap<Integer, HashMap<Vessel, Voyage>> giantTour) {
		double scheduleCost = 0;
		double durationViolation = 0;
		double capacityViolation = 0;
		double numberOfInstallationsViolation = 0;
		for (Integer day : giantTour.keySet()){
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					scheduleCost += voyage.getCost();
					durationViolation += voyage.getDurationViolation();
					capacityViolation += voyage.getCapacityViolation();
					numberOfInstallationsViolation += voyage.getNumberOfInstallationsViolation();
				}
			}
		}
		phenotype.setScheduleCost(scheduleCost);
		phenotype.setDurationViolation(durationViolation);
		phenotype.setCapacityViolation(capacityViolation);
		phenotype.setNumberOfInstallationsViolation(numberOfInstallationsViolation);
	}
	
}
