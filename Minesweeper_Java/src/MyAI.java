package src;

import src.Action.ACTION;

public class MyAI extends AI {
	private final int ROW_DIMENSION;
	private final int COL_DIMENSION;
	private final int TOTAL_MINES;
	private final int START_X;
	private final int START_Y;
	private int flagsLeft;
	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		this.ROW_DIMENSION = rowDimension;
		this.COL_DIMENSION = colDimension;
		this.TOTAL_MINES = this.flagsLeft = totalMines;
		this.START_X = startX;
		this.START_Y = startY;
	}
	// ======================== CODE BELOW HERE =========================
	
	// -------------------- YOUR VARIABLES GO HERE -------------------
	
	
	// --------------------- Implement getAction() --------------------
	public Action getAction(int number) {
		System.out.println("Dimension: " + this.ROW_DIMENSION + " x " + this.COL_DIMENSION);
		System.out.println("Flags Left: " + this.flagsLeft);
		System.out.println("StartX: " + this.START_X);
		System.out.println("StartY: " + this.START_Y);
		System.out.println("Number of Last Tile (From MYAI): " + number);

		return new Action(ACTION.LEAVE);	
	}
	
	// ------------------ YOUR HELPER METHODS GO HERE --------------------
	
	
	
}
