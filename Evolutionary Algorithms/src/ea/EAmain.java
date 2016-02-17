package ea;

import java.util.ArrayList;
import java.util.HashMap;

public class EAmain {
	
	private Population children;
	private Population adults;
	EAprocesses processes;
	IO io;
	private int generationNumber;
	private Parameters parameters;
	
	public static void main(String[] args) {
		EAmain main = new EAmain();
		main.initialize();
		main.createInitialPopulation();
		main.doEvolutionaryLoop();
		while (! main.stoppingCriterion()) {
			main.doEvolutionaryLoop();
		}
		main.terminate();
	}
	
	public void initialize() {
		io = new IO();
		children = new Population();
		adults = new Population(); //children is initialized in "createInitialPopulation"
		parameters = io.readInput();
		processes = new EAprocesses(parameters);
		generationNumber = 0;
	}

	public void createInitialPopulation() {
		children.setIndividuals(processes.createInitialPopulation());
	}
	
	public void doEvolutionaryLoop() {
		System.out.println("Generation: " + generationNumber);
		printChildren();
		// 1. Generate phenotypes from genotypes
		children = processes.convertGenoToPheno(children);
		printChildren();
		// 2. Evaluate the fitness of the children
		children = processes.evaluateFitness(children);
		printChildren();
		// 2a. Retain the best individuals to next generation
		// <elitist process>
		// 2b. Local search?
		// <local search process>
		// 3. Select adults
		adults.setIndividuals(processes.selectAdults(children.getIndividuals(), adults.getIndividuals()));
		printChildren();
		printAdults();
		// 4. Select parents
		ArrayList<ArrayList<Individual>> parents = processes.selectParents(adults.getIndividuals());
		// 5. Reproduction
		children.setIndividuals(processes.reproduction(adults.getIndividuals()));
		// 5a. Add elite to the next generation
		// <elitist process>

		// Increase generationNumber
		generationNumber++;
		// Record generation statistics
		io.recordGenerationStatistics(generationNumber, adults);
	}
	
	public boolean stoppingCriterion() {
		return processes.stoppingCriterion();
	}
	
	public void terminate() {
		io.writeOutput();
	}
	
	//helper method during development
	public void printChildren() {
		System.out.println("Children:");
		printPopulation(children);		
	}
	
	//helper method during development
	public void printAdults() {
		System.out.println("Adults:");
		printPopulation(adults);
	}
	
	//helper method during development
	public void printPopulation(Population population) {
		for (Individual individual : population.getIndividuals()) {
			System.out.println(individual);
		}
	}

}