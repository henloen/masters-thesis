package ea.svpp;

import java.util.HashMap;

import ea.Individual;
import ea.Population;
import ea.protocols.FitnessEvaluationProtocol;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public class FitnessSVPP implements FitnessEvaluationProtocol {
	
	private ProblemDataSVPP problemData;
	
	private String fitnessFunction;
	public HashMap<Integer, Integer> minimumSlack; // Key is voyage duration, value is minimum slack for voyage to be robust
	
	
	public FitnessSVPP(ProblemDataSVPP problemData, String fitnessFunction){
		this.problemData = problemData;
		this.fitnessFunction = fitnessFunction;
	}
	
	public int getScheduleCost(GenotypeSVPP genotype){
		int PSVCharterCost = getPSVCharterCost(genotype);
		int voyageCost = getVoyageCost(genotype);
		
		return PSVCharterCost + voyageCost;
	}
	
	private int getVoyageCost(GenotypeSVPP genotype) {
		int voyageCost = 0;
		
		for (Vessel vessel : genotype.getCharteredVessels()){
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				Voyage voyage = genotype.getDeparture(vessel, day);
				if (voyage != null){
					voyageCost += voyage.getCost();
					day += voyage.getDuration();
				}
				else day++;
			}
		}
		return voyageCost;
	}

	public int getPSVCharterCost(GenotypeSVPP genotype){
		int charterCost = 0;
		for (Vessel vessel : genotype.getCharteredVessels()) {
			charterCost += vessel.getTimeCharterCost();
		}
		return charterCost;
	}
	
	public int getScheduleRobustness(GenotypeSVPP genotype){
		int nRobustVoyages = 0;
		int nVoyages = 0;
		
		for (Vessel vessel : genotype.getCharteredVessels()){
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				Voyage voyage = genotype.getDeparture(vessel, day);
				if (voyage != null){
					int voyageDuration = voyage.getDuration();
					int slack = (int) voyage.getSlack();
					if (slack >= minimumSlack.get(voyageDuration)) nRobustVoyages++;
				}
			}
		}
		return nRobustVoyages/nVoyages;
	}
			
	public int getSchedulePersistence(PhenotypeSVPP schedule){
		// TODO: Auto-generated stub
		
		
		return 0;
	}

	@Override
	public void evaluateFitness(Population population) {
		for (Individual individual : population.getIndividuals()) {
			evaluateIndividual(individual);
		}
	}
	
	private void evaluateIndividual(Individual individual){
		switch (fitnessFunction){
		
		case "Cost":
			GenotypeSVPP schedule = (GenotypeSVPP) individual.getGenotype();
			individual.setFitness(-Double.valueOf(getScheduleCost(schedule)));
			break;
		default:
			individual.setFitness(0);
			break;
		}
	}
}
