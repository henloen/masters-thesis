package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.protocols.FitnessEvaluationProtocol;

public class PenaltyAdjustmentProtocol {
	
	private int solutionsSincePenaltyAdjustment;
	private int capacityFeasibleSolutions;
	private int durationFeasibleSolutions;
	private int numberOfInstallationsFeasibleSolutions;
	
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
	}
	
	public void adjustPenalties(FitnessEvaluationProtocol fitnessProtocol){
		if (solutionsSincePenaltyAdjustment == 100){
			adjustCapacityPenalty(fitnessProtocol);
			adjustDurationPenalty(fitnessProtocol);
			adjustNumberOfInstallationsPenalty(fitnessProtocol);
		}
		solutionsSincePenaltyAdjustment = 0;
		capacityFeasibleSolutions = 0;
		durationFeasibleSolutions = 0;
		numberOfInstallationsFeasibleSolutions = 0;
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
