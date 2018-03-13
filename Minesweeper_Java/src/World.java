package src;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

/*	Author: John Lu
 * 	Description: TO DO
 */

public class World {
	// Variables about the game board
	private int rowDimension;
	private int colDimension;
	private Tile[][] board;
	private int totalMines = 0;  // the # bombs this board has

	// Variables about the agent
	private AI agent;
	private int score;
	private int flagsLeft;
	
	// for faster score calculation and checking game end condition.
	private int coveredTiles;
	
	// For debugging
	private static int[][] BOMB_GRID;
	
	// Tile inner class to represent board squares
	private class Tile {
		private boolean mine = false;
		private boolean covered = true;
		private boolean flagged = false;
		private int number = 0;
	}
	
	private static final boolean DEBUG = true;
    // ---------------------------- Constructors -----------------------------	
	public World(String filename) {
		if (filename != null) {
			this.createBoardFromFile(filename);
		} else {
			// Create some default world
		}
	}
	
	private void createBoardFromFile(String filename) {
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
		// ---------------------- Populate Instance Variables ----------------------
		this.score = 0;
		this.rowDimension = nRows;
		this.colDimension = nCols;
		this.board = new Tile[rowDimension][colDimension];
		// Note: this method may be called only after board is instantiated.
		// This method will set the totalMines and flagsLeft instance variables
		this.createBoardFromGrid(nRows, nCols, grid);
		
		// Reveal the starting tile
		this.uncover(startX, startY);
	}
		
	private void uncoverAll() {
		for (int i = 0; i < this.rowDimension; i++) {
			for (int j = 0; j < this.colDimension; j++) {
				this.board[i][j].covered = false;
			}
		}
	}
	
	private void uncover(int x, int y) {
		this.board[rowDimension-y][x-1].covered = false;
	}
	
	// ========================== RUN LOOP ==============================
	public void run(AI ai) {
		Action actionObj;
		boolean gameOver = false;
		// Display fully covered board
		this.printBoard();
		System.out.println();
		
		// Loop until game is over.
		while (!gameOver) {
			System.out.println("------------------ Percepts ------------------ ");
			System.out.println("Board Dim: " + rowDimension + " x " + colDimension
					+ "   Flags Left: " + flagsLeft);
			System.out.println();
			
			// Ask agent for its action
			actionObj = ai.getAction(this.totalMines, this.flagsLeft, this.rowDimension, this.colDimension);
			if (DEBUG) {
				System.out.println(actionObj);
			}
			gameOver = this.performAction(actionObj);
			this.printBoard();
		}
		
		// ************* To Do: Calculate score here ************
	}
	
	// returns true if game is over
	private boolean performAction(Action actionObj) {
		String action;
		int x, y;
		action = actionObj.getAction();
		if (action.equals("L")) {
			System.out.println("Leaving World");
			this.uncoverAll();
			return true;  // quit game
		}
		x = actionObj.getX();
		y = actionObj.getY();
		Tile tile = this.board[rowDimension-y][x-1];
		switch (action) {
			case "U":
				System.out.println("Uncover");
				tile.covered = false;
				if (tile.mine) {
					this.uncoverAll();
					return true;
				}
				break;
			case "F":
				System.out.println("Flagging/Unflagging");
				// If already flagged, then unflag it
				if (tile.flagged) {
					tile.flagged = false;
					this.flagsLeft++;
				} else {
					// If unflagged, then flag it
					tile.flagged = true;
					this.flagsLeft--;
				}
				break;
		}
		return false;
	}
	
	/* 
	 * Description: This method prints the current state of the board.
	 * Inputs: 		None
	 * Outputs		Prints the current state of the boad.
	 */
	public void printBoard() {
		String yAxisLabelFmt = "%-2d| ";
		String xAxisLabelFmt = "%-4s";
		String gridElemStrFmt = "%-4s";
		String gridElemIntFmt = "%-4d";
		System.out.println("\n---------------- Game Board ------------------");
		System.out.println();
		for (int i = 0; i < this.rowDimension; i++) {
			// Print Row Numbers
			System.out.printf(yAxisLabelFmt,this.rowDimension-i);
			for (int j = 0; j < this.colDimension; j++) {
				Tile tile = this.board[i][j];
				if (tile.covered) {
					if (tile.flagged) {
						System.out.printf(gridElemStrFmt, "F");
					} else {
						System.out.printf(gridElemStrFmt, ".");
					}
				} else {
					// Uncovered tile
					if (tile.mine) {
						System.out.printf(gridElemStrFmt, "*");
					} else {
						System.out.printf(gridElemIntFmt, tile.number);
					}
				}
			}
			System.out.println();
		}
		// Print Bottom Line Above Column Numbers
		System.out.printf(xAxisLabelFmt, "");
		for (int j = 0; j < this.colDimension; j++) {
			System.out.printf(xAxisLabelFmt, "-");
		}
		System.out.println();
		// Print Column Numbers
		for (int i = 0; i < this.colDimension+1; i++) {
			if (i == 0) {
				System.out.printf(xAxisLabelFmt, "");
			} else {
				System.out.printf(xAxisLabelFmt, String.valueOf(i));
			}
		}
		System.out.println();
	}
	// ======================= END RUN LOOP FUNCTIONS =========================
	
