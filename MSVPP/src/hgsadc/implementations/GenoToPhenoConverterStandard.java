package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

import java.util.ArrayList;
import java.util.HashMap;

public class GenoToPhenoConverterStandard implements
		GenoToPhenoConverterProtocol {

	private ProblemData problemData;
	
	public GenoToPhenoConverterStandard(ProblemData problemData) {
		this.problemData = problemData;
	}
	
	@Override
	public void convertGenotypeToPhenotype(ArrayList<Individual> population) {
		for (Individual individual : population) {
			convertIndividualGenotypeToPhenotype(individual);
		}
	}

	private void convertIndividualGenotypeToPhenotype(Individual individual) {
		GenotypeHGS genotype = (GenotypeHGS) individual.getGenotype();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = genotype.getGiantTourChromosome();
		HashMap<Integer, HashMap<Vessel, Voyage>> giantTour = new HashMap<Integer, HashMap<Vessel,Voyage>>();
		for (Integer day : giantTourChromosome.keySet()) {
			HashMap<Vessel, Voyage> giantTourDay = new HashMap<Vessel, Voyage>();
			for (Integer vesselNumber : giantTourChromosome.get(day).keySet()) {
				ArrayList<Integer> installations = giantTourChromosome.get(day).get(vesselNumber);
				Vessel vessel = problemData.getVesselByNumber(vesselNumber);
				Voyage voyage = constructVoyage(installations, vessel);
				giantTourDay.put(vessel, voyage);
			}
			giantTour.put(day, giantTourDay);
		}
		PhenotypeHGS phenotype = new PhenotypeHGS(giantTour);
		individual.setPhenotype(phenotype);
	}
	
	/*
	 * contructVoyage is written without time windows, and thus no waiting time is added.
	 * Needs to be rewritten later, code that includes time windows can be found in voyageGeneratorDP/Generator
	 */
	private Voyage constructVoyage(ArrayList<Integer> installations, Vessel vessel) {
		Installation fromInstallation = problemData.getInstallationByNumber(0);//the depot is installation number 0
		ArrayList<Installation> visitedInstallations = new ArrayList<Installation>();
		double duration = 0;
		double cost = 0;
		double capacityUsed = 0;
		for (Integer installationNumber : installations) { //the depot is not contained in the list of installations, so a for each loop can be used
			Installation toInstallation = problemData.getInstallationByNumber(installationNumber);
			double sailingTime = Math.ceil((problemData.getDistance(fromInstallation, toInstallation)/vessel.getSpeed()));
			duration += sailingTime + toInstallation.getServiceTime();
			capacityUsed += toInstallation.getDemandPerVisit();
			cost += (sailingTime*vessel.getFuelCostSailing()) + (toInstallation.getServiceTime()*vessel.getFuelCostInstallation());
			visitedInstallations.add(toInstallation);
			fromInstallation = toInstallation;
		}
		return new Voyage(cost, capacityUsed, duration, visitedInstallations);
	}

}
