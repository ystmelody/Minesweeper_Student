package src;

/* Author: John Lu
 * 
 * Description:
 * 		This class represents a specific tile on the Minesweeper board.
 */

public class Tile {
	// *** Change to private
	private boolean isBomb;
	private boolean isCovered;
	private boolean isFlagged;
	private int nNeighborBombs;
	
	/* Description: Default Constructor creates a covered, unflagged tile
	 * 				that is not a bomb.
	 */
	public Tile() {
		this.isBomb = false;
		this.isCovered = true;
		this.isFlagged = false;
		this.nNeighborBombs = 0;
	}
	
	// ------------------ Mutators / Accessors ----------------------
	public void setIsBomb(boolean isBomb) {
		this.isBomb = isBomb;
	}
	
	public boolean isBomb() {
		return this.isBomb;
	}
	
	public void setIsCovered(boolean isCovered) {
		this.isCovered = isCovered;
	}
	
	public boolean isCovered() {
		return this.isCovered;
	}
	
	public void setIsFlagged(boolean isFlagged) {
		this.isFlagged = isFlagged;
	}
	
	public boolean isFlagged() {
		return this.isFlagged;
	}
	
	public int getNeighboringBombCount() {
		return this.nNeighborBombs;
	}
	
	public void incrementNeighboringBombCount() {
		this.nNeighborBombs++;
	}
}
