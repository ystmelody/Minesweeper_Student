package src;

public class Tile {
	// *** Change to private
	public boolean bomb;
	public boolean covered;
	public int nNeighborBombs;
	
	public Tile() {
		this(false, true, 0);
	}
	
	public Tile(boolean bomb, boolean covered, int nNeighborBombs) {
		this.bomb = false;
		this.covered = true;
		this.nNeighborBombs = 0;
	}
	
	public void setBomb(boolean isBomb) {
		this.bomb = isBomb;
	}
	
	public boolean isBomb() {
		return this.bomb;
	}
	
	public void setCovered(boolean isCovered) {
		this.covered = isCovered;
	}
	
	public boolean isCovered() {
		return this.covered;
	}
}
