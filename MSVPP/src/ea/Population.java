package ea;

import java.util.ArrayList;
import java.util.Collections;

public class Population {
	
	public Population(ArrayList<Individual> individuals) {
		this.individuals = individuals;
	}
	
	public Population(){
		
	}

	private ArrayList<Individual> individuals;

	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	public void setIndividuals(ArrayList<Individual> individuals) {
		this.individuals = individuals;
	}
	
	public double getSumFitness() {
		double sumFitness = 0;
		for (Individual individual : individuals) {
			sumFitness += individual.getFitness();
		}
		return sumFitness;
	}
	
	public double getAverageFitness() {
		return getSumFitness() / individuals.size();
	}
	
	public double getStandardDeviationFitness() {
		double avgFitness = getAverageFitness();
		double std = 0;
		for (Individual individual : individuals) {
			std += Math.pow(individual.getFitness()-avgFitness, 2);
		}
		std /= individuals.size();
		return Math.sqrt(std);
	}
	
	public Individual getBestIndividual() {
		Collections.sort(individuals, Collections.reverseOrder());
		return individuals.get(0);
	}
	

}
