package hgsadc;

import java.util.ArrayList;
import java.util.Comparator;

import hgsadc.protocols.FitnessEvaluationProtocol;

public class HGSmain {
	
	private String inputFileName = "data/hgs/input/input data hgs.xls",
			outputFileName = "data/hgs/output/";
	private IO io;
	private ProblemData problemData;
	private HGSprocesses processes;
	private ArrayList<Individual> feasiblePopulation, infeasiblePopulation;
	private Individual bestFeasibleIndividual;
	private long startTime, stopTime;
	
	private int iteration;
	
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize();
		
		System.out.println("Creating initial population...");
		main.createInitialPopulation();
		main.runEvolutionaryLoop();
		main.terminate();
		
	}
	
	private void initialize() {
		io = new IO(inputFileName);
		problemData = io.readData();
		problemData.generatePatterns();
		processes = new HGSprocesses(problemData);
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		iteration = 1;
		
		problemData.printProblemData();
		startTime = System.nanoTime();
	}
	
	private void terminate() {
		System.out.println("Final population:");
		printPopulation();
		printRunStatistics();
		printBestSolutions();
		stopTime = System.nanoTime();
		processes.exportRunStatistics(outputFileName, stopTime - startTime, bestFeasibleIndividual);
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
	
	private void runEvolutionaryLoop() {
		processes.recordRunStatistics(0, feasiblePopulation, infeasiblePopulation);//record initial population
		while (! stoppingCriterion()) {
			System.out.println("Iteration " + iteration);
			doIteration();
			processes.recordRunStatistics(iteration, feasiblePopulation, infeasiblePopulation);
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
		updateDiversificationCounter();
		if (processes.isDiversifyIteration()) {
			diversify(feasiblePopulation, infeasiblePopulation);
		}
		//printPopulation();
	}
	
	private boolean stoppingCriterion() {
		int maxIterations = problemData.getHeuristicParameterInt("Max iterations");
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
	}

	private void updateDiversificationCounter() {

		Individual bestIndividual = getBestSolution();
		double bestPenalizedCost = bestIndividual.getPenalizedCost();
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
		}
	}

	
	private Individual getBestSolution(){
		Individual bestFeasible = getBestSolution(feasiblePopulation);
		Individual bestInfeasible = getBestSolution(infeasiblePopulation);

		if (bestFeasible == null) {
			return bestInfeasible;
		}
		if (bestInfeasible == null){
			return bestFeasible;
		}
		
		if (bestInfeasible.getPenalizedCost() < bestFeasible.getPenalizedCost()){
			return bestInfeasible;
		}
		else {
			return bestFeasible;
		}
	}
}
