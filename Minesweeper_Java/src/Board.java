package src;
import java.util.Random;

/*	Author: John Lu
 * 	Description: TO DO
 */

public class Board {	
	private int nRows;
	private int nCols;
	// private int[][] board;  // change to Tiles
	private Tile[][] board;
	private int nBombs = 0;  // the # bombs this board has
	private int nFlagsLeft;
	
	// For debugging
	private static int[][] BOMB_GRID;
	
	// Tile inner class to represent board squares
	private class Tile {
		private boolean isBomb = false;
		private boolean isCovered = true;
		private boolean isFlagged = false;
		private int nNeighborBombs = 0;
	}
	
	private static final boolean DEBUG = true;
	
    // ---------------------------- Constructors -----------------------------
	// Description: Default constructor creates a random 10x10 board with 25 bombs
	public Board() {
		this(10, 10, 25);
	}
	
	/* Description:	This constructor takes the dimensions, n and m, of the board as input and
	 * 				a 2-d grid of of dimension n x m indicating bomb locations. grid[i][j] is
	 * 				0 iff location (i,j) has no bomb and is 1 iff location (i,j) has a bomb.
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				grid: a 2-d boolean array of size n x m
	 */
	public Board(int nRows, int nCols, int[][] grid) {
		this.nRows = nRows;
		this.nCols = nCols;
		this.board = new Tile[nRows][nCols];
		
		createBoardFromGrid(nRows, nCols, grid);
		this.nFlagsLeft = this.nBombs;
	}
	
	/* Description:	This constructor takes the integer dimensions, n and m, of the board as input and
	 * 				a third integer indicating the number of bombs for the board. A board of size
	 * 				n x m is created with the specified number of bombs placed randomly.
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				nBombs: the number of bombs to be randomly placed
	 */
	public Board(int nRows, int nCols, int nBombs) {
		this.nRows = nRows;
		this.nCols = nCols;
		// this.board = new int[nRows][nCols];
		this.board = new Tile[nRows][nCols];
		
		createRandomBoard(nRows, nCols, nBombs);
		this.nFlagsLeft = this.nBombs;
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
			System.out.println("Board Dim: " + nRows + " x " + nCols
					+ "   Flags Left: " + nFlagsLeft);
			System.out.println();
			
			// Ask agent for its action
			actionObj = ai.getAction(this.nBombs, this.nFlagsLeft, this.nRows, this.nCols);
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
		x = actionObj.getX();
		y = actionObj.getY();
		Tile tile = this.board[nRows-x][y-1];
		switch (action) {
			case "L":
				System.out.println("Leaving World");
				return true;  // quit game
			case "U":
				System.out.println("Uncover");
				tile.isCovered = false;
				if (tile.isBomb) {
					this.uncoverAll();
					return true;
				}
				break;
			case "F":
				System.out.println("Flagging/Unflagging");
				// If already flagged, then unflag it
				if (tile.isFlagged) {
					tile.isFlagged = false;
					this.nFlagsLeft++;
				} else {
					// If unflagged, then flag it
					tile.isFlagged = true;
					this.nFlagsLeft--;
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
		System.out.println("\n---------------- Game Board ------------------");
		for (int i = 0; i < this.nRows; i++) {
			for (int j = 0; j < this.nCols; j++) {
				Tile tile = this.board[i][j];
				if (tile.isCovered) {
					if (tile.isFlagged) {
						System.out.print("F ");
					} else {
						System.out.print(". ");
					}
				} else {
					// Uncovered tile
					if (tile.isBomb) {
						System.out.print("* ");
					} else {
						System.out.print(tile.nNeighborBombs + " ");
					}
				}
			}
			System.out.println();
		}
	}
	
	private void uncoverAll() {
		for (int i = 0; i < this.nRows; i++) {
			for (int j = 0; j < this.nCols; j++) {
				this.board[i][j].isCovered = false;
			}
		}
	}
	
	// ---------------------- Board Generation Methods ------------------------
	/* Description: This method creates an n x m dimensional board of integers
	 * 				where entry i,j is the number of neighboring bombs.
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				grid: a 2-d bit array of size n x m
	 */
	private Tile[][] createBoardFromGrid(int nRows, int nCols, int[][] grid) {
		// *** For Debugging ***
		BOMB_GRID = grid;
		
		// Initialize board
		initializeBoard(nRows, nCols);
		
		// Populate board with integers representing # neighboring bombs
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				// Create a new Tile to represent location (i, j)
				if (grid[i][j] == 1) {
					board[i][j].isBomb = true;
					this.nBombs++;
					// Increment each neighbor bomb count
					int rStart = i-1, rEnd = i+1, cStart = j-1, cEnd = j+1;
					if (i == 0) { rStart++; }
					if (i == nRows-1 ) { rEnd--; }
					if (j == 0 ) { cStart++; }
					if (j == nCols-1 ) { cEnd--; }
					for (int r = rStart; r <= rEnd; r++) {
						for (int c = cStart; c <= cEnd; c++) {
							if (r != i || c != j) {
								board[r][c].nNeighborBombs++;
							}
						}
					}
				}
			}
		}
		return board;
	}
	
	/* Description: This method creates an n x m dimensional board with the
	 * 				specified number of bombs (bombs placed randomly)
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				nBombs: the number of bombs
	 */
	private void createRandomBoard(int nRows, int nCols, int nBombs) {
		// Initialize board
		initializeBoard(nRows, nCols);
		int[][] randBombGrid = placeBombs(nRows, nCols, nBombs);
		createBoardFromGrid(nRows, nCols, randBombGrid);
	}
	
	/* Description: This method returns a 2-d bit-array where a 1 on entry (i,j) 
	 * 				indicates a bomb is present at (i,j) and 0 otherwise. Exactly
	 * 				nBombs will be present at termination.
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				nBombs: the number of bombs
	 * 
	 * Returns:		2-d bit-array.
	 */
	private int[][] placeBombs(int nRows, int nCols, int nBombs) {
		// Place bombs randomly
		int[][] bombs = new int[nRows][nCols];
		int bombsPlaced = 0;
		Random rand = new Random();
		while (bombsPlaced < nBombs) {
			int r = rand.nextInt(nRows);
			int c = rand.nextInt(nCols);
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
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 */
	private void initializeBoard(int nRows, int nCols) {
		for (int i = 0; i < this.nRows; i++) {
			for (int j = 0; j < this.nCols; j++) {
				this.board[i][j] = new Tile();
			}
		}
	}
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
