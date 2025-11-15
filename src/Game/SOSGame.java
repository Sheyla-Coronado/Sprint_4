package Game;

import java.util.ArrayList;
import java.util.List;

public class SOSGame {

	public static class SOSLine {
	    public final int startRow, startCol, endRow, endCol;
	    public final String player; // "Blue" or "Red"

	    public SOSLine(int startRow, int startCol, int endRow, int endCol, String player) {
	        this.startRow = startRow;
	        this.startCol = startCol;
	        this.endRow = endRow;
	        this.endCol = endCol;
	        this.player = player;
	    }
	}

    public static abstract class Player {
        protected String name;
        public Player(String name) { this.name = name; }
        public String getName() { return name; }
        public abstract boolean makeMove(SOSGameBase game, int row, int col, char letter);
    }

    public static class HumanPlayer extends Player {
        public HumanPlayer(String name) { super(name); }

        @Override
        public boolean makeMove(SOSGameBase game, int row, int col, char letter) {
            if (row < 0 || row >= game.getBoardSize() || col < 0 || col >= game.getBoardSize())
                return false;
            if (game.getBoard()[row][col] != ' ')
                return false;
            if (letter != 'S' && letter != 'O')
                return false;

            game.getBoard()[row][col] = letter;
            return true;
        }
    }
}