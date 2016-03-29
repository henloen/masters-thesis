package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

import java.util.ArrayList;
import java.util.HashMap;

import voyageGenerationDP.Label;

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
		for (Integer day : giantTourChromosome.keySet()) {
			for (Integer vesselNumber : giantTourChromosome.get(day).keySet()) {
				ArrayList<Integer> installations = giantTourChromosome.get(day).get(vesselNumber);
				Vessel vessel = problemData.getVesselByNumber(vesselNumber);
				Voyage voyage = constructVoyage(installations);
			}
		}
	}
	
	private Voyage constructVoyage(ArrayList<Integer> installations) {
		Installation fromInstallation = problemData.getInstallationByNumber(0);//the depot is installation number 0
		double currentDepartureTime = 0;
		double currentCost = 0;
		for (Integer installationNumber : installations) {
			Installation toInstallation = problemData.getInstallationByNumber(installationNumber);
			double sailingTime = Math.ceil((problemData.getDistance(fromInstallation, toInstallation)/vessel.getSpeed()));
			double arrivalTime = currentDepartureTime + sailingTime;
			double todaysOpeningHour = toInstallation.getTodaysOpeningHour(arrivalTime);
			double todaysClosingHour = toInstallation.getTodaysClosingHour(arrivalTime);
			double waitingTime = 0;
			int visitedSum = label.getVisitedSum() + (int)Math.pow(2, installation.getNumber());
			double tomorrowsOpeningHour = todaysOpeningHour+24; //assumes same opening hours every day
			if (arrivalTime < todaysOpeningHour) {
				waitingTime = todaysOpeningHour - arrivalTime;
			}
			else if (arrivalTime >= todaysClosingHour) {
				waitingTime = tomorrowsOpeningHour - arrivalTime;
			}
			//assumption: no installations have a service time greater than one working day
			else if (arrivalTime + installation.getServiceTime() > todaysClosingHour) {
				waitingTime = tomorrowsOpeningHour - todaysClosingHour; //add a night
			}
			double nextDepartureTime = arrivalTime + installation.getServiceTime() + waitingTime;
			double nextCapacityUsed = label.getCapacityUsed() + installation.getDemandPerVisit();
			//check that the solution is feasible
			if (nextDepartureTime <= maxDuration && nextCapacityUsed <= vessel.getCapacity()) {
				double nextCost = currentCost + (sailingTime*vessel.getFuelCostSailing()) + ((waitingTime+installation.getServiceTime())*vessel.getFuelCostInstallation());
				return new Label(visitedSum, nextCost, nextCapacityUsed, nextDepartureTime, 0, installation, label);
			}
			else {
				return null;
			}
		}
	}

}
