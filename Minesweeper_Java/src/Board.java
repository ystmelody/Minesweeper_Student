package src;
import java.util.Random;

/*	Author: John Lu
 * 	Description: TO DO
 */

public class Board {	
	private int nRows;  // unnecessary
	private int nCols;  // unnecessary
	private int[][] board;  // change to Tiles
	
	// For debugging
	private static int[][] BOMB_GRID;

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
		this.board = new int[nRows][nCols];
		createBoardFromGrid(nRows, nCols, grid);
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
		this.board = new int[nRows][nCols];
		createRandomBoard(nRows, nCols, nBombs);
	}
	// ------------------------------- Methods -------------------------------
	/* Description: This method creates an n x m dimensional board of integers
	 * 				where entry i,j is the number of neighboring bombs.
	 * 
	 * Inputs:		nRows: the number of rows, n
	 * 				nCols: the number of columns, m
	 * 				grid: a 2-d bit array of size n x m
	 */
	private int[][] createBoardFromGrid(int nRows, int nCols, int[][] grid) {
		// *** Debugging ***
		BOMB_GRID = grid;
		
		// Initialize board
		initializeBoard(nRows, nCols);
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				if (grid[i][j] == 1) {
					// Increment each neighbor bomb count
					int rStart = i-1, rEnd = i+1, cStart = j-1, cEnd = j+1;
					if (i == 0) { rStart++; }
					if (i == nRows-1 ) { rEnd--; }
					if (j == 0 ) { cStart++; }
					if (j == nCols-1 ) { cEnd--; }
					
					for (int r = rStart; r <= rEnd; r++) {
						for (int c = cStart; c <= cEnd; c++) {
							if (r != i || c != j) {
								board[r][c]++;
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
				this.board[i][j] = 0;
			}
		}
	}
	
	/* 
	 * Description: This method prints the current state of the board.
	 * Inputs: 		None
	 * Outputs		Prints the current state of the boad.
	 */
	public void printBoard() {
		System.out.println("\n ---------------- Game Board ------------------");
		for (int i = 0; i < this.nRows; i++) {
			for (int j = 0; j < this.nCols; j++) {
				System.out.print(this.board[i][j] + " ");
			}
			System.out.println();
		}
	}
	
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
