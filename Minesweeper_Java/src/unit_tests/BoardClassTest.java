package src.unit_tests;

import org.junit.Test;

import src.Board;

import static org.junit.Assert.assertEquals;

public class BoardClassTest {
	
	@Test
	public void testing() {
		Board board = new Board(4,4,10);
		assertEquals(board.getNumBombs(),10);
	}

}
