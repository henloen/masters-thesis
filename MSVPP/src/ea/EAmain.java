package ea;

import java.util.ArrayList;

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
		System.out.println("Creating initial population...");
		main.createInitialPopulation();
		
		main.doEvolutionaryLoop(); // have to do one iteration before checking stop criterion
		while (! main.stoppingCriterion()) {
			main.doEvolutionaryLoop();
		}
		main.terminate();
	}
	
	public void initialize() {
		io = new IO();
		children = new Population();
		adults = new Population(); //children is initialized in "createInitialPopulation"
		parameters = io.readParameters();
		
		processes = new EAprocesses(parameters);
		generationNumber = 0;
	}

	public void createInitialPopulation() {
		children.setIndividuals(processes.createInitialPopulation());
	}
	
	public void doEvolutionaryLoop() {
		System.out.println("Generation: " + generationNumber);
		// 1. Generate phenotypes from genotypes
		processes.convertGenoToPheno(children);
		// 2. Evaluate the fitness of the children
		processes.evaluateFitness(children);
		// 2a. Retain the best individuals to next generation
		// <elitist process>
		// 2b. Local search?
		// <local search process>
		// 3. Select adults
		processes.selectAdults(children, adults);
		// 4. Select parents
		ArrayList<ArrayList<Individual>> parents = processes.selectParents(adults);
		// 5. Reproduction
		ArrayList<Individual> offspring = processes.reproduction(parents);
		children.setIndividuals(offspring);
		// 5a. Add elite to the next generation
		// <elitist process>

		// Record generation statistics
		io.recordGenerationStatistics(generationNumber, adults);
		// Increase generationNumber
		generationNumber++;
	}
	
	public boolean stoppingCriterion() {
		return processes.stoppingCriterion(adults, generationNumber);
	}
	
	public void terminate() {
		io.writeOutput(parameters);
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
	
	public void printParents(ArrayList<ArrayList<Individual>> parents) {
		for (ArrayList<Individual> couple : parents) {
			System.out.println(couple.get(0) + ", " + couple.get(1));
		}
	}

}