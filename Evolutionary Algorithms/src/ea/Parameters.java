package ea;

public class Parameters {
	private int nAdults, nChildren, nElites, nGenerations;
	private double mutationRate, crossoverRate;
	private String problemName,
	initialPopulationProtocolString, genoToPhenoProtocolString, fitnessEvaluationProtocolString, adultSelectionProtocolString,
	localSearchProtocolString, stopProtocolString, parentSelectionProtocolString, ReproductionProtocolString;
	
	
	
	public Parameters(int nAdults, int nChildren, int nElites, int nGenerations,
			double mutationRate, double crossoverRate, String problemName,
			String initialPopulationProtocolString,
			String genoToPhenoProtocolString,
			String fitnessEvaluationProtocolString,
			String adultSelectionProtocolString,
			String localSearchProtocolString, String stopProtocolString,
			String parentSelectionProtocolString,
			String reproductionProtocolString) {
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
		ReproductionProtocolString = reproductionProtocolString;
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
		return ReproductionProtocolString;
	}

	public void setReproductionProtocolString(String reproductionProtocolString) {
		ReproductionProtocolString = reproductionProtocolString;
	}

	public int getnGenerations() {
		return nGenerations;
	}

	public void setnGenerations(int nGenerations) {
		this.nGenerations = nGenerations;
	}
	
}