package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class StatisticsHandler {
	
	private ProblemData problemData;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private HashMap<Integer, HashMap<String, Double>> statistics;
	private int skipNumberOfRows, lastGeneration;
	//the order of the properties array is important, as it corresponds with the sequence of plotting in the plotting file
	String[] properties = {"# feasible solutions", "# infeasible solutions",
			"cap. penality", "dur. penality", "num. penality",
			"best penalized cost", "best feasible cost",
			"best cap. violation", "best dur. violation", "best num. violation",
			"avg cap. violation", "avg dur. violation", "avg num. violation",
			"gap from BKS",
			"best visits pr voyage", "avg visits pr voyage"};

	public StatisticsHandler(ProblemData problemData, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		this.problemData = problemData;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
		statistics = new HashMap<Integer, HashMap<String,Double>>();
		skipNumberOfRows = 18 + problemData.getProblemInstanceParameters().size() + problemData.getHeuristicParameters().size()
				+ problemData.getLengthOfPlanningPeriod() + problemData.getVessels().size(); 
		//all rows before the data starts are skipped, see an output file for the layout
	}

	
	public void recordRunStatistics(int iteration, ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		HashMap<String, Double> iterationStatistics = new HashMap<String, Double>();
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		Individual bestPenalizedCostIndividual = getBestPenalizedCostIndividual(feasiblePopulation, infeasiblePopulation);
		Individual bestFeasibleCostIndividual = getBestPenalizedCostIndividual(feasiblePopulation);
		iterationStatistics.put("# feasible solutions", (double) feasiblePopulation.size());
		iterationStatistics.put("# infeasible solutions", (double) infeasiblePopulation.size());
		iterationStatistics.put("best penalized cost", bestPenalizedCostIndividual.getPenalizedCost());
		if (bestFeasibleCostIndividual == null) {//no feasible individual
			iterationStatistics.put("best feasible cost", 0.0);
			iterationStatistics.put("gap from BKS", 0.0);
		}
		else {
			iterationStatistics.put("best feasible cost", bestFeasibleCostIndividual.getPenalizedCost());
			double bestKnownSailingCost = Double.parseDouble(problemData.getProblemInstanceParameters().get("Best known sailing cost"));
			iterationStatistics.put("gap from BKS", (bestFeasibleCostIndividual.getPenalizedCost() / bestKnownSailingCost)-1);
		}
		iterationStatistics.put("cap. penality", fitnessEvaluationProtocol.getCapacityViolationPenalty());
		iterationStatistics.put("dur. penality", fitnessEvaluationProtocol.getDurationViolationPenalty());
		iterationStatistics.put("num. penality", fitnessEvaluationProtocol.getNumberOfInstallationsPenalty());;
		iterationStatistics.put("best cap. violation", bestPenalizedCostIndividual.getPhenotype().getCapacityViolation());
		iterationStatistics.put("best dur. violation", bestPenalizedCostIndividual.getPhenotype().getDurationViolation());
		iterationStatistics.put("best num. violation", bestPenalizedCostIndividual.getPhenotype().getNumberOfInstallationsViolation());
		iterationStatistics.put("avg cap. violation", getAverageCapacityViolation(entirePopulation));
		iterationStatistics.put("avg dur. violation", getAverageDurationViolation(entirePopulation));
		iterationStatistics.put("avg num. violation", getAverageNumberOfInstallationsViolation(entirePopulation));
		iterationStatistics.put("best visits pr voyage", getAverageVisitsPerVoyage(bestPenalizedCostIndividual));
		iterationStatistics.put("avg visits pr voyage", getAverageVisitsPerVoyage(entirePopulation));
		statistics.put(iteration, iterationStatistics);
	}

	public void exportStatistics(String outputFileName, long runningTime, Individual bestFeasibleIndividual,
			ArrayList<Integer> diversificationNumbers, int numberOfCrossoverRestarts, int numberOfConstructionHeuristicRestarts) {
		PrintWriter writer = null;
		String fileName = outputFileName + getCurrentTime() + " " + problemData.getProblemInstanceParameters().get("Problem size") + ".txt";
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when writing to output file");
		}
		lastGeneration = Collections.max(statistics.keySet());
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		if (bestFeasibleIndividual == null) {
			skipNumberOfRows -=(11+problemData.getVessels().size());
		}
		writer.println("Skip number of rows: " + skipNumberOfRows);
		writer.println("Time used: " + numberFormat.format((double) runningTime/1000000000) + " seconds");
		writeParameters(writer);
		writeRunStatistics(writer, diversificationNumbers, numberOfCrossoverRestarts, numberOfConstructionHeuristicRestarts);
		writeSolution(writer, bestFeasibleIndividual);
		writeStatisticsHeader(writer);
		for (int i=0; i<lastGeneration+1; i++) {
			writeIterationStatistics(i, writer);
		}
		writer.close();
	}
	
	private void writeRunStatistics(PrintWriter writer,
			ArrayList<Integer> diversificationNumbers,
			int numberOfCrossoverRestarts,
			int numberOfConstructionHeuristicRestarts) {
		writer.println("Number of iterations: " + lastGeneration);
		writer.println("Diversifaction happened at iterations: " + diversificationNumbers);
		writer.println("Number of crossover restarts: " + numberOfCrossoverRestarts);
		writer.println("Number of construction heuristic restarts: " + numberOfConstructionHeuristicRestarts);
		writer.println();
	}


	private void writeStatisticsHeader(PrintWriter writer) {
		String header = "Gen.";
		for (String property : properties) { //get statistics header
			header += "\t" + property;
		}
		writer.println(header);
	}
	
	private void writeIterationStatistics(int iteration, PrintWriter writer) {
		String str = "" + iteration;
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		HashMap<String, Double> iterationStatistics = statistics.get(iteration);
		for (String property : properties) {
			str += "\t" + df.format(iterationStatistics.get(property));
		}
		writer.println(str);
	}
	
	private void writeParameters(PrintWriter writer) {
		writer.println(); //one row of space
		writer.println("Problem instance parameters: ");
		for (String key : problemData.getProblemInstanceParameters().keySet()) {
			writer.println(key + " = " + problemData.getProblemInstanceParameters().get(key));
		}
		writer.println(); //one row of space
		writer.println("Heuristic parameters: ");
		for (String key : problemData.getHeuristicParameters().keySet()) {
			writer.println(key + " = " + problemData.getHeuristicParameters().get(key));
		}
		writer.println(); //one row of space
	}
	
	public void writeSolution(PrintWriter writer, Individual individual) {
		DecimalFormat df = new DecimalFormat("0.00");
		if (individual == null) {
			writer.println("No feasible solution!");
		}
		else {
			writer.println("Penalized cost: " + df.format(individual.getPenalizedCost()));
			double bestKnownSailingCost = Double.parseDouble(problemData.getProblemInstanceParameters().get("Best known sailing cost"));
			writer.println("Gap from BKS: " + df.format(((individual.getPenalizedCost() / bestKnownSailingCost)-1)));
			writer.println(individual.getPhenotype().getScheduleString());
			writer.println("Chartered fleet:");
			for (Vessel vessel : problemData.getVessels()) {
				writer.println(vessel.fullText());
			}
			writer.println();
		}
	}
	
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private Individual getBestPenalizedCostIndividual(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		return getBestPenalizedCostIndividual(entirePopulation);
	}
	
	private Individual getBestPenalizedCostIndividual(ArrayList<Individual> population) {
		if (population.size() == 0) {
			return null;
		}
		Collections.sort(population, Utilities.getPenalizedCostComparator());
		return population.get(0);
	}
	
	public double getAverageDurationViolation(ArrayList<Individual> population) {
		double sumDurationViolation = 0.0;
		for (Individual individual : population) {
			sumDurationViolation += individual.getPhenotype().getDurationViolation();
		}
		return sumDurationViolation / population.size();
	}
	
	private double getAverageCapacityViolation(ArrayList<Individual> population) {
		double sumCapacityViolation = 0.0;
		for (Individual individual : population) {
			sumCapacityViolation += individual.getPhenotype().getCapacityViolation();
		}
		return sumCapacityViolation / population.size();
		}	
	
	private double getAverageNumberOfInstallationsViolation(ArrayList<Individual> population) {
		double sumNumberOfInstallationsViolation = 0.0;
		for (Individual individual : population) {
			sumNumberOfInstallationsViolation += individual.getPhenotype().getNumberOfInstallationsViolation();
		}
		return sumNumberOfInstallationsViolation / population.size();
	}
	

	private double getAverageVisitsPerVoyage(Individual bestPenalizedCostIndividual) {
		HashMap<Integer, HashMap<Vessel, Voyage>> giantTour = bestPenalizedCostIndividual.getPhenotype().getGiantTour();
		double numberOfVoyages = 0;
		double numberOfVisits = 0;
		for (Integer day : giantTour.keySet()){
			for (Vessel vessel : giantTour.get(day).keySet()){
				Voyage voy = giantTour.get(day).get(vessel);
				if (voy != null) {
					numberOfVoyages++;
					numberOfVisits += voy.getInstallations().size();
				}
			}
		}
		return numberOfVisits / numberOfVoyages;
	}
	
	private double getAverageVisitsPerVoyage(ArrayList<Individual> population) {
		double sumVisitsPerVoyage = 0;
		for (Individual individual : population) {
			sumVisitsPerVoyage += getAverageVisitsPerVoyage(individual); 
		}
		return sumVisitsPerVoyage / population.size();
	}

}
