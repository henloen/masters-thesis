package ea;

import java.util.HashMap;

public class Parameters {
	private int nAdults, nChildren, nElites, nGenerations;
	private double mutationRate, crossoverRate;
	private String problemName,
	initialPopulationProtocolString, genoToPhenoProtocolString, fitnessEvaluationProtocolString, adultSelectionProtocolString,
	localSearchProtocolString, stopProtocolString, parentSelectionProtocolString, reproductionProtocolString, crossoverOperatorString, mutationOperatorString;
	private HashMap<String, String> optionalParameters;
	private HashMap<String, String> problemSpecificParameters; // This may be obsolete, instead input data is loaded into the object problemData
	private Object problemData;
	


	public Parameters(int nAdults, int nChildren, int nElites, int nGenerations,
			double mutationRate, double crossoverRate, String problemName,
			String initialPopulationProtocolString,
			String genoToPhenoProtocolString,
			String fitnessEvaluationProtocolString,
			String adultSelectionProtocolString,
			String localSearchProtocolString, String stopProtocolString,
			String parentSelectionProtocolString,
			String reproductionProtocolString,
			String crossoverOperatorString,
			String mutationOperatorString,
			HashMap<String, String> optionalParameters, HashMap<String, String> problemSpecificParameters) {
		
		this.nAdults = nAdults;
		this.nChildren = nChildren;
		this.nElites = nElites;
		this.nGenerations = nGenerations;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.problemName = problemName;
		this.initialPopulationProtocolString = initialPopulationProtocolString;
		this.genoToPhenoProtocolString = genoToPhenoProtocolString;
		this.fitnessEvaluationProtocolString = fitnessEvaluationProtocolString;
		this.adultSelectionProtocolString = adultSelectionProtocolString;
		this.localSearchProtocolString = localSearchProtocolString;
		this.stopProtocolString = stopProtocolString;
		this.parentSelectionProtocolString = parentSelectionProtocolString;
		this.reproductionProtocolString = reproductionProtocolString;
		this.crossoverOperatorString = crossoverOperatorString;
		this.mutationOperatorString= mutationOperatorString;
		this.optionalParameters = optionalParameters;
		this.problemSpecificParameters = problemSpecificParameters;
	}
	
	public Object getProblemData() {
		return problemData;
	}
	
	public HashMap<String, String> getOptionalParameters() {
		return optionalParameters;
	}

	public HashMap<String, String> getProblemSpecificParameters() {
		return problemSpecificParameters;
	}

	public void setProblemSpecificParameters(HashMap<String, String> problemSpecificParameters) {
		this.problemSpecificParameters = problemSpecificParameters;
	}

	public void setOptionalParameters(HashMap<String, String> optionalParameters) {
		this.optionalParameters = optionalParameters;
	}
	
	public String getCrossoverOperatorString() {
		return crossoverOperatorString;
	}

	public String getMutationOperatorString() {
		return mutationOperatorString;
	}

	public String toString() {
		String toString = "";
		toString += "Problem name: " + getProblemName() + "\n";
		toString += "Initial population protocol: " + getInitialPopulationProtocolString() + "\n";
		toString += "Genotype to phenotype protocol: " + getGenoToPhenoProtocolString() + "\n";
		toString += "Fitness evaluation protocol: " + getFitnessEvaluationProtocolString() + "\n";
		toString += "Adult selection protocol: " + getAdultSelectionProtocolString() + "\n";
		toString += "Local search protocol: " + getLocalSearchProtocolString() + "\n";
		toString += "Stop protocol: " + getStopProtocolString() + "\n";
		toString += "Parent selection protocol: " + getParentSelectionProtocolString() + "\n";
		toString += "Reproduction protocol: " + getReproductionProtocolString() + "\n";
		toString += "Number of children: " + getnChildren() + "\n";
		toString += "Number of adults: " + getnAdults() + "\n";
		toString += "Number of generations: "+ getnGenerations() + "\n";
		toString += "Number of elites: " + getnElites() + "\n";
		toString += "Crossover rate: " + getCrossoverRate() + "\n";
		toString += "Mutation rate: " + getMutationRate()  + "\n";
		return toString;
	}

	public int getnAdults() {
		return nAdults;
	}
	
	public void setnAdults(int nAdults) {
		this.nAdults = nAdults;
	}
	
	public int getnChildren() {
		return nChildren;
	}
	
	public void setnChildren(int nChildren) {
		this.nChildren = nChildren;
	}
	
	public int getnElites() {
		return nElites;
	}
	
	public void setnElites(int nElits) {
		this.nElites = nElits;
	}
	
	public double getMutationRate() {
		return mutationRate;
	}
	
	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	public double getCrossoverRate() {
		return crossoverRate;
	}
	
	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}
	
	public String getProblemName() {
		return problemName;
	}
	
	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public String getInitialPopulationProtocolString() {
		return initialPopulationProtocolString;
	}

	public void setInitialPopulationProtocolString(
			String initialPopulationProtocolString) {
		this.initialPopulationProtocolString = initialPopulationProtocolString;
	}

	public String getGenoToPhenoProtocolString() {
		return genoToPhenoProtocolString;
	}

	public void setGenoToPhenoProtocolString(String genoToPhenoProtocolString) {
		this.genoToPhenoProtocolString = genoToPhenoProtocolString;
	}

	public String getFitnessEvaluationProtocolString() {
		return fitnessEvaluationProtocolString;
	}

	public void setFitnessEvaluationProtocolString(
			String fitnessEvaluationProtocolString) {
		this.fitnessEvaluationProtocolString = fitnessEvaluationProtocolString;
	}

	public String getAdultSelectionProtocolString() {
		return adultSelectionProtocolString;
	}

	public void setAdultSelectionProtocolString(String adultSelectionProtocolString) {
		this.adultSelectionProtocolString = adultSelectionProtocolString;
	}

	public String getLocalSearchProtocolString() {
		return localSearchProtocolString;
	}

	public void setLocalSearchProtocolString(String localSearchProtocolString) {
		this.localSearchProtocolString = localSearchProtocolString;
	}

	public String getStopProtocolString() {
		return stopProtocolString;
	}

	public void setStopProtocolString(String stopProtocolString) {
		this.stopProtocolString = stopProtocolString;
	}

	public String getParentSelectionProtocolString() {
		return parentSelectionProtocolString;
	}

	public void setParentSelectionProtocolString(
			String parentSelectionProtocolString) {
		this.parentSelectionProtocolString = parentSelectionProtocolString;
	}

	public String getReproductionProtocolString() {
		return reproductionProtocolString;
	}

	public void setReproductionProtocolString(String reproductionProtocolString) {
		this.reproductionProtocolString = reproductionProtocolString;
	}

	public int getnGenerations() {
		return nGenerations;
	}

	public void setnGenerations(int nGenerations) {
		this.nGenerations = nGenerations;
	}
	
}
