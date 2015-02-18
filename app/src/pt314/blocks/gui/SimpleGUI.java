package pt314.blocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pt314.blocks.game.Block;
import pt314.blocks.game.Direction;
import pt314.blocks.game.GameBoard;
import pt314.blocks.game.HorizontalBlock;
import pt314.blocks.game.TargetBlock;
import pt314.blocks.game.VerticalBlock;

/**
 * Simple GUI test...
 */
public class SimpleGUI extends JFrame implements ActionListener {

    private GameBoard board;

    // currently selected block
    private Block selectedBlock;
    private int selectedBlockRow;
    private int selectedBlockCol;

    private GridButton[][] buttonGrid;

    private JMenuBar menuBar;
    private JMenu gameMenu, helpMenu;
    private JMenuItem newGameMenuItem;
    private JMenuItem loadSecondGame;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;

    public SimpleGUI() {
        super("Blocks");

        initMenus();

        newGame();
        initGridDisplay();
        
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initMenus() {
        menuBar = new JMenuBar();

        gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        newGameMenuItem = new JMenuItem("New game");
        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame();
                reInitGridDisplay();
            }
        });
        gameMenu.add(newGameMenuItem);
        
        loadSecondGame = new JMenuItem("Load Second Game");
        loadSecondGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavedGame("puzzle-002.txt");
                reInitGridDisplay();
            }
        });
        gameMenu.add(loadSecondGame);

        gameMenu.addSeparator();

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gameMenu.add(exitMenuItem);

        aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SimpleGUI.this, "Sliding blocks!");
            }
        });
        helpMenu.add(aboutMenuItem);

        setJMenuBar(menuBar);
    }

    /**
     * Adds a buttonGrid, buttons, and a GridLayout to this SimpleGUI container.
     */
    private void initGridDisplay() {
        //Initialize grid display
        int numRows = board.getHeight();
        int numCols = board.getWidth();
        buttonGrid = new GridButton[numRows][numCols];
        setLayout(new GridLayout(numRows, numCols));
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                GridButton cell = new GridButton(row, col);
                cell.setPreferredSize(new Dimension(64, 64));
                cell.setBackground(Color.LIGHT_GRAY);
                cell.addActionListener(this);
                buttonGrid[row][col] = cell;
                add(cell);
            }
        }
        
        updateUI();
        pack();
    }
    
    /**
     * Removes the buttons from this SimpleGUI and replaces
     * them with new ones.  This is necessary to use after loading a new game.
     */
    private void reInitGridDisplay() {
        //Remove old grid
        for(int i=0; i<buttonGrid.length; i++)
            for(int j=0; j<buttonGrid[0].length; j++){
                remove(buttonGrid[i][j]);
            }
        
        //Reinitialize grid
        initGridDisplay();
        
        //Repaint parent container
        repaint();
    }
    
    /**
     * Updates the display of the board.
     */
    private void updateUI() {
        int numRows = board.getHeight();
        int numCols = board.getWidth();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Block block = board.getBlockAt(row, col);
                JButton cell = buttonGrid[row][col];
                if (block == null) {
                    if(cell.getIcon() != null)
                        cell.setIcon(null);
                } else {
                    try {
                        ImageIcon icon = block.getImageIcon();
                        cell.setIcon(icon);
                    } catch (Exception ex) {
                        System.out.print(ex);
                    }
                }
            }
        }
    }

    /**
     * Handle board clicks.
     *
     * Movement is done by first selecting a block, and then selecting the
     * destination.
     *
     * Whenever a block is clicked, it is selected, even if another block was
     * selected before.
     *
     * When an empty cell is clicked after a block is selected, the block is
     * moved if the move is valid.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle grid button clicks...
        GridButton cell = (GridButton) e.getSource();
        int row = cell.getRow();
        int col = cell.getCol();
        System.out.println("Clicked (" + row + ", " + col + ")");

        if (selectedBlock == null || board.getBlockAt(row, col) != null) {
            selectBlockAt(row, col);
        } else {
            moveSelectedBlockTo(row, col);
        }
    }

    /**
     * Select block at a specific location.
     *
     * If there is no block at the specified location, the previously selected
     * block remains selected.
     *
     * If there is a block at the specified location, the previous selection is
     * replaced.
     */
    private void selectBlockAt(int row, int col) {
        Block block = board.getBlockAt(row, col);
        if (block != null) {
            selectedBlock = block;
            selectedBlockRow = row;
            selectedBlockCol = col;
        }
    }

    /**
     * Try to move the currently selected block to a specific location.
     *
     * If the move is not possible, nothing happens.
     */
    private void moveSelectedBlockTo(int row, int col) {

        int vertDist = row - selectedBlockRow;
        int horzDist = col - selectedBlockCol;

        if (vertDist != 0 && horzDist != 0) {
            System.err.println("Invalid move!");
            return;
        }

        Direction dir = getMoveDirection(selectedBlockRow, selectedBlockCol, row, col);
        int dist = Math.abs(vertDist + horzDist);

        if (!board.moveBlock(selectedBlockRow, selectedBlockCol, dir, dist)) {
            System.err.println("Invalid move!");
        } else {
            selectedBlock = null;
            updateUI();
        }
        
        if(board.hasWon())
            gameWonAction();
    }

    /**
     * Determines the direction of a move based on the starting location and the
     * destination.
     *
     * @return <code>null</code> if both the horizontal distance and the
     * vertical distance are not zero.
     */
    private Direction getMoveDirection(int startRow, int startCol, int destRow, int destCol) {
        int vertDist = destRow - startRow;
        int horzDist = destCol - startCol;
        if (vertDist < 0) {
            return Direction.UP;
        }
        if (vertDist > 0) {
            return Direction.DOWN;
        }
        if (horzDist < 0) {
            return Direction.LEFT;
        }
        if (horzDist > 0) {
            return Direction.RIGHT;
        }
        return null;
    }

    /**
     * Loads a saved game from a file.
     *
     * @param savedGame Filename of saved game to be loaded. The rest of the
     * pathname is appended inside the function.  Only changes 'board' if successful.
     * @return True if game loaded successfully.
     */
    private boolean loadSavedGame(String savedGame) {
        //Open file
        BufferedReader reader;
        try {
            InputStreamReader stream = new InputStreamReader(
                    getClass().getResourceAsStream("/puzzles/" + savedGame));
            reader = new BufferedReader(stream);
        } catch (Exception ex) {
            System.out.print(ex);
            System.out.print(ex.getMessage());
            return false;
        }

        //Read saved game state into temporary game.  Commit to "board" later.
        String fileLine;
        String[] fileLineWords;
        int totalRows;
        int totalCols;
        char cellMarker;
        GameBoard tempBoard;
        try {
            //Get size of puzzle
            fileLine = reader.readLine();
            fileLineWords = fileLine.split(" ");
            totalRows = Integer.parseInt(fileLineWords[0]);
            totalCols = Integer.parseInt(fileLineWords[1]);

            //Check puzzle size
            if (totalRows < 1 || totalCols < 1) {
                throw new IOException("Puzzle must have at least 1 row and 1 column.");
            }

            /*Read in puzzle.  Check puzzle validity at same time. Puzzle may only
            have one target block and no horizontal blocks to the right of the
            target block.
            */
            boolean targetExists = false;
            boolean targetEarlierOnRow;
            tempBoard = new GameBoard(totalRows, totalCols);
            for (int i = 0; i < totalRows; i++) {
                fileLine = reader.readLine();
                //Security Check: Make sure the length of the line read in equals totalCols.
                if(fileLine.length()!=totalCols)
                    throw new IOException("Puzzle format not correct.");
                targetEarlierOnRow = false;
                for (int j = 0; j < totalCols; j++) {
                    cellMarker = fileLine.charAt(j);
                    switch (cellMarker) {
                        case 'H':
                        case 'h':
                            if(targetEarlierOnRow)
                                throw new IOException("Horizontal block between "
                                        + "target and goal.");
                            tempBoard.placeBlockAt(new HorizontalBlock(), i, j);
                            break;
                        case 'V':
                        case 'v':
                            tempBoard.placeBlockAt(new VerticalBlock(), i, j);
                            break;
                        case 'T':
                        case 't':
                            if(targetExists)
                                throw new IOException("Multiple target blocks in file.");
                            tempBoard.placeBlockAt(new TargetBlock(), 2, 2);
                            targetExists = true;
                            targetEarlierOnRow = true;
                            break;
                        default:
                    }
                }
            }
                        
        } catch (Exception ex) {
            System.out.print(ex);
            System.out.print(ex.getMessage());
            return false;
        }

        //Commit new game board and update display.
        board = tempBoard;
        return true;
    }
    
    private void newGame() {
        //Load saved game or default game
        if (!loadSavedGame("puzzle-001.txt")) {
            // Default game if file loading fails
            board = new GameBoard(5, 5);
            board.placeBlockAt(new HorizontalBlock(), 0, 0);
            board.placeBlockAt(new HorizontalBlock(), 4, 4);
            board.placeBlockAt(new VerticalBlock(), 1, 3);
            board.placeBlockAt(new VerticalBlock(), 3, 1);
            board.placeBlockAt(new TargetBlock(), 2, 2);
        }
    }

    private void gameWonAction() {
        JOptionPane.showMessageDialog(SimpleGUI.this, "Congratulations! You Won!");
    }
}
