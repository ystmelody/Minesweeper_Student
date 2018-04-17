package src;
import src.Action.ACTION;
import java.util.LinkedList;
import java.util.HashMap;

public class MyAI extends AI {
	// ########################## INSTRUCTIONS ##########################
	// 
	// ###################### END OF INSTURCTIONS #######################
	
	
	// ######################### CODE BELOW HERE #########################
	private final int ROW_DIMENSION;
	private final int COL_DIMENSION;
	private final int TOTAL_MINES;
	private final int START_X;
	private final int START_Y;
	
	// World
	private HashMap<String, Integer>[][] boardInfo;
	// boardInfo[i][j] will be dictionary of the form:
	//		{number: r, bomb_neighbors: q, covered_neighbors: s}
	// where:
	// 	 r is the number on that is displaying on the tile
	// 	 q is the number of bordering bombs already found
	//	 s is the number of bordering tiles still covered
	private enum BOMB_INFO {
		NOBOMB, BOMB, UNKNOWN
	}
	private BOMB_INFO[][] bombInfo;
	
	// Agent
	private Action prevAction;
	private int flagsLeft;
	
	// Buffers
	private LinkedList<Action> moveQ;
	private LinkedList<Integer> numberQ;
	
	
	// Coordinate Class
	private class Coordinate {
		public int x;
		public int y;
		Coordinate(int x, int y) { this.x = x; this.y = y; }
		public String toString() {
			return "(" + this.x + "," + this.y + ")";
		}
	}
	
	// DELETE
	private int moveNumber = 0;
	
	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		this.ROW_DIMENSION = rowDimension;
		this.COL_DIMENSION = colDimension;
		this.TOTAL_MINES = this.flagsLeft = totalMines;
		this.START_X = startX;
		this.START_Y = startY;
		
		// this.boardInfo = new int[this.ROW_DIMENSION+1][this.COL_DIMENSION+1];
		this.boardInfo = new HashMap[this.ROW_DIMENSION+1][this.COL_DIMENSION+1];
		initBoardInfo();
		this.bombInfo = new BOMB_INFO[this.ROW_DIMENSION+1][this.COL_DIMENSION+1];
		initBombBoard();
		
