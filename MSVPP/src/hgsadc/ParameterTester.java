package hgsadc;

import java.util.ArrayList;
import java.util.HashMap;

public class ParameterTester {
	
	private HashMap<String, ArrayList<String>> parameters;
	
	public static void main(String[] args) {
		ParameterTester tester = new ParameterTester();
		tester.initializeParameters();
		tester.runTests();
	}
	
	private void initializeParameters() {
		parameters = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> values1 = new ArrayList<String>();
		values1.add("0,2");
		values1.add("0,4");
		values1.add("0,6");
		values1.add("0,8");
		parameters.put("Reference proportion of feasible individuals", values1);
		
		ArrayList<String> values2 = new ArrayList<String>();
		values2.add("15");
		values2.add("25");
		values2.add("35");
		parameters.put("Population size", values2);
		
		ArrayList<String> values3 = new ArrayList<String>();
		values3.add("25");
		values3.add("50");
		values3.add("75");
		values3.add("100");
		parameters.put("Number of offspring in a generation", values3);
	
		ArrayList<String> values4 = new ArrayList<String>();
		values4.add("0.1");
		values4.add("0.4");
		values4.add("0.7");
		parameters.put("Iterations before diversify", values4);
		
		ArrayList<String> values5 = new ArrayList<String>();
		values5.add("5000");
		values5.add("10000");
		parameters.put("Max iterations without improvement", values5);
		
		ArrayList<String> values6 = new ArrayList<String>();
		/*
		for (int i = 27; i < 28; i++) {
			values6.add(""+i);
		}
		*/
		values6.add("12");
		values6.add("20");
		values6.add("27");
		parameters.put("Problem size", values6);
		
		/*
		ArrayList<String> values7 = new ArrayList<String>();
		values7.add("0,2");
		values7.add("0,4");
		values7.add("0,6");
		values7.add("0,8");
		parameters.put("Proportion of elite individuals", values7);
		}*/
		
		ArrayList<String> values8 = new ArrayList<String>();
		values8.add("0");
		parameters.put("Education rate", values8);
	}
	
	private void runTests() {
		/*
		for (String value : parameters.get("Reference proportion of feasible individuals")) {
			String[] args = {"Reference proportion of feasible individuals=" + value};
			runTest(args);
		}
		
		for (String value : parameters.get("Problem size")) {
			for (String value1 : parameters.get("Population size")) {
				for (String value2: parameters.get("Number of offspring in a generation")) {
					String[] args = {"Population size=" + value1, "Number of offspring in a generation=" + value2, "Problem size=" + value};
					runTest(args);
				}
			}
		}
		
		for (String value1 : parameters.get("Iterations before diversify")) {
			for (String value2: parameters.get("Max iterations without improvement")) {
				String[] args = {"Iterations before diversify=" + (int) ((Double.valueOf(value1)*Double.valueOf(value2))), "Max iterations without improvement=" + value2};
				runTest(args);
			}
		}
		
		for (String value : parameters.get("Proportion of elite individuals")) {
			String[] args = {"Proportion of elite individuals=" + value};
			runTest(args);
		}
		 */
		for (String value1 : parameters.get("Problem size")) {
			for (String value2 : parameters.get("Education rate")) {
				String[] args = {"Problem size=" + value1, "Education rate=" + value2};
				runTest(args);
			}
		}
	}
	
	private void runTest(String[] args) {
		for (int i = 0; i < 5; i++) {
			System.out.println("Running test " + (i+1) + " with parameters:");
			for (String arg : args) {
				System.out.println(arg);
			}
			HGSmain.main(args);
		}
	}
}
