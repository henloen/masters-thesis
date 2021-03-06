package hgsadc;

import hgsadc.implementations.DiversificationAndStoppingStandard;
import hgsadc.implementations.EducationPersistence;
import hgsadc.implementations.EducationStandard;
import hgsadc.implementations.FeasibleFleetChecker;
import hgsadc.implementations.FitnessEvaluationMultiObjective;
import hgsadc.implementations.FitnessEvaluationStandard;
import hgsadc.implementations.GenoToPhenoConverterMultiObjective;
import hgsadc.implementations.GenoToPhenoConverterStandard;
import hgsadc.implementations.InitialPopulationAimed;
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
import java.util.Set;


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
	private FeasibleFleetChecker feasibleFleetChecker;
	
	private StatisticsHandler statisticsHandler;
	
	public HGSprocesses(ProblemData problemData) {
		this.problemData = problemData;
		selectProtocols();
	}
	
	public boolean isFeasibleFleet() {
		return feasibleFleetChecker.isFeasibleFleet(problemData);
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
		if (!individual.isFeasible()){
			double randomDouble = new Random().nextDouble();
			if (randomDouble < probability) {
				int penaltyMultiplier = 10;
				educationProtocol.repairEducate(individual, penaltyMultiplier);
				
				if (!individual.isFeasible()){
					penaltyMultiplier = 100;
					educationProtocol.repairEducate(individual, penaltyMultiplier);
				}
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
	
	public boolean isStoppingIteration() {
		return diversificationProtocol.isStoppingIteration();
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
		selectDiversificationAndStoppingProtocol();
		selectSurvivorSelectionProtocol();
		statisticsHandler = new StatisticsHandler(problemData, fitnessEvaluationProtocol);
		initializeFeasibleFleetChecker();
	}

	private void initializePenaltyAdjustmentProtocol() {
		double targetFeasibleProportion = problemData.getHeuristicParameterDouble("Reference proportion of feasible individuals");
		penaltyAdjustmentProtocol = new PenaltyAdjustmentProtocol(targetFeasibleProportion);
	}
	
	private void initializeFeasibleFleetChecker() {
		feasibleFleetChecker = new FeasibleFleetChecker();
	}

	private void selectInitialPopulationProtocol() {
		switch (problemData.getHeuristicParameters().get("Initial population heuristic")) {
			case "standard": initialPopulationProtocol = new InitialPopulationStandard(problemData);
				break;
			case "aimed": initialPopulationProtocol = new InitialPopulationAimed(problemData);
				break;
			default: initialPopulationProtocol = null;
				break;
		}
	}
	
	private void selectGenoToPhenoConverterProtocol() {
		switch (problemData.getHeuristicParameters().get("Genotype to phenotype converter protocol")) {
			case "Cost": genoToPhenoConverterProtocol = new GenoToPhenoConverterStandard(problemData);
				break;
			case "Cost+Persistence":
			case "Cost+Robustness":
			case "Cost+Persistence+Robustness":
				genoToPhenoConverterProtocol = new GenoToPhenoConverterMultiObjective(problemData);
				break;
			default: genoToPhenoConverterProtocol = null;
				break;
		}
	}
	
	private void selectFitnessEvaluationProtocol() {
		switch (problemData.getHeuristicParameters().get("Fitness evaluation protocol")) {
			case "Cost": fitnessEvaluationProtocol = new FitnessEvaluationStandard(problemData);
				break;
			case "Cost+Persistence" :
			case "Cost+Robustness" :
			case "Cost+Persistence+Robustness":
				fitnessEvaluationProtocol = new FitnessEvaluationMultiObjective(problemData);
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
			case "Cost": educationProtocol = new EducationStandard(problemData, fitnessEvaluationProtocol, penaltyAdjustmentProtocol, genoToPhenoConverterProtocol);
				break;
			case "Cost+Persistence":	
				educationProtocol = new EducationPersistence(problemData, fitnessEvaluationProtocol, penaltyAdjustmentProtocol, genoToPhenoConverterProtocol);
				break;
			default: educationProtocol = null;
				break;
		}
	}
	
	private void selectDiversificationAndStoppingProtocol() {
		switch (problemData.getHeuristicParameters().get("Diversification protocol")) {
			case "standard" :
				int iterationsBeforeDiversify = problemData.getHeuristicParameterInt("Iterations before diversify");
				int maxIterationsWithoutImprovement = problemData.getHeuristicParameterInt("Max iterations without improvement");
				int maxTime = problemData.getHeuristicParameterInt("Max time");
				diversificationProtocol = new DiversificationAndStoppingStandard(iterationsBeforeDiversify, maxIterationsWithoutImprovement, maxTime);
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
	
	public void recordDiversification(int iteration) {
		diversificationProtocol.addDiversification(iteration);
		diversificationProtocol.resetDiversificationCounter();
	}

	public String getRunStatistics() {
		String stats = "";
		stats += "Diversifications happened at iteration: " + diversificationProtocol.getDiversificationNumbers();
		stats += "\nNumber of crossover restarts: " + reproductionProtocol.getNumberOfCrossoverRestarts();
		return stats;
	}
	
	public void recordRunStatistics(int iteration, ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation, Individual bestIndividual) {
			statisticsHandler.recordRunStatistics(iteration, feasiblePopulation, infeasiblePopulation, bestIndividual);
	}
	
	public void exportRunStatistics(String outputFileName, long runningTime, Individual bestFeasibleIndividual, String[] args) {
		String argsList = "";
		for (int i=0; i<args.length;i++) {
			argsList += args[i];
			if (i < args.length-1) {
				argsList += " ";
			}
		}
		statisticsHandler.exportStatistics(outputFileName, runningTime, bestFeasibleIndividual,
				diversificationProtocol.getDiversificationNumbers(), reproductionProtocol.getNumberOfCrossoverRestarts(),
				initialPopulationProtocol.getNumberOfConstructionHeuristicRestarts(), argsList);
	}
	
	public void exportRunStatistics(String outputFileName, long runningTime, Set<Individual> paretoFront) {
		statisticsHandler.exportStatistics(outputFileName, runningTime, paretoFront,
				diversificationProtocol.getDiversificationNumbers(), reproductionProtocol.getNumberOfCrossoverRestarts(),
				initialPopulationProtocol.getNumberOfConstructionHeuristicRestarts());
	}
	
	public ArrayList<Individual> getClones(ArrayList<Individual> subpopulation){
		return survivorSelectionProtocol.getClones(subpopulation, fitnessEvaluationProtocol);
	}

	public void updateIterationsSinceImprovementCounter(boolean improvingSolutionFound) {
		diversificationProtocol.updateIterationsSinceImprovementCounter(improvingSolutionFound);
	}



}