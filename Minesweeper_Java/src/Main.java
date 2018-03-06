package src;
import java.io.*;
import java.util.*;

public class Main {
	
	static final String VALID_OPTIONS = "fm";   // List of all valid options
	public static void main(String[] args) {
		// --------------- For Development ------------------
		final boolean DEBUG = true;
		
		String options = parseOptions(args);   // To store user-inputed options
		
		if (options.contains("m")) {
			Board board = createBoardFromFile("./worlds/world6x8_1.txt");
			ManualAI ai = new ManualAI();
			board.run(ai);
		}
		/*
		
		// Read from file if option f was provided
		if (options.contains("f")) {
			int nRows = -1, nCols = -1;
			int[][] grid = {};
			String filename = "";
			try {
				filename = args[1];
				BufferedReader in = new BufferedReader(new FileReader(filename));	
				// First line is two integers that denote the number of rows and columns
				String[] dims = in.readLine().split(" ");
				try {
					nRows = Integer.parseInt(dims[0]);
					nCols = Integer.parseInt(dims[1]);
					// text file has 2-d, space-delimited grid of 0's and 1's 
					// indicating locations of bombs (1 for bomb).
					grid = new int[nRows][nCols];
					int row = 0;
					String lineStr = in.readLine();
					while (lineStr != null) {
						String[] line = lineStr.split(" ");
						for (int i = 0; i < line.length; i++) {
							grid[row][i] = Integer.parseInt(line[i]);
						}
						row++;
						lineStr = in.readLine();			
					}

				} catch (NumberFormatException e) {
					System.out.println("World file " + filename + " has non-integer ");
					System.exit(1);
				}
				in.close();
			} catch(FileNotFoundException e) {
				System.out.println("File " + filename + " not found");
				System.exit(1);
				
			} catch(IOException e) {
				// Print something useful
			}		
			// Create board that corresponds to text file
			Board board = new Board(nRows, nCols, grid);
			board.printBoard();
			board.printBombGrid();
		} 
		// If no file is provided, create a default random board
		else  {
			Board board = new Board();
			ManualAI ai = new ManualAI();
			board.run(ai);
		}
		*/
	}
	
	public static Board createBoardFromFile(String filename) {
		// Runs Manual AI using sample text file
		int nRows = -1, nCols = -1, startX = -1, startY = -1;
		String[] dims, startTile;
		int[][] grid = {};
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));	
		}
		catch(FileNotFoundException e) {
			System.out.println("Invalid filename");
			System.exit(1);
		} 
		catch(Exception e) {

		}
		// -------------------- Read in the Dimensions ------------------------
		try {
			dims = in.readLine().split(" ");
			// Convert dimensions to integers
			nRows = Integer.parseInt(dims[0]);
			nCols = Integer.parseInt(dims[1]);
		} catch (Exception e) {
			System.out.println("Invalid Dimension Format found in file: " + filename);
			System.out.println(e.getLocalizedMessage());
			System.exit(1);
		}
		// -------------------- Read in the Starting Tile ------------------------
		try {
			startTile = in.readLine().split(" ");
			// Get the starting tile coordinates
			startX = Integer.parseInt(startTile[0]);
			startY = Integer.parseInt(startTile[1]);
			System.out.println("StartX: " + startX + " StartY: " + startY);
		} catch (Exception e) {
			System.out.println("Invalid Starting Square Format found in file: " + filename);
			System.exit(1);
		}
		// ---------------------------- Read Bomb Grid ---------------------------
		try {
			grid = new int[nRows][nCols];
			int row = 0;
			String lineStr = in.readLine();
			while (lineStr != null) {
				String[] line = lineStr.split(" ");
				for (int i = 0; i < line.length; i++) {
					grid[row][i] = Integer.parseInt(line[i]);
				}
				row++;
				lineStr = in.readLine();			
			}	
		} catch (Exception e) {
			System.out.println("Invalid bomb grid format found in file: " + filename);
		}
		// --------------------------- Close Reader ------------------------------
		try {
			in.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		Board board = new Board(nRows, nCols, grid);
		board.uncover(startX, startY);
		return board;
	}
	
	// For Debugging
	public static void printArray(int[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j]);
			}
			System.out.println();
		}
	}
	
	public static String parseOptions(String[] args) {
		String options = "";
		if (args.length != 0) {
			if (args[0].charAt(0) == '-') {
				String optString = args[0].substring(1, args[0].length());
				for (int i = 0; i < optString.length(); i++) {
					if (VALID_OPTIONS.contains(optString.substring(i,i+1))) {
						options += optString.substring(i,i+1);
					} else {
						// Invalid option foundd
						System.out.println("illegal option -- " + optString.charAt(i) );
						System.out.println("usage: " + "[-" + VALID_OPTIONS + "]"
								+ " [file...]");
						System.exit(1);
					}
				}
			}
		}
		return options;
	}
	
	public static Board createBoardFromGrid(int nRows, int nCols, int[][] grid) {
		return new Board(nRows, nCols, grid);
	}
	
	public static Board createRandomBoard(int nRows, int nCols, int nBombs) {
		// Initialize 2-D array board representation
		return new Board(nRows, nCols, nBombs);
		
	}
}
