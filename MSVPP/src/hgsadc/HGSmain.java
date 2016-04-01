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
		ArrayList<Individual> initialPopulation = processes.createInitialPopulation();
		processes.convertGenotypeToPhenotype(initialPopulation);
		addToSubpopulation(initialPopulation);
		System.out.println("Initial population:");
		printPopulation();
	}
	
	private void doIteration() {
		ArrayList<Individual> parents = processes.selectParents(feasiblePopulation, infeasiblePopulation);
		Individual offspring = processes.generateOffspring(parents);
		processes.educateOffspring(offspring);
		addToSubpopulation(offspring, true);
		adjustPenaltyParameters();
		if (diversifyIteration()) {
			processes.diversify(feasiblePopulation, infeasiblePopulation);
		}
		printPopulation();
	}
	
	private void addToSubpopulation(Individual individual, boolean updateBiasedFitness) {
		if (individual.isFeasible()) {
			feasiblePopulation.add(individual);
			processes.addDiversityDistance(individual);
			checkSubpopulationSize(true);
		}
		else {
			infeasiblePopulation.add(individual);
			processes.addDiversityDistance(individual);
			checkSubpopulationSize(false);
		}
		if (updateBiasedFitness) {
			processes.updateBiasedFitness(feasiblePopulation, infeasiblePopulation);
		}
	}
	
	private void addToSubpopulation(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			addToSubpopulation(individual, false);
		}
		processes.updateBiasedFitness(feasiblePopulation, infeasiblePopulation);
	}
	
	private void adjustPenaltyParameters() {
		//TO-DO
	}
	
	private boolean diversifyIteration() {
		return false;
	}
	
	private void checkSubpopulationSize(boolean checkFeasiblePopulation) { //true-> check feasible subpop, false -> check infeasible subpop
		ArrayList<Individual> subpopulation;
		ArrayList<Individual> otherSubpopulation;
		int maxSubpopulationSize = problemData.getHeuristicParameterInt("Population size") 
				+ problemData.getHeuristicParameterInt("Number of offspring in a generation");
		if (checkFeasiblePopulation) {
			subpopulation = feasiblePopulation;
			otherSubpopulation = infeasiblePopulation;
		}
		else {
			subpopulation = infeasiblePopulation;
			otherSubpopulation = feasiblePopulation;
		}
		if (subpopulation.size() >= maxSubpopulationSize) {
			processes.survivorSelection(subpopulation, otherSubpopulation);
		}
	}
	
	private void printPopulation() {
		System.out.println("Feasible subpopulation:");
		for (Individual individual : feasiblePopulation) {
			System.out.print("Individual " + individual + " - ");
			System.out.println(individual.getFullText());
		}
		System.out.println("Infeasible subpopulation:");
		for (Individual individual : infeasiblePopulation) {
			System.out.print("Individual " + individual + " - ");
			System.out.println(individual.getFullText());
		}
		System.out.println("");
	}
}
