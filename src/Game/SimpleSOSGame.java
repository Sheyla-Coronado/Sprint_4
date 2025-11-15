package Game;

public class SimpleSOSGame extends SOSGameBase {

    public SimpleSOSGame(int boardSize, SOSGame.Player p1, SOSGame.Player p2) {
        super(boardSize, p1, p2, "simple");
    }

    @Override
    public int checkForNewSOS() {
        int newLines = countNewSOS(currentPlayer);
        if (newLines > 0) {
            winner = currentPlayer.getName();
        }
        return newLines;
    }

    @Override
    public void checkGameStatus() {
        // Simple mode ends immediately when SOS is made
    }

    @Override
    public String getWinner() { return winner; }

    @Override
    public boolean isDraw() { return false; }

    @Override
    public int getBlueScore() {
        return winner != null && winner.equals("Blue") ? 1 : 0;
    }

    @Override
    public int getRedScore() {
        return winner != null && winner.equals("Red") ? 1 : 0;
    }
}
