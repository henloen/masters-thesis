package hgsadc;

import hgsadc.implementations.GenotypeHGS;
import hgsadc.implementations.Dominator;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class HGSmain {
	
	private String inputFileName = "data/hgs/input/input data hgs.xls",
			outputFileName = "data/hgs/output/";
	private IO io;
	private ProblemData problemData;
	private HGSprocesses processes;
	private ArrayList<Individual> feasiblePopulation, infeasiblePopulation;
	private Individual bestFeasibleIndividual;
	private long startTime, stopTime;
	
	private Set<Individual> paretoFront;
	private String[] args;
	
	private int iteration;
	
	public static int PERSISTENCE_EDUCATIONS = 0;
	public static int COST_EDUCATIONS = 0;
	public static int PERSISTENCE_COST_EDUCATIONS = 0;
	public static int COST_PERSISTENCE_EDUCATIONS = 0;
	
	public static int UNFEASIBLE_PERSISTENCE_EDUCATIONS = 0;
	public static int CUM_PERSISTENCE_REDUCED = 0;
	public static int NOFEASIBLEVESSELDAYS = 0;
	public static int NODAYSWITHTOOFEWVOYAGES = 0;
	public static int NODAYSWITHTOOMANYVOYAGES = 0;
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize(args);
		int vesselsRemoved = 0;
		
		if (main.isVariableFleetProblem()){ // TODO How to handle this with multiobjective? Possible to run multiple times and save all non-dominated solutions
			boolean feasibleFleet = true;
			while (feasibleFleet) {
				if (main.isFeasibleFleet(vesselsRemoved, args)) {
					vesselsRemoved++;
				}
				else {
					feasibleFleet = false;
				}
			}
		}
		main.fullHGSADCrun(vesselsRemoved-1, args);
		
//		System.out.println("=======================================================");
//		System.out.println("Persistence educations: " + PERSISTENCE_EDUCATIONS);
//		System.out.println("Cost educations: " + COST_EDUCATIONS);
//		System.out.println("Cost+Persistence educations: " + COST_PERSISTENCE_EDUCATIONS);
//		System.out.println("Persistence+Cost educations: " + PERSISTENCE_COST_EDUCATIONS);
//		System.out.println();
//		System.out.println("Failed move voyage-educations: " + UNFEASIBLE_PERSISTENCE_EDUCATIONS);
//		System.out.println("No days with too few: " + NODAYSWITHTOOFEWVOYAGES);
//		System.out.println("No days with too many: " + NODAYSWITHTOOMANYVOYAGES);
//		System.out.println("No feasible vessel days: " + NOFEASIBLEVESSELDAYS);
//		System.out.println();
//		System.out.println("Persistence/education: " + (double) CUM_PERSISTENCE_REDUCED/(PERSISTENCE_COST_EDUCATIONS + COST_PERSISTENCE_EDUCATIONS + PERSISTENCE_EDUCATIONS));
//		
	}
	
	private boolean isVariableFleetProblem() {
		return Boolean.parseBoolean(problemData.getHeuristicParameters().get("Variable fleet"));
	}

	private boolean isFeasibleFleet(int vesselsRemoved, String[] changeParameters) {
		problemData = io.readData(vesselsRemoved, changeParameters);
		problemData.generatePatterns(); 
		System.out.println("Testing fleet with " + problemData.getVessels().size() + " vessels");
		
		processes = new HGSprocesses(problemData);
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		iteration = 1;
		
		if (processes.isFeasibleFleet()) {
			createInitialPopulation();
			return runFleetAdjustmentEvolutionaryLoop();
		}
		else {
			return false;
		}
	}
	
	private void fullHGSADCrun(int vesselsRemoved, String[] changeParameters) {
		problemData = io.readData(vesselsRemoved, changeParameters);
		
		if (problemData.dominationCriteria != null){ // If multi-objective
			paretoFront = new HashSet<>();
		}
		
		problemData.generatePatterns(); 
		processes = new HGSprocesses(problemData);
		
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		iteration = 1;
		bestFeasibleIndividual = null;
		
		problemData.printProblemData();
		
		System.out.println("Creating initial population...");
		createInitialPopulation();
		processes.updateIterationsSinceImprovementCounter(true); // Resets counters
		runEvolutionaryLoop();
		terminate();
	}

	private void initialize(String[] changeParameters) {
		startTime = System.nanoTime();
		io = new IO(inputFileName);
		this.args = changeParameters;
		problemData = io.readData(0, changeParameters);
	}

	private void terminate() {
		System.out.println("Final population:");
		printPopulation();
		printRunStatistics();
		printBestSolutions();
		stopTime = System.nanoTime();
		
		if (problemData.dominationCriteria == null){ // Single-objective problem
			processes.exportRunStatistics(outputFileName, stopTime - startTime, bestFeasibleIndividual, args);
		}
		else {
			processes.exportRunStatistics(outputFileName, stopTime - startTime, paretoFront);
		}
	}

	private void createInitialPopulation(){
		int populationSize = problemData.getHeuristicParameterInt("Population size");
		int initialPopulationSize = 4 * populationSize;
		for (int i = 0; i < initialPopulationSize; i++) {
			Individual individual = processes.createIndividual();
			processes.educate(individual);
			if (! individual.isFeasible()) {
				processes.repair(individual, 0.5);
			}
			addToSubpopulation(individual);
		}
	}
	
	private boolean runFleetAdjustmentEvolutionaryLoop() {
		int maxIterations = problemData.getHeuristicParameterInt("Max iterations without improvement");
		while (! stoppingCriterionFleetAdjustment(maxIterations)) {
			System.out.println("Iteration " + iteration);
			doIteration();
			iteration++;
		}
		return (feasiblePopulation.size() > 0);
	}
	
	private void runEvolutionaryLoop() {
		processes.recordRunStatistics(0, feasiblePopulation, infeasiblePopulation, bestFeasibleIndividual);//record initial population
		while (! stoppingCriterion()) {
			System.out.println("Iteration " + iteration);
			doIteration();
			processes.recordRunStatistics(iteration, feasiblePopulation, infeasiblePopulation, bestFeasibleIndividual);
			iteration++;
		}
		System.out.println();
	}
	
	private void doIteration() {
		ArrayList<Individual> parents = processes.selectParents(feasiblePopulation, infeasiblePopulation);
		Individual offspring = processes.generateOffspring(parents);
		processes.educate(offspring);
		processes.repair(offspring);
		boolean isImprovingSolution = addToSubpopulation(offspring);
		processes.updateIterationsSinceImprovementCounter(isImprovingSolution);
		processes.adjustPenaltyParameters(feasiblePopulation, infeasiblePopulation);
		if (processes.isDiversifyIteration()) {
			diversify(feasiblePopulation, infeasiblePopulation);
		}
//		printPopulation();
	}
	
	private boolean stoppingCriterionFleetAdjustment(int maxIterations){
		return (feasiblePopulation.size() > 0) || iteration > maxIterations;
	}

	private boolean stoppingCriterion() {
		return processes.isStoppingIteration();
		
		
		/*
		double bestKnownSailingCost = Double.parseDouble(problemData.getProblemInstanceParameters().get("Best known sailing cost"));
		double gap = problemData.getHeuristicParameterDouble("Gap from best known solution");
		if (gap == 0) {
			gap = 0.0001; //to avoid that the algorithm can't find BKS due to rounding errors 
		}
		if (feasiblePopulation.size() > 0) {
			double bestFeasibleSailingCost = getBestSolution(feasiblePopulation).getPenalizedCost();
			return (iteration > maxIterations)
					|| ((bestFeasibleSailingCost - bestKnownSailingCost) <= (gap*bestKnownSailingCost));
		}
		else {
			return (iteration > maxIterations);
		}
		*/
	}
	
	private void diversify(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		/*  1. Eliminate all but the best third of each subpopulation in terms of penalized cost
		 *  2. Create 4� new individual 
		 */
		System.out.println("Diversifying...");
		// 1st argument is subpopulation to kill, 2nd is the other subpopulation (needed for cleaning up in the fitnessprotocol)
		killLessValuableIndividuals(feasiblePopulation, infeasiblePopulation, 2/3);  
		killLessValuableIndividuals(infeasiblePopulation, feasiblePopulation, 2/3);
		
		System.out.println("Breeding new population...");
		createInitialPopulation();
		
		processes.recordDiversification(iteration);
	}
	
	/**
	 * Genocide function
	 * @param subpopulation Subpopulation to murder
	 * @param otherSubpopulation Rest of the population
	 * @param proportionToKill Proportion of subpopulation to kill
	 */
	private void killLessValuableIndividuals(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation, double proportionToKill){
		subpopulation.sort(Utilities.getPenalizedCostComparator().reversed()); // Sorts population from highest to lowest penalized cost
		int nIndividualsToKill = (int) Math.round(subpopulation.size() * proportionToKill);
		ArrayList<Individual> individualsToKill = new ArrayList<>(subpopulation.subList(0, nIndividualsToKill));
		removeFromSubpopulation(subpopulation, otherSubpopulation, individualsToKill);
	}
	
	private void removeFromSubpopulation(ArrayList<Individual> subpopulation,
			ArrayList<Individual> otherSubpopulation, ArrayList<Individual> individualsToKill) {
		
		for (Individual individual : individualsToKill) {
			HGSmain.removeFromSubpopulation(subpopulation, individual, otherSubpopulation, processes.fitnessEvaluationProtocol, false);
		}
	}

	private void checkSubpopulationSize(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation) {
		int maxSubpopulationSize = problemData.getHeuristicParameterInt("Population size") 
				+ problemData.getHeuristicParameterInt("Number of offspring in a generation");
		if (subpopulation.size() >= maxSubpopulationSize) {
			processes.survivorSelection(subpopulation, otherSubpopulation);
		}
	}
	
	private boolean addToSubpopulation(Individual individual) {
		boolean isImprovingSolution = false;

		if (individual.isFeasible()) {
			if (problemData.dominationCriteria == null){ // Single-objective problem
				if ((bestFeasibleIndividual == null)
						|| (individual.getPenalizedCost() < bestFeasibleIndividual.getPenalizedCost())) {
					bestFeasibleIndividual = individual;
					isImprovingSolution = true; // Improving solution found
				}
				else {
					isImprovingSolution = false; // No improving solution found
				}
			}
			else { // Multi-objective problem
				Set<Individual> dominatingIndividuals = Utilities.getIndidividualsDominating(individual, paretoFront, problemData.dominationCriteria);
				
//				System.out.println("=============== Current pareto front =======================");
//				for (Individual paretoInd : paretoFront){
//					System.out.println(paretoInd.getObjectiveValues());
//				}
//				System.out.println();
//				System.out.println(individual.getObjectiveValues() + " is dominated by: ");
//				for (Individual dom : dominatingIndividuals) {
//					System.out.println(dom.getObjectiveValues());
//				}
//				System.out.println();
				
				if (dominatingIndividuals.isEmpty()){
					Set<Individual> dominatedIndividuals = Utilities.getIndividualsDominatedBy(individual, paretoFront, problemData.dominationCriteria);
					
//					System.out.println(individual.getObjectiveValues() + " dominates: ");
//					for (Individual dom : dominatedIndividuals) {
//						System.out.println(dom.getObjectiveValues());
//					}
					paretoFront.removeAll(dominatedIndividuals);
					
					Set<Individual> clones = Utilities.getObjectiveClones(individual, paretoFront, problemData.dominationCriteria);
					
					if (clones.isEmpty()){
						paretoFront.add(individual);
						isImprovingSolution = true; // Improving solution found
					}
					else {
						isImprovingSolution = false; // New solution already in pareto front. No improving solution found
					}
					
				}
				else {
					isImprovingSolution = false; // No improving solution found
				}
			}
			feasiblePopulation.add(individual);
		}
		else {
			isImprovingSolution = false; // No improving solution found
			infeasiblePopulation.add(individual);
		}			
		processes.addDiversityDistance(individual);
		processes.updateBiasedFitness(feasiblePopulation, infeasiblePopulation);
		
		if (individual.isFeasible()) {
			checkSubpopulationSize(feasiblePopulation, infeasiblePopulation);
		}
		else {
			checkSubpopulationSize(infeasiblePopulation, feasiblePopulation);
		}
		
		return isImprovingSolution;
	}
	
	// 
	public static void removeFromSubpopulation(ArrayList<Individual> subpopulation, Individual individual, ArrayList<Individual> otherSubpopulation, FitnessEvaluationProtocol fitnessEvaluationProtocol, boolean updateFitness) {
		subpopulation.remove(individual);
		fitnessEvaluationProtocol.removeDiversityDistance(individual);
		
		if (updateFitness){ // Updates fitness for all individuals
			fitnessEvaluationProtocol.updateBiasedFitness(Utilities.getAllElements(subpopulation, otherSubpopulation));
		}
	}
	
	private void printPopulation() {
		System.out.println("Feasible subpopulation:");
		for (Individual individual : feasiblePopulation) {
			System.out.println("Individual " + individual.getFullText());
		}
		System.out.println("Infeasible subpopulation:");
		for (Individual individual : infeasiblePopulation) {
			System.out.println("Individual " + individual.getFullText());
		}
		System.out.println("");
	}
	
	private void printRunStatistics() {
		System.out.println("====================== Run complete ===========================");
		System.out.println("Number of iterations: " + (iteration-1));
		System.out.println(processes.getRunStatistics());
	}

	private Individual getBestSolution(ArrayList<Individual> subpopulation) {
		if (subpopulation.size() == 0){
			return null;
		}
		
		Comparator<Individual> penCostComparator = Utilities.getPenalizedCostComparator();
		subpopulation.sort(penCostComparator);
		return subpopulation.get(0);
	}

	private void printBestSolutions() {
		if (problemData.dominationCriteria != null){
			printParetoFront();
		}
		else {
			System.out.println("==================== Best feasible solution found ==========================");
			if (bestFeasibleIndividual == null){
				System.out.println("Tough luck, no feasible solutions in final population");
			}
			else {
				System.out.println(bestFeasibleIndividual.getFullText());
				System.out.println(bestFeasibleIndividual.getPhenotype().getScheduleString());
			}
			Individual bestInfeasibleSolution = getBestSolution(infeasiblePopulation);
			System.out.println("==================== Best infeasible solution found ==========================");
			if (bestInfeasibleSolution == null){
				System.out.println("Hmmm, no infeasible solutions in final population");
			}
			else {
				System.out.println(bestInfeasibleSolution.getFullText());
				System.out.println(bestInfeasibleSolution.getPhenotype().getScheduleString());
				GenotypeHGS geno = (GenotypeHGS) bestInfeasibleSolution.getGenotype();
				System.out.println(geno.getInstallationDeparturePatternChromosome());
				System.out.println(geno.getVesselDeparturePatternChromosome());
			}
			
		}
	}
	
	private void printParetoFront() {
		DecimalFormat df = new DecimalFormat("0");
		System.out.println("======================== Pareto front  ==========================================");
		for (Individual individual : paretoFront) {
			System.out.println("Cost : " + df.format(individual.getPenalizedCost()) + " Persistence: " + individual.getNumberOfChangesFromBaseline() + " Robustness: " + individual.getRobustness());
		}
		System.out.println();
		for (Individual individual : paretoFront){
			System.out.println(individual.getFullText());
			System.out.println("Type of education: " + individual.typeOfEducation);
			System.out.println(individual.getRobustnessString());
		}
	}

	// DEPRECATED
	private Set<Individual> getParetoFront(){
		ArrayList<Set<Individual>> paretoFronts = Utilities.nonDominatedSorting(feasiblePopulation, problemData.dominationCriteria);
		ArrayList<Individual> paretoFrontList = new ArrayList<>(paretoFronts.get(0));
		Set<Individual> paretoFrontWithoutClones = new HashSet<>(processes.getClones(paretoFrontList));
		
		return Utilities.removeObjectiveClones(paretoFrontWithoutClones, problemData.dominationCriteria);
	}
}
