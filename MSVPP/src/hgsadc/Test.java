package hgsadc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Test {
	
	public static void main(String[] args) {
		Test test = new Test();
		test.testVoyage();
	}
	
	private void testVoyage() {
		IO io = new IO("data/hgs/input/input data hgs.xls");
		ProblemData problemData = io.readData();
		
		ArrayList<Integer> installations1 = new ArrayList<Integer>();
		installations1.add(0);
		installations1.add(7);
		installations1.add(9);
		installations1.add(4);
		installations1.add(5);
		installations1.add(1);
		installations1.add(3);
		installations1.add(2);
		installations1.add(8);
		installations1.add(0);
		
		ArrayList<Integer> installations2 = new ArrayList<Integer>();
		installations2.add(0);
		installations2.add(2);
		installations2.add(3);
		installations2.add(4);
		installations2.add(5);
		installations2.add(1);
		installations2.add(9);
		installations2.add(7);
		installations2.add(8);
		installations2.add(0);
		
		Set<Integer> vesselDepartures = new HashSet<Integer>();
		vesselDepartures.add(0);
		vesselDepartures.add(4);
		Vessel vessel = new Vessel("Far seeker", 1090, 12, 7129, 0.34, 0.0453,0.17,120000,7,1);
		Voyage voyage1 = new Voyage(installations1, vessel, vesselDepartures, 0, problemData);
		Voyage voyage2 = new Voyage(installations2, vessel, vesselDepartures, 0, problemData);
		System.out.println(voyage1);
		System.out.println(voyage2);
	}

}
