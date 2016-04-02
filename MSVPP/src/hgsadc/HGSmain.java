package hgsadc;

import java.util.ArrayList;

public class HGSmain {
	
	private String inputFileName = "data/hgs/input/input data hgs.xls";
	private IO io;
	private ProblemData problemData;
	private HGSprocesses processes;
	private ArrayList<Individual> feasiblePopulation, infeasiblePopulation;
	
	private int iteration;
	
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize();
		System.out.println("Creating initial population...");
		main.createInitialPopulation();
		main.runEvolutionaryLoop();
		System.out.println("Initial population:");
		main.printPopulation();
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
		int maxIterations = problemData.getHeuristicParameterInt("Max iterations");
		while (iteration <= maxIterations) {
			System.out.println("Iteration " + iteration);
			doIteration();
			iteration++;
		}
	}
	
	private void doIteration() {
		ArrayList<Individual> parents = processes.selectParents(feasiblePopulation, infeasiblePopulation);
		Individual offspring = processes.generateOffspring(parents);
		processes.educate(offspring);
		addToSubpopulation(offspring);
		processes.adjustPenaltyParameters();
		if (processes.isDiversifyIteration()) {
			diversify(feasiblePopulation, infeasiblePopulation);
		}
		//printPopulation();
		iteration++;
	}

	private void diversify(ArrayList<Individual> feasiblePopulation2, ArrayList<Individual> infeasiblePopulation2) {
		// TODO Auto-generated method stub
		
	}

	private void addToSubpopulation(Individual individual) {
		if (individual.isFeasible()) {
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
	
	private void checkSubpopulationSize(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation) {
		int maxSubpopulationSize = problemData.getHeuristicParameterInt("Population size") 
				+ problemData.getHeuristicParameterInt("Number of offspring in a generation");
		if (subpopulation.size() >= maxSubpopulationSize) {
			processes.survivorSelection(subpopulation, otherSubpopulation);
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
}
