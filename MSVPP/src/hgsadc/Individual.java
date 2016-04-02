package hgsadc;

import java.text.DecimalFormat;

import hgsadc.protocols.Genotype;
import hgsadc.protocols.Phenotype;


public class Individual {

	private Genotype genotype;
	private Phenotype phenotype;
	private double penalizedCost, diversityContribution, biasedFitness;
	

	private int number, costRank, diversityRank;
	private static int numberOfIndividuals = 0;
	
	public Individual(Genotype genotype) {
		this.genotype = genotype;
		numberOfIndividuals++;
		this.number = numberOfIndividuals;
	}
	
	public Genotype getGenotype() {
		return genotype;
	}
	
	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}
	
	public Phenotype getPhenotype() {
		return phenotype;
	}
	
	public double getPenalizedCost() {
		return penalizedCost;
	}

	public void setPenalizedCost(double penalizedCost) {
		this.penalizedCost = penalizedCost;
	}
	
	public String toString() {
		return ""+number;
	}
	
	public String getFullText() {
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		return "" + number + ", biased fitness: " + numberFormat.format(biasedFitness) + ", penalized cost: " + numberFormat.format(penalizedCost)
				+ ", cost rank: " + costRank + ", diversity contribution: "
				+ numberFormat.format(diversityContribution) + ", diversity rank: " + diversityRank
				 + ", capviol: " + numberFormat.format(phenotype.getCapacityViolation())
				 + ", durviol: " + numberFormat.format(phenotype.getDurationViolation())
				 + ", numViol: " + numberFormat.format(phenotype.getNumberOfInstallationsViolation())
				 + ", feasible: " + isFeasible();
	}

	public double getDiversityContribution() {
		return diversityContribution;
	}

	public void setDiversityContribution(double diversityContribution) {
		this.diversityContribution = diversityContribution;
	}

	public int getCostRank() {
		return costRank;
	}

	public void setCostRank(int costRank) {
		this.costRank = costRank;
	}

	public int getDiversityRank() {
		return diversityRank;
	}

	public void setDiversityRank(int diversityRank) {
		this.diversityRank = diversityRank;
	}
	
	public double getBiasedFitness() {
		return biasedFitness;
	}

	public void setBiasedFitness(double biasedFitness) {
		this.biasedFitness = biasedFitness;
	}
	
	public boolean isFeasible() {
		return phenotype.isFeasible();
	}
	
	/*
	public String toString() {
		String str = "";
		if (phenotype != null) {
			str += "Penalized cost: " + penalizedCost + "\n" + phenotype.toString() + "\n"; 
		}
		return  str;
	}
	*/
}
