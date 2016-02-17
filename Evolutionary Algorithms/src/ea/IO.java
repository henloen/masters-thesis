package ea;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;

public class IO {

	String parametersFileName = "data/input/parameters.xls";
	HashMap<Integer, Double[]> generationStatistics;
	
	public IO() {
		generationStatistics = new HashMap<Integer, Double[]>(); //hashmap is chosen to ensure that the statistics are record for the correct generation 
	}
	
	public Parameters readInput() {
		HashMap<String, String> parameterHashMap = readParameters();
		Parameters parameters = convertToParametersObject(parameterHashMap);
		return parameters;
	}
	
	public void writeOutput() {
		writeStatistics();
	}
	
	private void writeStatistics() {
		
	}

	public void recordGenerationStatistics(int generationNumber, Population population) {
		Double[] statistics = new Double[]{population.getSumFitness(), population.getAverageFitness(), population.getStandardDeviationFitness()};
		generationStatistics.put(generationNumber, statistics);
	}
	
	
	private HashMap<String, String> readParameters() {
		int nameColumn = 2; //0-indexed
		int valueColumn = 3; //0-indexed
		int firstRow = 3; //0-indexed
		HashMap<String, String> parameterHashmap = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(parametersFileName));
			Sheet sheet = workbook.getSheet(0); //the data is expected in the first sheet
			int i = 0;
			while (sheet.getCell(valueColumn,firstRow+i).getContents() != "") {
				String name = sheet.getCell(nameColumn,firstRow+i).getContents();
				String value = sheet.getCell(valueColumn, firstRow+i).getContents();
				parameterHashmap.put(name, value);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong when loading the parameters from Excel");
		}
		return parameterHashmap;
	}
 	
	private Parameters convertToParametersObject(HashMap<String, String> parameterHashMap) {
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
				fitnessFunction, adultSelection, localSearch, stoppingCriterion, parentSelection, reproduction, geneticOperator);
	}
	
}
