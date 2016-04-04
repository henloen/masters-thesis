package hgsadc;

import hgsadc.implementations.DiversificationStandard;
import hgsadc.implementations.EducationStandard;
import hgsadc.implementations.FitnessEvaluationStandard;
import hgsadc.implementations.GenoToPhenoConverterStandard;
import hgsadc.implementations.InitialPopulationStandard;
import hgsadc.implementations.ParentSelectionBinaryTournament;
import hgsadc.implementations.PenaltyAdjustmentProtocol;
import hgsadc.implementations.ReproductionStandard;
import hgsadc.implementations.StatisticsHandler;
import hgsadc.implementations.SurvivorSelectionStandard;
import hgsadc.protocols.DiversificationProtocol;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.InitialPopulationProtocol;
import hgsadc.protocols.ParentSelectionProtocol;
import hgsadc.protocols.ReproductionProtocol;
import hgsadc.protocols.SurvivorSelectionProtocol;

import java.util.ArrayList;
import java.util.Random;


public class HGSprocesses {
	
	private ProblemData problemData;
	
	private InitialPopulationProtocol initialPopulationProtocol;
	private GenoToPhenoConverterProtocol genoToPhenoConverterProtocol;
	FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private ParentSelectionProtocol parentSelectionProtocol;
	private ReproductionProtocol reproductionProtocol;
	private EducationProtocol educationProtocol;
	private DiversificationProtocol diversificationProtocol;
	private SurvivorSelectionProtocol survivorSelectionProtocol;
	private PenaltyAdjustmentProtocol penaltyAdjustmentProtocol;
	
	private StatisticsHandler statisticsHandler;
	
	public HGSprocesses(ProblemData problemData) {
		this.problemData = problemData;
		selectProtocols();
	}
	
	public Individual createIndividual() {
		Individual individual = initialPopulationProtocol.createIndividual();
		convertGenotypeToPhenotype(individual);
		return individual;
	}
	
	public void convertGenotypeToPhenotype(Individual individual) {
		genoToPhenoConverterProtocol.convertGenotypeToPhenotype(individual);
		fitnessEvaluationProtocol.setPenalizedCostIndividual(individual);
	}
	
	public void updateBiasedFitness(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		fitnessEvaluationProtocol.updateBiasedFitness(entirePopulation);
	}
	
	public void addDiversityDistance(Individual individual) {
		fitnessEvaluationProtocol.addDiversityDistance(individual);
	}
	
	public ArrayList<Individual> selectParents(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		return parentSelectionProtocol.selectParents(entirePopulation);
	}

	public Individual generateOffspring(ArrayList<Individual> parents) {
		Individual individual = reproductionProtocol.crossover(parents);
		convertGenotypeToPhenotype(individual);
		if (!individual.isFeasible()){
			repair(individual, problemData.getHeuristicParameterDouble("Repair rate"));
		}
		
		return individual;
	}

	public void educate(Individual individual) {
		educationProtocol.educate(individual);
	}
	
	public void adjustPenaltyParameters(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		penaltyAdjustmentProtocol.adjustPenalties(entirePopulation, fitnessEvaluationProtocol);
	}
	
	public void repair(Individual individual, double probability) {
		double randomDouble = new Random().nextDouble();
		if (probability < randomDouble) {
			
			int penaltyMultiplier = 10;
			educationProtocol.repairEducate(individual, penaltyMultiplier);
			
			if (!individual.isFeasible()){
				penaltyMultiplier = 100;
				educationProtocol.repairEducate(individual, penaltyMultiplier);
			}
		}
	}
	
	public void repair(Individual individual) {
		double probability = problemData.getHeuristicParameterDouble("Repair rate");
		repair(individual, probability);
	}

	public boolean isDiversifyIteration() {
		return diversificationProtocol.isDiversifyIteration();
	}
	
