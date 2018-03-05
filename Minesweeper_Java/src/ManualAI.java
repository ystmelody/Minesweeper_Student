package src;
import java.util.Arrays;
import java.util.Scanner;

public class ManualAI extends AI {
	
	public Action getAction(int nNeighborBombs, int flagsLeft, int rows, int cols) {
		Scanner scanner = new Scanner(System.in);
		String actionStr = "", coordStr = "";
		int row = -1, col = -1;
		boolean valid = false;
		// Prompt user for input (loop until valid input given)
		while (!valid) {
			System.out.println("---------------- Available Actions ----------------");
			System.out.println("L: leave game   U: uncover tile   F: flag/unflag ");
			actionStr = scanner.nextLine().toUpperCase();
			if (actionStr.equals("L")) {
				return new Action("L");
			} else if (Arrays.asList(AI.VALID_ACTIONS).contains(actionStr)) {
				System.out.print("Enter (x,y) coordinate: ");
				coordStr = scanner.nextLine();
				String[] coords = coordStr.split(",");
				try {
					row = Integer.parseInt(coords[0]);
					col = Integer.parseInt(coords[1]);
				} catch (Exception e) {
					System.out.println("Illegal Coordinates: Coordinates must have format x,y");
					continue;
				}			
				valid = true;
			} else {
				System.out.println("Illegal Action");
			}
		}
		return new Action(actionStr, row, col);
	}
	
	public static void printArray(String[] coords) {
		for (int i = 0; i < coords.length; i++) {
			System.out.print("element " + i + ": " + coords[i] + " ");
		}
		System.out.println();
	}
}
