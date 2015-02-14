package pt314.blocks.game;

import java.awt.Color;
import javax.swing.ImageIcon;

/**
 * This type of block can only move vertically (up or down).
 */
public class VerticalBlock extends Block {

	public VerticalBlock() {}

	@Override
	public boolean isValidDirection(Direction dir) {
		return dir == Direction.UP || dir == Direction.DOWN;
	}

    @Override
    public ImageIcon getImageIcon() {
        return new ImageIcon(getClass().getResource("/images/block-red.png"));
    }

    @Override
    public Color getBackgroundColor() {
        return Color.RED;
    }
}
