package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.protocols.FitnessEvaluationProtocol;

public class PenaltyAdjustmentProtocol {
	
	private double solutionsSincePenaltyAdjustment;
	private double capacityFeasibleSolutions;
	private double durationFeasibleSolutions;
	private double numberOfInstallationsFeasibleSolutions;
	
	private double targetFeasibleProportion;
	
	public PenaltyAdjustmentProtocol(double targetFeasibleProportion) {
		this.targetFeasibleProportion = targetFeasibleProportion;
		this.solutionsSincePenaltyAdjustment = 0;
		this.capacityFeasibleSolutions = 0;
		this.durationFeasibleSolutions = 0;
		this.numberOfInstallationsFeasibleSolutions = 0;
	}
	
	public void countAddedIndividual(Individual individual){
		solutionsSincePenaltyAdjustment++;
		
		if (individual.getPhenotype().isCapacityFeasible()){
			capacityFeasibleSolutions++;
		}
		if (individual.getPhenotype().isDurationFeasible()){
			durationFeasibleSolutions++;
		}
		if (individual.getPhenotype().isNumberOfInstallationsFeasible()){
			numberOfInstallationsFeasibleSolutions++;
		}
		
		//System.out.println("Number of solutions since penalty adjustment: " + solutionsSincePenaltyAdjustment);
	}
	
	public void adjustPenalties(ArrayList<Individual> entirePopulation, FitnessEvaluationProtocol fitnessProtocol){
		if (solutionsSincePenaltyAdjustment >= 100){
			System.out.println("Adjusting penalty parameters...");
			
			adjustCapacityPenalty(fitnessProtocol);
			adjustDurationPenalty(fitnessProtocol);
			adjustNumberOfInstallationsPenalty(fitnessProtocol);
			
			System.out.println("New duration penalty: " + fitnessProtocol.getDurationViolationPenalty());
			System.out.println("New capacity penalty: " + fitnessProtocol.getCapacityViolationPenalty());
			System.out.println("New nInstallations penalty: " + fitnessProtocol.getNumberOfInstallationsPenalty());
			
			solutionsSincePenaltyAdjustment = 0;
			capacityFeasibleSolutions = 0;
			durationFeasibleSolutions = 0;
			numberOfInstallationsFeasibleSolutions = 0;
			
			fitnessProtocol.setPenalizedCostPopulation(entirePopulation);
		}
	}
	
	private void adjustCapacityPenalty(FitnessEvaluationProtocol fitnessProtocol){
		double capacityFeasibleProportion = capacityFeasibleSolutions/solutionsSincePenaltyAdjustment;
		if (capacityFeasibleProportion <= targetFeasibleProportion - 0.05){
			double currentPenalty = fitnessProtocol.getCapacityViolationPenalty();
			fitnessProtocol.setCapacityViolationPenalty(currentPenalty * 1.2);
		}
		else if (capacityFeasibleProportion >= targetFeasibleProportion + 0.05){
			double currentPenalty = fitnessProtocol.getCapacityViolationPenalty();
			fitnessProtocol.setCapacityViolationPenalty(currentPenalty * 0.85);
		}
	}
	
	private void adjustDurationPenalty(FitnessEvaluationProtocol fitnessProtocol){
		double durationFeasibleProportion = durationFeasibleSolutions/solutionsSincePenaltyAdjustment;
		if (durationFeasibleProportion <= targetFeasibleProportion - 0.05){
			double currentPenalty = fitnessProtocol.getDurationViolationPenalty();
			fitnessProtocol.setDurationViolationPenalty(currentPenalty * 1.2);
		}
		else if (durationFeasibleProportion >= targetFeasibleProportion + 0.05){
			double currentPenalty = fitnessProtocol.getDurationViolationPenalty();
			fitnessProtocol.setDurationViolationPenalty(currentPenalty * 0.85);
		}
	}
	
	private void adjustNumberOfInstallationsPenalty(FitnessEvaluationProtocol fitnessProtocol){
		double numberOfInstallationsFeasibleProportion = numberOfInstallationsFeasibleSolutions/solutionsSincePenaltyAdjustment;
		if (numberOfInstallationsFeasibleProportion <= targetFeasibleProportion - 0.05){
			double currentPenalty = fitnessProtocol.getNumberOfInstallationsPenalty();
			fitnessProtocol.setNumberOfInstallationsViolationPenalty(currentPenalty * 1.2);
		}
		else if (numberOfInstallationsFeasibleProportion >= targetFeasibleProportion + 0.05){
			double currentPenalty = fitnessProtocol.getNumberOfInstallationsPenalty();
			fitnessProtocol.setNumberOfInstallationsViolationPenalty(currentPenalty * 0.85);
		}
	}

	
}