	// ---------------------- Board Generation Methods ------------------------
	/* Description: This method creates an n x m dimensional board of integers
	 * 				where entry i,j is the number of neighboring bombs.
	 * 
	 * Inputs:		rowDimension: the number of rows, n
	 * 				colDimension: the number of columns, m
	 * 				grid: a 2-d bit array of size n x m
	 */
	private Tile[][] createBoardFromGrid(int rowDimension, int colDimension, int[][] grid) {
		// *** For Debugging ***
		BOMB_GRID = grid;
		
		// Initialize board
		initializeBoard(rowDimension, colDimension);
		
		// Populate board with integers representing # neighboring bombs
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < colDimension; j++) {
				// Create a new Tile to represent location (i, j)
				if (grid[i][j] == 1) {
					board[i][j].mine = true;
					this.totalMines++;
					// Increment each neighbor bomb count
					int rStart = i-1, rEnd = i+1, cStart = j-1, cEnd = j+1;
					if (i == 0) { rStart++; }
					if (i == rowDimension-1 ) { rEnd--; }
					if (j == 0 ) { cStart++; }
					if (j == colDimension-1 ) { cEnd--; }
					for (int r = rStart; r <= rEnd; r++) {
						for (int c = cStart; c <= cEnd; c++) {
							if (r != i || c != j) {
								board[r][c].number++;
							}
						}
					}
				}
			}
		}
		this.flagsLeft = this.totalMines;
		return board;
	}
	
	/* Description: This method creates an n x m dimensional board with the
	 * 				specified number of bombs (bombs placed randomly)
	 * 
	 * Inputs:		rowDimension: the number of rows, n
	 * 				colDimension: the number of columns, m
	 * 				totalMines: the number of bombs
	 */
	private void createRandomBoard(int rowDimension, int colDimension, int totalMines) {
		// Initialize board
		initializeBoard(rowDimension, colDimension);
		int[][] randBombGrid = placeBombs(rowDimension, colDimension, totalMines);
		createBoardFromGrid(rowDimension, colDimension, randBombGrid);
	}
	
	/* Description: This method returns a 2-d bit-array where a 1 on entry (i,j) 
	 * 				indicates a bomb is present at (i,j) and 0 otherwise. Exactly
	 * 				totalMines will be present at termination.
	 * 
	 * Inputs:		rowDimension: the number of rows, n
	 * 				colDimension: the number of columns, m
	 * 				totalMines: the number of bombs
	 * 
	 * Returns:		2-d bit-array.
	 */
	private int[][] placeBombs(int rowDimension, int colDimension, int totalMines) {
		// Place bombs randomly
		int[][] bombs = new int[rowDimension][colDimension];
		int bombsPlaced = 0;
		Random rand = new Random();
		while (bombsPlaced < totalMines) {
			int r = rand.nextInt(rowDimension);
			int c = rand.nextInt(colDimension);
			// If bomb already exists at location (r, c),
			// then get another location
			if (bombs[r][c] == 0) {
				bombs[r][c] = 1;
				bombsPlaced++;
			}
		}
		return bombs;
	}
	
	/* Description: Initializes 2-d array to represent the game board.
	 * 
	 * Inputs:		rowDimension: the number of rows, n
	 * 				colDimension: the number of columns, m
	 */
	private void initializeBoard(int rowDimension, int colDimension) {
		for (int i = 0; i < this.rowDimension; i++) {
			for (int j = 0; j < this.colDimension; j++) {
				this.board[i][j] = new Tile();
			}
		}
	}
	// -------------------- End Board Generation Methods ----------------------
	
	
	// ----------------------- Debugging functions ---------------------------
	public void printBombGrid() {
		System.out.println("\n ---------------- Bomb Grid ------------------");
		for (int i = 0; i < BOMB_GRID.length; i++) {
			for (int j = 0; j < BOMB_GRID[0].length; j++) {
				System.out.print(BOMB_GRID[i][j] + " ");
			}
			System.out.println();
		}
	}
}
