package src;
import src.Action.ACTION;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	
	// Coordinate Class
	private class Coordinate {
		public int x;
		public int y;
		Coordinate(int x, int y) { this.x = x; this.y = y; }
		public String toString() {
			return "(" + this.x + "," + this.y + ")";
		}
		public boolean equals(Object other) {
			Coordinate otherCoord = (Coordinate) other;
			return this.x == otherCoord.x && this.y == otherCoord.y;
		}
	}
	
	// DELETE
	private int moveNumber = 0;
	
	@SuppressWarnings("unchecked")
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
		this.doAction(this.prevAction, this.boardInfo, this.bombInfo);
		this.moveQ = new LinkedList<Action>();
	}
	
	// ### Implement getAction() ###
	public Action getAction(int number) {
		this.moveNumber++;
		//System.out.println("Flags Left: " + this.flagsLeft);
		//System.out.println("Number of Last Tile (From MYAI): " + number);
		//System.out.println("Move #: " + this.moveNumber);
		
		if (prevAction.action == ACTION.UNCOVER) {
			this.boardInfo[prevAction.y][prevAction.x].put("number", number);
		}
		
		if (number == 0) {
			clearZeroPatch(prevAction.x, prevAction.y);
		}
		
		Action nextAction = getNextMove();
		this.prevAction = nextAction;
		this.doAction(nextAction, this.boardInfo, this.bombInfo);
		return nextAction;
	}
	
	private void doAction(Action a, HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		/*
		 * Make the necessary updates to state after doing an action
		 */
		// System.out.println("Doing action...");
		// System.out.println(a);
		LinkedList<Coordinate> allNeighbors = getAllNeighbors(a.x, a.y);
		if (a.action == ACTION.UNCOVER) {
			bombInfo[a.y][a.x] = BOMB_INFO.NOBOMB;	
			// Update neighbors' information.
			for (Coordinate nb : allNeighbors) {
				HashMap<String, Integer> tile = boardInfo[nb.y][nb.x];
				//System.out.print("Before: " + tile.get("covered_neighbors"));
				tile.put("covered_neighbors", tile.get("covered_neighbors")-1);
				//System.out.print("After: " + tile.get("covered_neighbors"));
			}
		} else if (a.action == ACTION.FLAG) {
			// Flagging occurs when a bomb has been found. Update neighbors'
			// bomb_neighbors info.
			bombInfo[a.y][a.x] = BOMB_INFO.BOMB;
			for (Coordinate nb : allNeighbors) {
				HashMap<String, Integer> tile = boardInfo[nb.y][nb.x];
				tile.put("bomb_neighbors", tile.get("bomb_neighbors")+1);
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
		this.flagBombsFromCounts(this.boardInfo, this.bombInfo);
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		
		// Determine safe moves from "counts"
		this.uncoverSafeTiles(this.boardInfo, this.bombInfo);
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		
		// Run backtracking
		// System.out.println("### Finding Contradiction ###");
		for (int y = 1; y < this.boardInfo.length; y++) {
			for (int x = 1; x < this.boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = this.boardInfo[y][x];
				if (tile.get("number") == -1 && this.bombInfo[y][x] == BOMB_INFO.UNKNOWN) {
					if (this.contradictionSearch(new Coordinate(x,y))) {
						Action a = new Action(ACTION.UNCOVER, x, y);
						this.moveQ.addLast(a);
					}
				}
			}
		}
		if (!moveQ.isEmpty()) {
			return moveQ.removeFirst();
		}
		
		// printAgentKnowledge();
		return new Action(ACTION.LEAVE);
	}
	
	private boolean contradictionSearch(Coordinate tile) {
		/* Inputs:
		 * 		x,y: starting point
		 * 		maxDepth: maximum depth for recursion
		 * 
		 * Outputs:
		 * 		false if search is inconclusive, true if (x,y) is a bomb.
		 * 
		 * Description:
		 * Assume (x,y) is a bomb and attempts to derive contradiction.
		 * If contradiction, then we proved (x,y) is not a bomb).
		 */
		HashMap<String, Integer>[][] boardCopy = deepCopy(this.boardInfo);
		// copy bomb info
		BOMB_INFO[][] bombInfoCopy = new BOMB_INFO[this.bombInfo.length][this.bombInfo[0].length];
		for (int i = 0; i < this.bombInfo.length; i++) {
			for (int j = 0; j < this.bombInfo[0].length; j++) {
				bombInfoCopy[i][j] = this.bombInfo[i][j];
			}
		}
		// Assume (x,y) is a bomb (and flag it).
		Action a = new Action(ACTION.FLAG, tile.x, tile.y);
		this.doAction(a, boardCopy, bombInfoCopy);
		return contradictionSearch(tile, boardCopy, bombInfoCopy);
	}
	
	private boolean contradictionSearch(Coordinate tile, HashMap<String,Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		/*
		 * Pseudocode:
		 * 
		 * flagBombs;
		 * uncoverSafeTiles;
		 * if contradiction:
		 * 		return true;
		 * else:
		 * 		for nbr : neighbors of tile :
		 * 			explored.add(tile)
		 * 			backtrackSearch(nbr, boardInfo, bombInfo, depth-1, explored)
		 * 
		 */
		LinkedList<Coordinate> safeTiles = this.findSafeTilesFromCounts(boardInfo, bombInfo);
		// System.out.println(safeTiles);
		for (Coordinate t : safeTiles) {
			boardInfo[t.y][t.x].put("number", -2);
			Action a = new Action(ACTION.UNCOVER, t.x, t.y);
			this.doAction(a, boardInfo, bombInfo);
		}
		
		// System.out.println("######### Hypothetical Board ########");
		// this.printInfo(boardInfo, bombInfo);
		
		return contradiction(boardInfo, bombInfo);
	}
	
	private boolean contradiction(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		/*
		 * A contradiction occurs when, for a given (uncoverd) tile:
		 * 		1) tile.number > tile.covered_neighbors or
		 * 		2) tile.number < tile.bomb_neighbors
		 * 
		 * We only detect type 1 here.
		 */
		
		for (int y = boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
				if (tile.get("number") != -1 && tile.get("number") != -2) {
					if (tile.get("number") > tile.get("covered_neighbors")) {
						System.out.println("Contradiction at " + x + "," + y);
						return true;
					}	
				}
			}
		}
		return false;
	}
	
	private HashMap<String, Integer>[][] deepCopy(HashMap<String, Integer>[][] boardInfo) {
		@SuppressWarnings("unchecked")
		HashMap<String, Integer>[][] boardCopy = new HashMap[boardInfo.length][boardInfo[0].length];
		for (int i = 0; i < boardInfo.length; i++) {
			for (int j = 0; j < boardInfo[0].length; j++) {
				HashMap<String, Integer> tileCopy = new HashMap<String, Integer>();
				HashMap<String, Integer> tile = this.boardInfo[i][j];
				tileCopy.put("number", tile.get("number"));
				tileCopy.put("bomb_neighbors", tile.get("bomb_neighbors"));
				tileCopy.put("covered_neighbors", tile.get("covered_neighbors"));
				
				boardCopy[i][j] = tileCopy;
			}
		}		
		return boardCopy;
	}
	
	private LinkedList<Coordinate> findBombsFromCounts(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		/* Finds (new) bombs from counts
		 * 
		 * Pseudocode:
		 * 
		 * if tile[i][j].number == tile[i][j].coveredTiles:
		 * 		if bombInfo[i][j] == UNKNOWN
		 * 			bombTile.append(tile[i][j])
		 */
		
		LinkedList<Coordinate> newBombs = new LinkedList<Coordinate>();
		for (int y = 0; y < boardInfo.length; y++) {
			for (int x = 0; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
				if (tile.get("number") == -1) {
					continue;
				}			
				if (tile.get("number") == tile.get("covered_neighbors")) {
					LinkedList<Coordinate> allNeighbors = getAllNeighbors(x,y);
					// Flag all covered neighbors. Mark them as bombs
					for (Coordinate nb : allNeighbors) {
						// Flag if bomb status unknown and still covered
						if (boardInfo[nb.y][nb.x].get("number") == -1 && bombInfo[nb.y][nb.x] == BOMB_INFO.UNKNOWN) {
							newBombs.addLast(nb);	
						}
					}
				}
			}
		}
		return newBombs;
	}
	
	private void flagBombsFromCounts(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		LinkedList<Coordinate> newBombs = this.findBombsFromCounts(boardInfo, bombInfo);
		for (Coordinate newBomb : newBombs) {
			Action a = new Action(ACTION.FLAG, newBomb.x, newBomb.y);
			if (!inBuffer(a)) {
				this.moveQ.addLast(a);
			}
		}
	}
	
	private void uncoverSafeTiles(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		// Uncover the safe tiles (that are still covered)
		// Add all covered neighbors to moveQ
		LinkedList<Coordinate> safeTiles = this.findSafeTilesFromCounts(boardInfo, bombInfo);
		for (Coordinate safeTile : safeTiles) {
			if (boardInfo[safeTile.y][safeTile.x].get("number") == -1 && bombInfo[safeTile.y][safeTile.x] != BOMB_INFO.BOMB) {
				Action a = new Action(ACTION.UNCOVER, safeTile.x, safeTile.y);
				if (!inBuffer(a)) {
					this.moveQ.addLast(a);
				}
			}
		}
	}
	
	private LinkedList<Coordinate> findSafeTilesFromCounts(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		/*
		 * Finds covered tiles that are safe to uncover.
		 */
		LinkedList<Coordinate> safeTiles = new LinkedList<Coordinate>();
		for (int y = 0; y < boardInfo.length; y++) {
			for (int x = 0; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
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
				 * 				safeTiles.append(nbr)
				 */
				if (tile.get("number") == tile.get("bomb_neighbors")) {
					LinkedList<Coordinate> allNeighbors = getAllNeighbors(x,y);
					// System.out.println("All Neighbors of " + x + "," + y + ": " + allNeighbors);
					
					// Add all covered neighbors to moveQ
					for (Coordinate nb : allNeighbors) {
						if (boardInfo[nb.y][nb.x].get("number") == -1 && bombInfo[nb.y][nb.x] != BOMB_INFO.BOMB) {
							if (!safeTiles.contains(nb)) {
								safeTiles.addLast(nb);
							}
						}
					}
				}
				
			}
		}
		return safeTiles;
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
				if (isInBounds(x1, y1) && boardInfo[y1][x1].get("number") == -1) {
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
		this.printInfo(this.boardInfo, this.bombInfo);
	}
	
	private void printInfo(HashMap<String, Integer>[][] boardInfo, BOMB_INFO[][] bombInfo) {
		System.out.println("----- board coverage ------");
		// Board Coverage
		for (int y = boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
				if (bombInfo[y][x] == BOMB_INFO.BOMB) {
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
			for (int x = 1; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
				System.out.printf("%3d",tile.get("covered_neighbors"));
			}
			System.out.println();
		}
		// neighboring bombs found
		System.out.println("----- bomb_neighbors ------");
		for (int y = boardInfo.length-1; y >= 1; y--) {
			for (int x = 1; x < boardInfo[0].length; x++) {
				HashMap<String, Integer> tile = boardInfo[y][x];
				System.out.printf("%3d",tile.get("bomb_neighbors"));
			}
			System.out.println();
		}
	}
}
