package ea;

public class Individual implements Comparable<Individual>{
	
	private Genotype genotype;
	private Phenotype phenotype;
	private double fitness;
	
	public Individual(Genotype genotype) {
		this.genotype = genotype;
	}
	
	public Individual(Genotype genotype, Phenotype phenotype, double fitness) {
		this.genotype = genotype;
		this.phenotype = phenotype;
		this.fitness = fitness;
	}
	
	
	public Individual cloneGenotype(){
		Genotype clonedGenotype = genotype.cloneGenotype();
		return new Individual(clonedGenotype);
	}
	
	@Override
	public Individual clone(){
		Genotype clonedGenotype = genotype.cloneGenotype();
		Phenotype clonedPhenotype = phenotype.clonePhenotype();
		double clonedFitness = fitness;
		return new Individual(clonedGenotype, clonedPhenotype, clonedFitness);
	}
	
	public Genotype getGenotype() {
		return genotype;
	}
	
	public void setGenotype(Genotype genotype) {
		this.genotype = genotype;
	}
	
	public Phenotype getPhenotype() {
		return phenotype;
	}
	
	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public String toString() {
		return "Genotype: " + getGenotype() + ", Phenotype: " + getPhenotype() + ", Fitness: " + getFitness();
	}

	@Override
	public int compareTo(Individual o) {
		if (this.fitness < o.getFitness()) {
			return -1;
		}
		else if (this.fitness > o.getFitness()) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
}
