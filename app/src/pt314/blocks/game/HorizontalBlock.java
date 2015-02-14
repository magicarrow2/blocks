package pt314.blocks.game;

import java.awt.Color;
import javax.swing.ImageIcon;

/**
 * This type of block can only move horizontally (left or right).
 */
public class HorizontalBlock extends Block {

	public HorizontalBlock() {}

	@Override
	public boolean isValidDirection(Direction dir) {
		return dir == Direction.LEFT || dir == Direction.RIGHT;
	}

    @Override
    public ImageIcon getImageIcon() {
        return new ImageIcon(getClass().getResource("/images/block-blue.png"));
    }

    @Override
    public Color getBackgroundColor() {
        return Color.BLUE;
    }
}
