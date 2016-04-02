package hgsadc.implementations;

import java.util.ArrayList;
import java.util.Collections;

public class Move {
	
	private int numberOfMoves = 7;
	
	public ArrayList<Integer> doMove(Integer u, Integer v, ArrayList<Integer> installations, int moveNumber) {
		ArrayList<Integer> newInstallations;
		Integer x = getSuccessor(u, installations);
		Integer y = getSuccessor(v, installations);
		switch (moveNumber) {
			case 1: newInstallations = move1(u, v, installations);
				break;
			case 2: newInstallations = move2(u, v, x, installations);
				break;
			case 3: newInstallations = move3(u, v, x, installations);
				break;
			case 4: newInstallations = move4(u, v, installations);
				break;
			case 5: newInstallations = move5(u, v, x, installations);
				break;
			case 6: newInstallations = move6(u, v, x, y, installations);
				break;
			case 7: newInstallations = move7(v, x, installations);
				break;
			default: newInstallations = installations;
				System.out.println("Illegal move number!");
				break;
			}
		return newInstallations;
	}
	
	public int getNumberOfMoves() {
		return numberOfMoves;
	}
	
	//returns the successor of a in installations, null if a is the last element
	private Integer getSuccessor(Integer a, ArrayList<Integer> installations) {
		int indexA = installations.indexOf(a);
		if (indexA == installations.size()-1) {
			//no successor
			return null;
		}
		else {
			return installations.get(indexA+1);
		}
	}
	
	//remove u and place it after v
	private ArrayList<Integer> move1(Integer u, Integer v, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		newInstallations.remove(u);
		int indexV = newInstallations.indexOf(v); 
		newInstallations.add(indexV+1, u);
		return newInstallations;
	}
	
	//remove u and x and place u and x after v
	private ArrayList<Integer> move2(Integer u, Integer v, Integer x, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		if (x != null) { //if x is null, then u is the last element of installations and nothing is done
			newInstallations.remove(u);
			newInstallations.remove(x);
			int indexV = newInstallations.indexOf(v); 
			newInstallations.add(indexV+1, u);
			newInstallations.add(indexV+2, x);
		}
		return newInstallations;
	}
	
	//remove u and x and place x and u after v (note the insertion sequence)
	private ArrayList<Integer> move3(Integer u, Integer v, Integer x, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		if (x != null) { //if x is null, then u is the last element of installations and nothing is done
			newInstallations.remove(u);
			newInstallations.remove(x);
			int indexV = newInstallations.indexOf(v); 
			newInstallations.add(indexV+1, x);
			newInstallations.add(indexV+2, u);
		}
		return newInstallations;
	}
	
	//swap the place of u and v
	private ArrayList<Integer> move4(Integer u, Integer v, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		int indexU = newInstallations.indexOf(u);
		int indexV = newInstallations.indexOf(v);
		Collections.swap(newInstallations, indexU, indexV);
		return newInstallations;
	}
	
	//swap the place of u and x with v
	private ArrayList<Integer> move5(Integer u, Integer v, Integer x, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		if (x != null) { //if x is null, then u is the last element of installations and nothing is done
			//swap the place of u and v
			int indexU = newInstallations.indexOf(u);
			int indexV = newInstallations.indexOf(v);
			Collections.swap(newInstallations, indexU, indexV);
			//remove x and insert it after u (previous index of v)
			newInstallations.remove(x);
			indexU = newInstallations.indexOf(u);
			newInstallations.add(indexU+1, x);
		}
		return newInstallations;
	}
	
	//swap u and x with v and y
	private ArrayList<Integer> move6(Integer u, Integer v, Integer x, Integer y, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		if (x != null && y != null) { //if x or y is null, then u or v is the last element of installations and nothing is done
			int indexU = newInstallations.indexOf(u);
			int indexV = newInstallations.indexOf(v);
			int indexX = newInstallations.indexOf(x);
			int indexY = newInstallations.indexOf(y);
			Collections.swap(newInstallations, indexU, indexV);
			Collections.swap(newInstallations, indexX, indexY);
		}
		return newInstallations;
	}
	
	//swap x and v
	private ArrayList<Integer> move7(Integer v, Integer x, ArrayList<Integer> installations) {
		ArrayList<Integer> newInstallations = new ArrayList<Integer>(installations);
		if (x != null) { //if x is null, then u is the last element of installations and nothing is done
			int indexX = newInstallations.indexOf(x);
			int indexV = newInstallations.indexOf(v);
			Collections.swap(newInstallations, indexX, indexV);
		}
		return newInstallations;
	}
	
}
