package pt314.blocks;

import pt314.blocks.game.Block;
import pt314.blocks.game.Direction;
import pt314.blocks.game.GameBoard;
import pt314.blocks.game.HorizontalBlock;

/**
 * Just a test...
 */
public class Test {

	public static void main(String[] args) {
		
		GameBoard board = new GameBoard(3, 5);
		
		Block block1 = new HorizontalBlock();
		
		board.placeBlockAt(block1, 0, 0);
		
		board.print();
		for (int i = 0; i < 5; i++) {
			boolean result = board.moveBlock(0, i, Direction.RIGHT, 1);
			System.out.println(result);
			board.print();
		}
	}
}
