package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static class ComputerPlayer extends Player {
        private static final int MAX_DEPTH = 3;
        private Random random = new Random();

        public ComputerPlayer(String name) { super(name); }

        @Override
        public boolean makeMove(SOSGameBase game, int row, int col, char letter) {

            Move bestMove = computeBestMove(game);
            if (bestMove != null) {
                game.getBoard()[bestMove.row][bestMove.col] = bestMove.letter;
                return true;
            }
            return false;
        }

        public Move computeBestMove(SOSGameBase game) {
            Move bestMove = null;
            int bestScore = Integer.MIN_VALUE;
            
            List<Move> possibleMoves = getAllPossibleMoves(game);

            java.util.Collections.shuffle(possibleMoves, random);

            for (Move move : possibleMoves) {

                char[][] board = game.getBoard();
                char original = board[move.row][move.col];
                board[move.row][move.col] = move.letter;

                int score = minimax(game, MAX_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                

                board[move.row][move.col] = original;
                
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
            
            return bestMove;
        }

        // Minimax algol w/ alpha-beta pruning
        private int minimax(SOSGameBase game, int depth, int alpha, int beta, boolean isMaximizing) {
 
            if (depth == 0 || isGameOver(game)) {
                return evaluatePosition(game);
            }

            List<Move> moves = getAllPossibleMoves(game);
            if (moves.isEmpty()) {
                return evaluatePosition(game);
            }

            if (isMaximizing) {
                int maxEval = Integer.MIN_VALUE;
                for (Move move : moves) {
                    char[][] board = game.getBoard();
                    char original = board[move.row][move.col];
                    board[move.row][move.col] = move.letter;
                    
                    int eval = minimax(game, depth - 1, alpha, beta, false);
                    
                    board[move.row][move.col] = original;
                    
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
                return maxEval;
            } else {
                int minEval = Integer.MAX_VALUE;
                for (Move move : moves) {
                    char[][] board = game.getBoard();
                    char original = board[move.row][move.col];
                    board[move.row][move.col] = move.letter;
                    
                    int eval = minimax(game, depth - 1, alpha, beta, true);
                    
                    board[move.row][move.col] = original;
                    
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break; 
                }
                return minEval;
            }
        }

        private int evaluatePosition(SOSGameBase game) {
            int score = 0;
            char[][] board = game.getBoard();
            int size = game.getBoardSize();
            

            int computerPotential = 0;
            int opponentPotential = 0;
            

            int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
            
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    for (int[] d : dirs) {
                        int r2 = r + d[0];
                        int r3 = r + 2 * d[0];
                        int c2 = c + d[1];
                        int c3 = c + 2 * d[1];
                        
                        if (inBounds(r3, c3, size)) {

                            if (board[r][c] == 'S' && board[r2][c2] == 'O' && board[r3][c3] == 'S') {
                                computerPotential += 100;
                            }

                            else if (board[r][c] == 'S' && board[r3][c3] == 'S' && board[r2][c2] == ' ') {
                                computerPotential += 10; 
                            }
                            else if (board[r][c] == 'S' && board[r2][c2] == 'O' && board[r3][c3] == ' ') {
                                computerPotential += 15;
                            }
                            else if (board[r][c] == ' ' && board[r2][c2] == 'O' && board[r3][c3] == 'S') {
                                computerPotential += 15;
                            }
                        }
                    }
                }
            }
            

            int centerBonus = 0;
            int centerR = size / 2;
            int centerC = size / 2;
            if (board[centerR][centerC] != ' ') {
                centerBonus = 5;
            }
            
            score = computerPotential + centerBonus;
            
            return score;
        }

        private boolean inBounds(int r, int c, int size) {
            return r >= 0 && r < size && c >= 0 && c < size;
        }

        private boolean isGameOver(SOSGameBase game) {
            char[][] board = game.getBoard();
            for (int i = 0; i < game.getBoardSize(); i++) {
                for (int j = 0; j < game.getBoardSize(); j++) {
                    if (board[i][j] == ' ') return false;
                }
            }
            return true;
        }

        private List<Move> getAllPossibleMoves(SOSGameBase game) {
            List<Move> moves = new ArrayList<>();
            char[][] board = game.getBoard();
            int size = game.getBoardSize();
            
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (board[r][c] == ' ') {
                        moves.add(new Move(r, c, 'S'));
                        moves.add(new Move(r, c, 'O'));
                    }
                }
            }
            
            return moves;
        }


        public static class Move {
            public final int row;
            public final int col;
            public final char letter;
            
            public Move(int row, int col, char letter) {
                this.row = row;
                this.col = col;
                this.letter = letter;
            }
        }
    }
}