		this.prevAction = new Action(ACTION.UNCOVER, START_X, START_Y);
		this.doAction(this.prevAction);
		this.moveQ = new LinkedList<Action>();
		this.numberQ = new LinkedList<Integer>();
	}
	
	// ### Implement getAction() ###
	public Action getAction(int number) {
		this.moveNumber++;
//		System.out.println("Dimension: " + this.ROW_DIMENSION + " x " + this.COL_DIMENSION);
		System.out.println("Flags Left: " + this.flagsLeft);
//		System.out.println("StartX: " + this.START_X);
//		System.out.println("StartY: " + this.START_Y);
		System.out.println("Number of Last Tile (From MYAI): " + number);
		System.out.println("Move #: " + this.moveNumber);
		
		if (prevAction.action == ACTION.UNCOVER) {
			this.boardInfo[prevAction.y][prevAction.x].put("number", number);
		}
		
		if (number == 0) {
			clearZeroPatch(prevAction.x, prevAction.y);
		}
		
		Action nextAction = getNextMove();
		this.prevAction = nextAction;
		this.doAction(nextAction);
		return nextAction;
	}
	
	private void doAction(Action a) {
		/*
		 * Make the necessary updates to state after doing an action
		 */
		System.out.println("Doing action...");
		System.out.println(a);
		LinkedList<Coordinate> allNeighbors = getAllNeighbors(a.x, a.y);
		if (a.action == ACTION.UNCOVER) {
			this.bombInfo[a.y][a.x] = BOMB_INFO.NOBOMB;	
			// Update neighbors' information.
			for (Coordinate nb : allNeighbors) {
				HashMap<String, Integer> tile = this.boardInfo[nb.y][nb.x];
				//System.out.print("Before: " + tile.get("covered_neighbors"));
				tile.put("covered_neighbors", tile.get("covered_neighbors")-1);
				//System.out.print("After: " + tile.get("covered_neighbors"));
			}
		} else if (a.action == ACTION.FLAG) {
			// Flagging occurs when a bomb has been found. Update neighbors'
			// bomb_neighbors info.
			this.bombInfo[a.y][a.x] = BOMB_INFO.BOMB;
			for (Coordinate nb : allNeighbors) {
				HashMap<String, Integer> tile = this.boardInfo[nb.y][nb.x];
				//System.out.println(nb);
				//System.out.println("Before (bomb neigh): " + tile.get("bomb_neighbors"));
				tile.put("bomb_neighbors", tile.get("bomb_neighbors")+1);
				//System.out.println("After: (bomb neigh)" + tile.get("bomb_neighbors"));
			}
			
		} else if (a.action == ACTION.UNFLAG) {
			// Should never happen
		}
		
	}
	
	private Action getNextMove() {
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		// Update bombInfo
		flagBombsFromCounts();
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		
		// Determine safe moves from "counts"
		findSafeTilesFromCounts();
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		
		// Run backtracking
		
		printAgentKnowledge();
		return new Action(ACTION.LEAVE);
	}
	

	
	private void flagBombsFromCounts() {
		/*
		 * Update bombInfo based on boardInfo
		 */
		
		/*
		 * Pseudocode:
		 * 
		 * if tile[i][j].number == tile[i][j].coveredTiles:
		 * 		mark all COVERED tiles as bombs if bomb status UNKOWN
		 */
		for (int y = 0; y < this.boardInfo.length; y++) {
			for (int x = 0; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				if (tile.get("number") == -1) {
					continue;
				}			
				if (tile.get("number") == tile.get("covered_neighbors")) {
					LinkedList<Coordinate> allNeighbors = getAllNeighbors(x,y);
					// Flag all covered neighbors. Mark them as bombs
					for (Coordinate nb : allNeighbors) {
						// Flag if bomb status unknown and still covered
						if (this.boardInfo[nb.y][nb.x].get("number") == -1 && this.bombInfo[nb.y][nb.x] == BOMB_INFO.UNKNOWN) {
							Action a = new Action(ACTION.FLAG, nb.x, nb.y);
							if (!inBuffer(a)) {
								this.moveQ.addLast(a);
							}	
						}
					}
				}
			}
		}
	}
	
	private Action[] findSafeTilesFromCounts() {
		/*
		 * 
		 */
		for (int y = 0; y < this.boardInfo.length; y++) {
			for (int x = 0; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				if (tile.get("number") == -1) {
					continue;
				}
				// If the number on a tile is same as number of neighbor bombs already found
				// all remaining neighbor tiles are safe
				
				/*
				 * Pseudocode:
				 * 
				 * if tile[i][j].number == tile[i][j].neighbor_bombs:
				 * 		allNbrs = getAllNeighbors(i,j)
				 * 		for nbr in allNbrs:
				 * 			if nbr is covered and not bomb:
				 * 				moveQ.append(nbr)
				 */
				if (tile.get("number") == tile.get("bomb_neighbors")) {
					LinkedList<Coordinate> allNeighbors = getAllNeighbors(x,y);
					// System.out.println("All Neighbors of " + x + "," + y + ": " + allNeighbors);
					
					// Add all covered neighbors to moveQ
					for (Coordinate nb : allNeighbors) {
						if (this.boardInfo[nb.y][nb.x].get("number") == -1 && this.bombInfo[nb.y][nb.x] != BOMB_INFO.BOMB) {
							Action a = new Action(ACTION.UNCOVER, nb.x, nb.y);
							if (!inBuffer(a)) {
								this.moveQ.addLast(a);
							}
						}
					}
				}
				
			}
		}
		return null;
	}
	
	private LinkedList<Coordinate> getAllNeighbors(int x, int y) {
		/*
		 * Return an list of all bordering tiles' coordinates 
		 */
		LinkedList<Coordinate> neighbors = new LinkedList<Coordinate>();
		
		int i = -1;
		while (i <= 1) {
			// start at row r-1 and stop at row r+1
			int x1 = x+i;
			int j = -1;
			while (j <= 1) {
				// start at col c-1 and stop at col c+1
				int y1 = y+j;
				if (x1 == x && y1 == y) { j++; continue; }
				if (isInBounds(x1, y1)) {
					neighbors.addLast(new Coordinate(x1,y1));
				}
				j++;
			}
			i++;
		}
		return neighbors;
	}
	
	private void clearZeroPatch(int x, int y) {
		// Clears the squares surrounding (x,y) where (x,y) is 0.
		int i = -1;
		while (i <= 1) {
			// start at row y-1 and stop at row y+1
			int y1 = y+i;
			int j = -1;
			while (j <= 1) {
				// start at col x-1 and stop at row x+1
				int x1 = x+j;
				if (isInBounds(x1, y1) && this.boardInfo[y1][x1].get("number") == -1) {
					Action a = new Action(ACTION.UNCOVER, x1, y1);
					// Check if action is already in the buffer
					if (!inBuffer(a)) {
						this.moveQ.addLast(a);
					}
				}
				j++;
			}
			i++;
		}
	}
	
	private boolean inBuffer(Action a) {
		for (Action a2 : this.moveQ) {
			if (a.action == a2.action && a.x == a2.x && a.y == a2.y) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isInBounds(int x, int y) {
		if (x < 1 || x > this.COL_DIMENSION || y < 1 || y > this.ROW_DIMENSION) {
			return false;
		}
		return true;
	}
	
	private void initBoardInfo() {
		for (int y = this.boardInfo.length-1; y >= 0; y--) {
			for (int x = 0; x < this.boardInfo[0].length; x++) {
				
				if (x == 1 && y == 6) {
					System.out.println(this.getAllNeighbors(x, y));
				}
				HashMap<String, Integer> m = new HashMap<String, Integer>();
				m.put("number", -1);
				m.put("bomb_neighbors", 0);
				m.put("covered_neighbors", this.getAllNeighbors(x, y).size());
				
				this.boardInfo[y][x] = m;
			}
		}
	}
	
	private void initBombBoard() {
		for (int i = 0; i < this.bombInfo.length; i++) {
			for (int j = 0; j < this.bombInfo[i].length; j++) {
				this.bombInfo[i][j] = BOMB_INFO.UNKNOWN;
			}
		}
	}
	
	private void printAgentKnowledge() {
		System.out.println("####### Agent's Knowledge ########");
		System.out.println("----- board coverage ------");
		// Board Coverage
		for (int y = this.boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				if (this.bombInfo[y][x] == BOMB_INFO.BOMB) {
					System.out.printf("%3s","F");
				} else if (tile.get("number") == -1) {
					System.out.printf("%3s",".");
				}  else {
					System.out.printf("%3d",tile.get("number"));
				}
			}
			System.out.println();
		}
		// covered neighbors counts
		System.out.println("----- covered_neighbors ------");
		for (int y = this.boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				System.out.printf("%3d",tile.get("covered_neighbors"));
			}
			System.out.println();
		}
		// neighboring bombs found
		System.out.println("----- bomb_neighbors ------");
		for (int y = this.boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				System.out.printf("%3d",tile.get("bomb_neighbors"));
			}
			System.out.println();
		}
	}
}
