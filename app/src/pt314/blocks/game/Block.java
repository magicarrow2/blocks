package pt314.blocks.game;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import javax.swing.ImageIcon;

/**
 * Generic block.
 * 
 * Subclasses should specify the directions in which they can be moved.
 */
public abstract class Block {
	
	public Block() {}

	public abstract boolean isValidDirection(Direction dir);
        public abstract ImageIcon getImageIcon();
        public abstract Color getBackgroundColor();
}