	public void survivorSelection(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation) {
		survivorSelectionProtocol.selectSurvivors(subpopulation, otherSubpopulation, fitnessEvaluationProtocol);
	}
	
	
	private void selectProtocols() {
		selectInitialPopulationProtocol();
		selectGenoToPhenoConverterProtocol();
		selectFitnessEvaluationProtocol();
		selectParentSelectionProtocol();
		selectReproductionProtocol();
		initializePenaltyAdjustmentProtocol();
		selectEducationProtocol();
		selectDiversificationProtocol();
		selectSurvivorSelectionProtocol();
		statisticsHandler = new StatisticsHandler(problemData, fitnessEvaluationProtocol);
	}

	private void initializePenaltyAdjustmentProtocol() {
		double targetFeasibleProportion = problemData.getHeuristicParameterDouble("Reference proportion of feasible individuals");
		penaltyAdjustmentProtocol = new PenaltyAdjustmentProtocol(targetFeasibleProportion);
	}

	private void selectInitialPopulationProtocol() {
		switch (problemData.getHeuristicParameters().get("Initial population heuristic")) {
			case "standard": initialPopulationProtocol = new InitialPopulationStandard(problemData);
				break;
			default: initialPopulationProtocol = null;
				break;
		}
	}
	
	private void selectGenoToPhenoConverterProtocol() {
		switch (problemData.getHeuristicParameters().get("Genotype to phenotype converter protocol")) {
			case "standard": genoToPhenoConverterProtocol = new GenoToPhenoConverterStandard(problemData);
				break;
			default: genoToPhenoConverterProtocol = null;
				break;
		}
	}
	
	private void selectFitnessEvaluationProtocol() {
		switch (problemData.getHeuristicParameters().get("Fitness evaluation protocol")) {
			case "standard": fitnessEvaluationProtocol = new FitnessEvaluationStandard(problemData);
				break;
			default: fitnessEvaluationProtocol = null;
				break;
		}
	}
	
	private void selectParentSelectionProtocol() {
		switch (problemData.getHeuristicParameters().get("Parent selection protocol")) {
			case "binary tournament": parentSelectionProtocol = new ParentSelectionBinaryTournament();
				break;
			default: parentSelectionProtocol = null;
				break;
		}
	}

	private void selectReproductionProtocol() {
		switch (problemData.getHeuristicParameters().get("Reproduction protocol")) {
			case "standard": reproductionProtocol = new ReproductionStandard(problemData, fitnessEvaluationProtocol);
				break;
			default: reproductionProtocol = null;
				break;
		}
	}

	private void selectEducationProtocol() {
		switch (problemData.getHeuristicParameters().get("Education protocol")) {
			case "standard": educationProtocol = new EducationStandard(problemData, fitnessEvaluationProtocol, penaltyAdjustmentProtocol);
				break;
			default: educationProtocol = null;
				break;
		}
	}
	
	private void selectDiversificationProtocol() {
		switch (problemData.getHeuristicParameters().get("Diversification protocol")) {
			case "standard": diversificationProtocol = new DiversificationStandard(problemData.getHeuristicParameterInt("Iterations before diversify"));
				break;
			default: diversificationProtocol = null;
				break;
		}
	}

	private void selectSurvivorSelectionProtocol() {
		switch (problemData.getHeuristicParameters().get("Survivor selection protocol")) {
			case "standard": survivorSelectionProtocol = new SurvivorSelectionStandard(problemData);
				break;
			default: survivorSelectionProtocol = null;
				break;
		}	
	}
	
	public void recordDiversification() {
		diversificationProtocol.addDiversification();
		diversificationProtocol.resetCounter();
	}

	public void updateDiversificationCounter(double bestPenalizedCost) {
		diversificationProtocol.updateCounterSinceLastImprovement(bestPenalizedCost);
		
	}

	public String getRunStatistics() {
		String stats = "";
		stats += "Number of diversifications: " + diversificationProtocol.getNumberOfDiversifications();
		stats += "\nNumber of crossover restarts: " + reproductionProtocol.getNumberOfCrossoverRestarts();
		return stats;
	}
	
	public void recordRunStatistics(int iteration, ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
			statisticsHandler.recordRunStatistics(iteration, feasiblePopulation, infeasiblePopulation);
	}
	
	public void exportRunStatistics(String outputFileName) {
		statisticsHandler.exportStatistics(outputFileName);
	}



}