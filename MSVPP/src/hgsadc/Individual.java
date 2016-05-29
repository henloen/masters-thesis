package hgsadc;

import hgsadc.protocols.Genotype;
import hgsadc.protocols.Phenotype;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;


public class Individual {

	private Genotype genotype;
	private Phenotype phenotype;
	private double penalizedCost, diversityContribution, biasedFitness;
	private int numberOfChangesFromBaseline, numberOfRobustVoyages;
	
	public String typeOfEducation = "";

	private int number, costRank, diversityRank, persistenceRank, robustnessRank;

	private static int numberOfIndividuals = 0;
	
	public Individual(Genotype genotype) {
		this.genotype = genotype;
		numberOfIndividuals++;
		this.number = numberOfIndividuals;
	}
	
	public int getNumberOfRobustVoyages(){
		return numberOfRobustVoyages;
	}
	
	public double getRobustness() {
		return (double) numberOfRobustVoyages/getNumberOfVoyagesSailed();
	}
	
	private int getNumberOfVoyagesSailed() {
		return phenotype.getNumberOfVoyagesSailed();
	}

	public void setNumberOfRobustVoyages(int numberOfRobustVoyages) {
		this.numberOfRobustVoyages = numberOfRobustVoyages;
	}
	public int getRobustnessRank() {
		return robustnessRank;
	}
	
	public int getNumberOfChangesFromBaseline() {
		return numberOfChangesFromBaseline;
	}
	
	public void setNumberOfChangesFromBaseline(int numberOfChangesFromBaseline) {
		this.numberOfChangesFromBaseline = numberOfChangesFromBaseline;
	}

	public Set<Integer> getDaysWithVesselDeparture() {
		return genotype.getDaysWithVesselDeparture();
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
		DecimalFormat noDecimalFormat = new DecimalFormat("0");
		return "" + number + ", biased fitness: " + numberFormat.format(biasedFitness) + ", penalized cost: " + noDecimalFormat.format(penalizedCost)
				+ ", persistence " + numberOfChangesFromBaseline + ", cost rank: " + costRank + ", persistence rank: " + persistenceRank + ", diversity contribution: "
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
	
	public int getPersistenceRank() {
		return persistenceRank;
	}
	
	public void setPersistenceRank(int persistenceRank){
		this.persistenceRank = persistenceRank;
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


	public HashMap<Integer, Set<Integer>> getVesselDeparturesPerDay() {
		return genotype.getVesselDeparturesPerDay();
	}


	public void setGenotype(Genotype genotype) {
		this.genotype = genotype;
	}
	
	public void setGenotypeAndUpdatePenalizedCost(Genotype newGenotype, GenoToPhenoConverterProtocol genoToPhenoConverter, FitnessEvaluationProtocol fitnessEvaluationProtocol){
		setGenotype(newGenotype);
//		System.out.println("Old penCost: " + individual.getPenalizedCost() + " New penCost: " + newIndividual.getPenalizedCost());
		genoToPhenoConverter.convertGenotypeToPhenotype(this);
		fitnessEvaluationProtocol.setPenalizedCostIndividual(this);
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
	public String getObjectiveValues(){
		return "Individual " + number + ", Cost: " + penalizedCost + ", Persistence: " + numberOfChangesFromBaseline + ", Robustness: " + numberOfRobustVoyages;
	}
	
	public HashMap<Integer, Integer> getNumberOfVoyagesPerDay() {
		HashMap<Integer, Integer> numberOfDeparturesPerDay = new HashMap<>();
		HashMap<Integer, Set<Integer>> vesselDeparturesPerDay = genotype.getVesselDeparturesPerDay(); 
		
		for (Integer day : vesselDeparturesPerDay.keySet()){
			int nDeparturesOnDay = vesselDeparturesPerDay.get(day).size();
			numberOfDeparturesPerDay.put(day, nDeparturesOnDay);
		}
		return numberOfDeparturesPerDay;
	}

	public void setRobustnessRank(int robustnessRank) {
		this.robustnessRank = robustnessRank;
	}

	public String getRobustnessString() {
		String str = "=========================================================\n"
				+ "Voyages sailed:\n";
		
		for (int day : phenotype.getGiantTour().keySet()){
			for (Voyage voyage : phenotype.getGiantTour().get(day).values()){
				if (voyage == null) continue;
				str += "\n" + "Duration: " + voyage.getDurationDays() + " days, " + voyage.getDuration() + " hrs. Slack: " + voyage.getSlack() + " hrs";
			}
		}
		return str;
	}

}
