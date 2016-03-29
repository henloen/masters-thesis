package hgsadc;

import java.util.ArrayList;

public class HGSmain {
	
	private String inputFileName = "data/hgs/input/input data hgs.xls";
	private IO io;
	private ProblemData problemData;
	private HGSprocesses processes;
	private int iteration;
	private ArrayList<Individual> feasiblePopulation, infeasiblePopulation;
	
	
	public static void main(String[] args) {
		HGSmain main = new HGSmain();
		main.initialize();
		System.out.println("Creating initial population...");
		main.createInitialPopulation();
		
		main.doIteration();
	}

	private void initialize() {
		io = new IO(inputFileName);
		problemData = io.readData();
		problemData.generatePatterns();
		processes = new HGSprocesses(problemData);
		feasiblePopulation = new ArrayList<Individual>();
		infeasiblePopulation = new ArrayList<Individual>();
		iteration = 0;
		
		problemData.printProblemData();
	}

	private void createInitialPopulation(){
		ArrayList<Individual> initialPopulation = processes.createInitialPopulation();
		processes.convertGenotypeToPhenotype(initialPopulation);
		for (Individual individual : initialPopulation) {
			addToSubpopulation(individual);
		}
	}
	
	private void doIteration() {
		System.out.println("Iteration: " + iteration);
		ArrayList<Individual> parents = processes.selectParents(feasiblePopulation, infeasiblePopulation);
		Individual offspring = processes.generateOffspring(parents);
		processes.educateOffspring(offspring);
		addToSubpopulation(offspring);
		adjustPenaltyParameters();
		if (diversifyIteration()) {
			processes.diversify(feasiblePopulation, infeasiblePopulation);
		}
		iteration++;
	}
	
	private void addToSubpopulation(Individual individual) {
		if (problemData.isFeasible(individual)) {
			feasiblePopulation.add(individual);
			checkSubpopulationSize(feasiblePopulation);
		}
		else {
			infeasiblePopulation.add(individual);
			checkSubpopulationSize(infeasiblePopulation);
		}
	}
	
	private void adjustPenaltyParameters() {
		//TO-DO
	}
	
	private boolean diversifyIteration() {
		return false;
	}
	
	private void checkSubpopulationSize(ArrayList<Individual> subpopulation) {
		if (subpopulation.size() >= problemData.getHeuristicParameterInt("Maximum subpopulation size")) {
			processes.survivorSelection(subpopulation);
		}
	}
}
