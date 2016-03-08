package ea.svpp;

import java.util.HashMap;

import ea.Individual;
import ea.Population;
import ea.protocols.FitnessEvaluationProtocol;
import voyageGenerationDP.Voyage;

public class FitnessSVPP implements FitnessEvaluationProtocol {
	
	private ProblemDataSVPP problemData;
	
	private String fitnessFunction;
	public HashMap<Integer, Integer> minimumSlack; // Key is voyage duration, value is minimum slack for voyage to be robust
	
	
	public FitnessSVPP(ProblemDataSVPP problemData, String fitnessFunction){
		this.problemData = problemData;
		this.fitnessFunction = fitnessFunction;
	}
	
	public int getScheduleCost(PhenotypeSVPP schedule){
		int PSVCharterCost = getPSVCharterCost(schedule);
		int voyageCost = getVoyageCost(schedule);
		
		return PSVCharterCost + voyageCost;
	}
	
	private int getVoyageCost(PhenotypeSVPP schedule) {
		int voyageCost = 0;
		
		for (int[] vrt : schedule.getVoyagesSailed()) {
			int v = vrt[0];
			int r = vrt[1];
			int t = vrt[2];
			
			voyageCost += problemData.voyageSet.get(v).getCost();
		}
		return voyageCost;
	}

	public int getPSVCharterCost(PhenotypeSVPP schedule){
		int charterCost = 0;
		for (Integer PSV : schedule.getCharteredPSVs()) {
			charterCost += problemData.vessels.get(PSV).getTimeCharterCost();
		}
		return charterCost;
	}
	
	public int getScheduleRobustness(PhenotypeSVPP schedule){
		int nRobustVoyages = 0;
		int nVoyages = 0;
		
		for (int[] vrt : schedule.getVoyagesSailed()) {
			nVoyages++;
			int v = vrt[0];
			int r = vrt[1];
			int t = vrt[2];
			
			Voyage voyage = problemData.voyageSet.get(v);
			int voyageDuration = voyage.getDuration();
			int slack = (int) voyage.getSlack();
			
			if (slack >= minimumSlack.get(voyageDuration)){
				nRobustVoyages++;
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
			PhenotypeSVPP schedule = (PhenotypeSVPP) individual.getPhenotype();
			individual.setFitness(-Double.valueOf(getScheduleCost(schedule)));
			break;
		default:
			individual.setFitness(0);
			break;
		}
	}
}
