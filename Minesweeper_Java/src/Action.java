package src;

public class Action {
	// Description: TO DO
	private String action;
	private int x;
	private int y;
	
	public String getAction() { return this.action; }
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public Action(String action, int x, int y) {
		this.action = action;
		this.x = x;
		this.y = y;
	}
	public Action(String action) {
		this.action = action;
		this.x = this.y = -1;
	}
	
	public String toString() {
		String retString = "Action: " + this.action + "\n";
		retString = retString + "x: " + this.x + " y: " + this.y;
		return retString;
	}
}
