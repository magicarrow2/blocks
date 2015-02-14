package pt314.blocks.game;

import java.awt.Color;
import javax.swing.ImageIcon;

/**
 * This is that target block, which must be moved out of the board.
 */
public class TargetBlock extends HorizontalBlock {

    public TargetBlock() {}
        
    @Override
    public ImageIcon getImageIcon() {
        return new ImageIcon(getClass().getResource("/images/blocks-yellow.png"));
    }

    @Override
    public Color getBackgroundColor() {
        return Color.YELLOW;
    }
}
