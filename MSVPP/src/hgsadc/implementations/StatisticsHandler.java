package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;
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
	String[] properties = {"# feasible solutions", "# infeasible solutions", "cap. penality", "dur. penality", "num. penality", "best penalized cost", "best feasible cost"};

	public StatisticsHandler(ProblemData problemData, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		this.problemData = problemData;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
		statistics = new HashMap<Integer, HashMap<String,Double>>();
	}

	
	public void recordRunStatistics(int iteration, ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		HashMap<String, Double> iterationStatistics = new HashMap<String, Double>();
		double bestPenalizedCost = getBestPenalizedCost(feasiblePopulation, infeasiblePopulation);
		double bestFeasibleCost = getBestPenalizedCost(feasiblePopulation);
		iterationStatistics.put("# feasible solutions", (double) feasiblePopulation.size());
		iterationStatistics.put("# infeasible solutions", (double) infeasiblePopulation.size());
		iterationStatistics.put("best penalized cost", bestPenalizedCost);
		iterationStatistics.put("best feasible cost", bestFeasibleCost);
		iterationStatistics.put("cap. penality", fitnessEvaluationProtocol.getCapacityViolationPenalty());
		iterationStatistics.put("dur. penality", fitnessEvaluationProtocol.getDurationViolationPenalty());
		iterationStatistics.put("num. penality", fitnessEvaluationProtocol.getNumberOfInstallationsPenalty());;
		statistics.put(iteration, iterationStatistics);
	}


	public void exportStatistics(String outputFileName) {
		PrintWriter writer = null;
		String fileName = outputFileName + getCurrentTime() + ".txt";
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when writing to output file");
		}
		int lastGeneration = Collections.max(statistics.keySet());
		writeParameters(writer);
		writeStatisticsHeader(writer);
		for (int i=1; i<lastGeneration+1; i++) {
			writeIterationStatistics(i, writer);
		}
		writer.close();
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
		//TODO write a method that prints the relevant parameters
		writer.println("some parameters");
	}
	
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private double getBestPenalizedCost(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		return getBestPenalizedCost(entirePopulation);
	}
	
	private double getBestPenalizedCost(ArrayList<Individual> population) {
		if (population.size() == 0) {
			return 0;
		}
		Collections.sort(population, Utilities.getPenalizedCostComparator());
		return population.get(0).getPenalizedCost();
	}
	

}
