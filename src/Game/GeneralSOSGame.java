package Game;

public class GeneralSOSGame extends SOSGameBase {

    public GeneralSOSGame(int boardSize, SOSGame.Player p1, SOSGame.Player p2) {
        super(boardSize, p1, p2, "general");
    }

    @Override
    public int checkForNewSOS() {
        int newLines = countNewSOS(currentPlayer);

        if (currentPlayer.getName().equals("Blue")) {
            blueScore += newLines;
        } else {
            redScore += newLines;
        }

        return newLines;
    }

    @Override
    public void checkGameStatus() {
        if (isBoardFull()) {
            draw = blueScore == redScore;
            if (!draw) {
                winner = (blueScore > redScore) ? "Blue" : "Red";
            }
        }
    }

    @Override
    public String getWinner() { return winner; }

    @Override
    public boolean isDraw() { return draw; }

    @Override
    public int getBlueScore() { return blueScore; }

    @Override
    public int getRedScore() { return redScore; }

    private boolean isBoardFull() {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (board[i][j] == EMPTY) return false;
        return true;
    }
}
