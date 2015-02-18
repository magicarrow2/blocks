package pt314.blocks.game;

public class GameBoard {

    private int width;
    private int height;
    private Block[][] blocks;

    public GameBoard(int height, int width) {
        this.width = width;
        this.height = height;
        blocks = new Block[height][width];
    }

    /**
     * Place block at the specified location.
     *
     * If there is a block at the location, it is replaced by the new block.
     */
    public void placeBlockAt(Block block, int row, int col) {
        blocks[row][col] = block;
    }

    // TODO: Check for out of bounds
    public Block getBlockAt(int row, int col) {
        return blocks[row][col];
    }

    /**
     * Move block at the specified location.
     *
     * @param dir direction of movement.
     * @param dist absolute movement distance.
     *
     * @return <code>true</code> if and only if the move is possible. Movement
     * happens if possible.
     */
    public boolean moveBlock(int row, int col, Direction dir, int dist) {

	// TODO: throw exception if move is invalid, instead of using return value
        // Check input location is in bounds
        if (row < 0 || col < 0 || row >= blocks.length || col >= blocks[0].length) {
            return false;
        }

        Block block = blocks[row][col];

        // no block at specified location
        if (block == null) {
            return false;
        }

        // block cannot move in the specified direction
        if (!block.isValidDirection(dir)) {
            return false;
        }

        // determine new location
        int newRow = row;
        int newCol = col;
        dist = Math.abs(dist);
        if (dir == Direction.UP) {
            newRow -= dist;
        } else if (dir == Direction.DOWN) {
            newRow += dist;
        } else if (dir == Direction.LEFT) {
            newCol -= dist;
        } else if (dir == Direction.RIGHT) {
            newCol += dist;
        } else {
            return false;  //Unsupported direction was added to Direction enumeration.
        }

        // destination out of bounds
        if (!isWithinBounds(newRow, newCol)) {
            return false;
        }

        int dx = 0;
        int dy = 0;
        if (dir == Direction.UP) {
            dy = -1;
        } else if (dir == Direction.DOWN) {
            dy = 1;
        } else if (dir == Direction.LEFT) {
            dx = -1;
        } else if (dir == Direction.RIGHT) {
            dx = 1;
        }

        // check all cells from block location to destination
        int tmpRow = row;
        int tmpCol = col;
        for (int i = 0; i < dist; i++) {
            tmpRow += dy;
            tmpCol += dx;
            if (blocks[tmpRow][tmpCol] != null) {
                return false; // another block in the way
            }
        }

        blocks[newRow][newCol] = blocks[row][col];
        blocks[row][col] = null;
        return true;
    }

    /**
     * Check if a location is inside the board.
     */
    public boolean isWithinBounds(int row, int col) {
        if (row < 0 || row >= height) {
            return false;
        }
        if (col < 0 || col >= width) {
            return false;
        }
        return true;
    }

    /**
     * Print the board to standard out.
     */
    public void print() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Block block = blocks[row][col];
                char ch = '.';
                if (block instanceof TargetBlock) {
                    ch = 'T';
                } else if (block instanceof HorizontalBlock) {
                    ch = 'H';
                } else if (block instanceof VerticalBlock) {
                    ch = 'V';
                }
                System.out.print(ch);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Get gameboard width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get gameboard height
     */
    public int getHeight() {
        return height;
    }
    
    /**Check to see if the player has won.
     * 
     * @return True if target block has reached the right edge.
     */
    public boolean hasWon() {
        for(int i=0; i < height; i++) {
            if(blocks[i][width-1] instanceof HorizontalBlock) {
                return true;
            }
        }
        return false;
    }
}
