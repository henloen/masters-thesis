package hgsadc;

import hgsadc.implementations.GenotypeHGS;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.util.ArrayList;
import java.util.Comparator;
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
	private String[] args;
	
	private int iteration;
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize(args);
		boolean feasibleFleet = true;
		int vesselsRemoved = 0;
		while (feasibleFleet) {
			if (main.isFeasibleFleet(vesselsRemoved)) {
				vesselsRemoved ++;
			}
			else {
				feasibleFleet = false;
			}
		}
		main.fullHGSADCrun(vesselsRemoved-1);
	}
	
	private boolean isFeasibleFleet(int vesselsRemoved) {
		problemData = io.readData(vesselsRemoved, args);
		changeSystemArgumentParameters(problemData);
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
	
	private void fullHGSADCrun(int vesselsRemoved) {
		problemData = io.readData(vesselsRemoved, args);
		changeSystemArgumentParameters(problemData);
		problemData.generatePatterns(); 
		processes = new HGSprocesses(problemData);
		
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		iteration = 1;
		bestFeasibleIndividual = null;
		
		problemData.printProblemData();
		
		System.out.println("Creating initial population...");
		createInitialPopulation();
		runEvolutionaryLoop();
		terminate();
	}
	
	private void changeSystemArgumentParameters(ProblemData problemData) {
		System.out.println("Before change:");
		System.out.println(problemData.getHeuristicParameters());
		System.out.println(problemData.getProblemInstanceParameters());
		for (String arg : args) {
			String[] splittedArg = arg.split("=");
			String parameter = splittedArg[0];
			String value = splittedArg[1];
			if (problemData.getHeuristicParameters().containsKey(parameter)) {
				problemData.getHeuristicParameters().put(parameter, value);
			}
			else {
				problemData.getProblemInstanceParameters().put(parameter, value);
			}
		}
		System.out.println("After change:");
		System.out.println(problemData.getHeuristicParameters());
		System.out.println(problemData.getProblemInstanceParameters());
	}
	
	
	private void initialize(String[] args) {
		startTime = System.nanoTime();
		io = new IO(inputFileName);
		this.args = args;
	}
	
	private void terminate() {
		System.out.println("Final population:");
		printPopulation();
		printRunStatistics();
		printBestSolutions();
		stopTime = System.nanoTime();
		processes.exportRunStatistics(outputFileName, stopTime - startTime, bestFeasibleIndividual, args);
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
		addToSubpopulation(offspring);
		processes.adjustPenaltyParameters(feasiblePopulation, infeasiblePopulation);
		updateCounters();
		if (processes.isDiversifyIteration()) {
			diversify(feasiblePopulation, infeasiblePopulation);
		}
		//printPopulation();
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
	
	private void updateCounters() {
		Individual bestFeasibleIndividual = getBestSolution(feasiblePopulation);
		double bestPenalizedCost;
		
		if (bestFeasibleIndividual == null){
			bestPenalizedCost = Double.POSITIVE_INFINITY;
		}
		else {
			bestPenalizedCost = bestFeasibleIndividual.getPenalizedCost();
		}
		processes.updateDiversificationCounter(bestPenalizedCost);
	}

	private void diversify(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		/*  1. Eliminate all but the best third of each subpopulation in terms of penalized cost
		 *  2. Create 4� new individual 
		 */
		System.out.println("Diversifying...");
		// 1st argument is subpopulation to kill, 2nd is the other subpopulation (needed for cleaning up in the fitnessprotocol)
		killLessValuableIndividuals(feasiblePopulation, infeasiblePopulation, 2/3);  
		killLessValuableIndividuals(infeasiblePopulation, feasiblePopulation, 2/3);
		
		System.out.println("Mass murder of less valuable individuals complete");
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
	
	private void addToSubpopulation(Individual individual) {
		if (individual.isFeasible()) {
			if ((bestFeasibleIndividual == null)
				|| (individual.getPenalizedCost() < bestFeasibleIndividual.getPenalizedCost())) {
				bestFeasibleIndividual = individual;
			}
			feasiblePopulation.add(individual);
		}
		else {
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
