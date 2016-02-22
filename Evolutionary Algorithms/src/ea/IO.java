package ea;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;

public class IO {

	String parametersFileName = "data/input/parameters.xls";
	String outputFileName = "data/output/";
	HashMap<Integer, HashMap<String, Double>> generationStatistics;
	
	public IO() {
		generationStatistics = new HashMap<Integer, HashMap<String, Double>>(); //hashmap is chosen to ensure that the statistics are record for the correct generation 
	}
	
	public Parameters readParameters() {
		HashMap<String, String> parameterHashMap = readParameters(2,3);
		HashMap<String, String> optionalParameterHashMap = readParameters(2, 3 + parameterHashMap.keySet().size() + 1);
		Parameters parameters = convertToParametersObject(parameterHashMap, optionalParameterHashMap);
		return parameters;
	}
	
	public void writeOutput(Parameters parameters){
		writeStatistics(parameters);
	}
	
	private void writeStatistics(Parameters parameters) {
		PrintWriter writer = null;
		String fileName = outputFileName + getCurrentTime() + ".txt";
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when writing to output file");
		}
		int lastGeneration = Collections.max(generationStatistics.keySet());
		writeParameters(writer, parameters);
		writeGenerationStatisticsHeader(writer);
		for (int i=0; i<lastGeneration+1; i++) {
			writeGenerationStatistics(i, writer);
		}
		writer.close();
	}

	public void recordGenerationStatistics(int generationNumber, Population population) {
		HashMap<String, Double> statistics = new HashMap<String, Double>();
		statistics.put("Avg. fitness", population.getAverageFitness());
		statistics.put("Std. fitness", population.getStandardDeviationFitness());
		statistics.put("Best fitness", population.getBestIndividual().getFitness());
		generationStatistics.put(generationNumber, statistics);
	}
	
	private void writeGenerationStatistics(int generation, PrintWriter writer) {
		writer.println(getGenerationStatistics(generation));
	}
	
	private void writeGenerationStatisticsHeader(PrintWriter writer) {
		String header = "Gen.";
		for (String stat : generationStatistics.get(0).keySet()) {
			header += "\t" + stat;
		}
		writer.println(header);
	}
	
	private void writeParameters(PrintWriter writer, Parameters parameters) {
		String str = "Crossover rate: " + parameters.getCrossoverRate();
		str += "\t" + "Mutation rate: " + parameters.getMutationRate();
		writer.println(str);
	}
	
	private String getGenerationStatistics(int generation) {
		String str = "" + generation;
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		HashMap<String, Double> generationStats = generationStatistics.get(generation);
		for (String stat : generationStats.keySet()) {
			str += "\t" + df.format(generationStats.get(stat));
		}
		return str;
	}
	
	
	private HashMap<String, String> readParameters(int column, int startRow) {
		int nameColumn = column; //0-indexed
		int valueColumn = column+1; //0-indexed
		HashMap<String, String> parameterHashmap = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(parametersFileName));
			Sheet sheet = workbook.getSheet(0); //the data is expected in the first sheet
			int i = 0;
			while (sheet.getCell(valueColumn,startRow+i).getContents() != "") {
				String name = sheet.getCell(nameColumn,startRow+i).getContents();
				String value = sheet.getCell(valueColumn, startRow+i).getContents();
				parameterHashmap.put(name, value);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when loading the parameters from Excel");
		}
		return parameterHashmap;
	}
	
	private Parameters convertToParametersObject(HashMap<String, String> parameterHashMap, HashMap<String, String> optionalParameterHashMap) {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		df.setDecimalFormatSymbols(symbols);
		
		String problemName = parameterHashMap.get("Problem name");
		String initialPopulation = parameterHashMap.get("Initial population generation");
		String genoToPhenoConverter = parameterHashMap.get("Genotype to phenotype converter");
		String fitnessFunction = parameterHashMap.get("Fitness function");
		String adultSelection = parameterHashMap.get("Adult selection");
		String localSearch = parameterHashMap.get("Local search");
		String stoppingCriterion = parameterHashMap.get("Stopping criterion");
		String parentSelection = parameterHashMap.get("Parent selection");
		String reproduction = parameterHashMap.get("Reproduction");
		String geneticOperator = parameterHashMap.get("Genetic Operator");
		int nChildren = Integer.parseInt(parameterHashMap.get("Number of children"));
		int nAdults = Integer.parseInt(parameterHashMap.get("Number of adults"));
		int nGenerations = Integer.parseInt(parameterHashMap.get("Number of generations"));
		int nElites = Integer.parseInt(parameterHashMap.get("Number of elites"));
		double crossoverRate, mutationRate;
		try {
			crossoverRate = df.parse(parameterHashMap.get("Crossover rate")).doubleValue();
			mutationRate = df.parse(parameterHashMap.get("Mutation rate")).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Something went wrong when parsing doubles from the parameter input file, crossover and mutation rate are set to 0.0");
			crossoverRate = 0.0;
			mutationRate = 0.0;
		}
		return new Parameters(nAdults, nChildren, nElites, nGenerations, mutationRate,
				crossoverRate, problemName, initialPopulation, genoToPhenoConverter,
				fitnessFunction, adultSelection, localSearch, stoppingCriterion, parentSelection, reproduction, geneticOperator, optionalParameterHashMap);
	}
	
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
}
