package src;
import java.util.List;
import java.util.Arrays;

public abstract class AI {	
	public static String[] VALID_ACTIONS = {"L", "U", "F"};
	/* Percepts:
	  	- nNeighborBombs: the number on the last uncovered tile
	  	- flagsLeft: the number of unused flags remaining
		- rows: row dimension of board
		- cols: col dimension of board
	 */
	public abstract Action getAction(int nNeighborBombs, int flagsLeft, int rows, int cols);
}
