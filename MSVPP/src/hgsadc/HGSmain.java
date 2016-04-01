package hgsadc;

import java.util.ArrayList;

public class HGSmain {
	
	private String inputFileName = "data/hgs/input/input data hgs.xls";
	private IO io;
	private ProblemData problemData;
	private HGSprocesses processes;
	private ArrayList<Individual> feasiblePopulation, infeasiblePopulation;
	
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize();
		main.createInitialPopulation();
		int iteration = 1;
		while (iteration <= main.problemData.getHeuristicParameterInt("Iterations")) {
			System.out.println("Iteration " + iteration);
			main.doIteration();
			iteration++;
		}
	}

	private void initialize() {
		io = new IO(inputFileName);
		problemData = io.readData();
		problemData.generatePatterns();
		processes = new HGSprocesses(problemData);
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		
		problemData.printProblemData();
	}

	private void createInitialPopulation(){
		System.out.println("Creating initial population...");
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
		System.out.println("Initial population:");
		printPopulation();
	}
	
	private void doIteration() {
		ArrayList<Individual> parents = processes.selectParents(feasiblePopulation, infeasiblePopulation);
		Individual offspring = processes.generateOffspring(parents);
		processes.educate(offspring);
		addToSubpopulation(offspring);
		adjustPenaltyParameters();
		if (diversifyIteration()) {
			processes.diversify(feasiblePopulation, infeasiblePopulation);
		}
		printPopulation();
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
	
	private void adjustPenaltyParameters() {
		//TO-DO
	}
	
	private boolean diversifyIteration() {
		return false;
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
