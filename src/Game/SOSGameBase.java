package Game;

import java.util.ArrayList;
import java.util.List;

public abstract class SOSGameBase {

    protected int boardSize;
    protected char[][] board;
    protected SOSGame.Player player1;
    protected SOSGame.Player player2;
    protected SOSGame.Player currentPlayer;

    protected String winner = null;
    protected boolean draw = false;

    protected int blueScore = 0;
    protected int redScore = 0;

    protected String gameMode;

    protected static final char EMPTY = ' ';

    protected List<SOSGame.SOSLine> sosLines = new ArrayList<>();


    public SOSGameBase(int boardSize, SOSGame.Player p1, SOSGame.Player p2, String mode) {
        this.boardSize = boardSize;
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = player1;
        this.gameMode = mode;
        this.board = new char[boardSize][boardSize];
        initializeBoard();
    }

    protected void initializeBoard() {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                board[i][j] = EMPTY;
    }

    public int getBoardSize() { return boardSize; }

    public char[][] getBoard() { return board; }

    public SOSGame.Player getCurrentPlayer() { return currentPlayer; }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public List<SOSGame.SOSLine> getSOSLines() { return sosLines; }

    public String getGameMode() { return gameMode; }

    public abstract int checkForNewSOS();

    public abstract void checkGameStatus();

    public abstract String getWinner();

    public abstract boolean isDraw();

    public abstract int getBlueScore();

    public abstract int getRedScore();


    // Count new SOS patterns
    protected int countNewSOS(SOSGame.Player player) {
        int count = 0;

        int[][] dirs = {
            {0, 1},   // horizontal
            {1, 0},   // vertical
            {1, 1},   // diagonal down-right
            {1, -1}   // diagonal down-left
        };

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                for (int[] d : dirs) {
                    int r2 = r + d[0];
                    int r3 = r + 2 * d[0];
                    int c2 = c + d[1];
                    int c3 = c + 2 * d[1];

                    if (inBounds(r3, c3)) {
                        if (board[r][c] == 'S' && board[r2][c2] == 'O' && board[r3][c3] == 'S') {
                            // Check if line already exists
                            boolean exists = false;
                            for (SOSGame.SOSLine line : sosLines) {
                                if ((line.startRow == r && line.startCol == c &&
                                     line.endRow == r3 && line.endCol == c3) ||
                                    (line.startRow == r3 && line.startCol == c3 &&
                                     line.endRow == r && line.endCol == c)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                sosLines.add(new SOSGame.SOSLine(r, c, r3, c3, player.getName()));
                                count++;
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < boardSize && c >= 0 && c < boardSize;
    }
}
