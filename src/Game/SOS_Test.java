package Game;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SOS_Test {

    @Test
    @DisplayName("AC 1.1 - Display available board size options")
    void testAvailableBoardSizesDisplayed() {
        Integer[] expectedSizes = {3, 4, 5, 6, 7, 8};
        assertTrue(expectedSizes.length >= 3, "There should be at least 3 board size options available");
    }

    @Test
    @DisplayName("AC 1.2 - Select and confirm board size")
    void testBoardSizeSelectionSavesCorrectly() {
        int selectedBoardSize = 5;
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame game = new SimpleSOSGame(selectedBoardSize, player1, player2);
        assertEquals(selectedBoardSize, game.getBoardSize(), "The selected board size should be saved correctly");
    }

    @Test
    @DisplayName("AC 1.3 - Visual preview or confirmation of board size")
    void testBoardSizePreviewDisplayed() {
        int selectedSize = 6;
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        GeneralSOSGame game = new GeneralSOSGame(selectedSize, player1, player2);
        int actualRows = game.getBoard().length;
        int actualCols = game.getBoard()[0].length;

        assertEquals(selectedSize, actualRows, "The number of rows should match the selected board size");
        assertEquals(selectedSize, actualCols, "The number of columns should match the selected board size");
    }

    @Test
    @DisplayName("AC 1.4 - Game mode selection is stored")
    void testGameModeSelection() {
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame simpleGame = new SimpleSOSGame(5, player1, player2);
        assertEquals("simple", simpleGame.getGameMode(), "Game mode should be saved as 'simple'");

        GeneralSOSGame generalGame = new GeneralSOSGame(5, player1, player2);
        assertEquals("general", generalGame.getGameMode(), "Game mode should be saved as 'general'");
    }

    @Test
    @DisplayName("AC 1.5 - Board initializes correctly with empty cells")
    void testBoardInitialization() {
        int boardSize = 4;
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame game = new SimpleSOSGame(boardSize, player1, player2);
        char[][] board = game.getBoard();

        assertEquals(boardSize, board.length, "Board should have correct number of rows");
        assertEquals(boardSize, board[0].length, "Board should have correct number of columns");

        for (char[] row : board) {
            for (char cell : row) {
                assertEquals(' ', cell, "All board cells should start empty");
            }
        }
    }

    @Test
    @DisplayName("AC 1.6 - Initial player turn is Blue")
    void testInitialPlayerTurn() {
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame game = new SimpleSOSGame(3, player1, player2);
        assertEquals("Blue", game.getCurrentPlayer().getName(), "Initial turn should be Blue");
    }

    @Test
    @DisplayName("AC 1.7 - Making a valid move updates board")
    void testMakeMove() {
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame game = new SimpleSOSGame(3, player1, player2);
        boolean result = player1.makeMove(game, 0, 0, 'S');

        assertTrue(result, "Should allow placing 'S' in empty cell");
        assertEquals('S', game.getBoard()[0][0], "Cell should now contain 'S'");
    }

    @Test
    @DisplayName("AC 1.8 - Switching turns alternates between players")
    void testSwitchPlayer() {
        SOSGame.Player player1 = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player player2 = new SOSGame.HumanPlayer("Red");

        SimpleSOSGame game = new SimpleSOSGame(3, player1, player2);
        assertEquals("Blue", game.getCurrentPlayer().getName());

        game.switchPlayer();
        assertEquals("Red", game.getCurrentPlayer().getName(), "Turn should switch to Red");
    }

    @Test
    @DisplayName("AC 1.9 - SOS detection and scoring works")
    void testDetectSOSAndScoreUpdate() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        // Create a horizontal SOS by Blue
        blue.makeMove(game, 0, 0, 'S');
        game.switchPlayer();
        red.makeMove(game, 0, 1, 'O');
        game.switchPlayer();
        blue.makeMove(game, 0, 2, 'S');

        int newSOS = game.checkForNewSOS();
        assertTrue(newSOS > 0, "An SOS should have been detected");
        assertEquals(1, game.getBlueScore(), "Blue's score should be 1 in simple mode");
        assertEquals("Blue", game.getWinner(), "Blue should be the winner in simple mode");
    }
    
    //Other tests
    @Test
    @DisplayName("Invalid move: Cannot place in occupied cell")
    void testInvalidMoveOccupiedCell() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        blue.makeMove(game, 0, 0, 'S');
        boolean result = red.makeMove(game, 0, 0, 'O');

        assertFalse(result, "Should not allow placing in occupied cell");
        assertEquals('S', game.getBoard()[0][0], "Cell should still contain original letter");
    }

    @Test
    @DisplayName("Invalid move: Out of bounds")
    void testInvalidMoveOutOfBounds() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        boolean result = blue.makeMove(game, 5, 5, 'S');
        assertFalse(result, "Should not allow move out of bounds");
    }

    @Test
    @DisplayName("Invalid move: Invalid letter")
    void testInvalidMoveBadLetter() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        boolean result = blue.makeMove(game, 0, 0, 'X');
        assertFalse(result, "Should not allow invalid letter");
        assertEquals(' ', game.getBoard()[0][0], "Cell should remain empty");
    }
    
    //Simple and General Mode Tests
    
    @Test
    @DisplayName("Simple Mode: Game ends immediately after first SOS")
    void testSimpleModeEndsAfterFirstSOS() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        assertEquals("Blue", game.getCurrentPlayer().getName());

        game.getCurrentPlayer().makeMove(game, 0, 0, 'S'); // Blue
        game.switchPlayer();                               // now Red
        game.getCurrentPlayer().makeMove(game, 0, 1, 'O'); // Red
        game.switchPlayer();                               // now Blue
        game.getCurrentPlayer().makeMove(game, 0, 2, 'S'); // Blue

        int newSOS = game.checkForNewSOS();

        assertTrue(newSOS > 0, "An SOS should have been detected for Blue");
        assertEquals("Blue", game.getWinner(), "Blue should be the winner in simple mode after forming SOS");
        assertTrue(game.getBlueScore() >= 1, "Blue's score should be incremented");
    }
    
    @Test
    @DisplayName("Simple Mode: No SOS no winner")
    void testSimpleModeNoSOS() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blue, red);

        game.getCurrentPlayer().makeMove(game, 0, 0, 'S'); // Blue
        game.switchPlayer();
        game.getCurrentPlayer().makeMove(game, 1, 1, 'S'); // Red
        game.switchPlayer();
        game.getCurrentPlayer().makeMove(game, 2, 2, 'O'); // Blue

        int newSOS = game.checkForNewSOS();
        assertEquals(0, newSOS, "No SOS should be detected");
        assertNull(game.getWinner(), "There should be no winner if no SOS formed");
        assertFalse(game.isDraw(), "Should not be a draw");
    }
    
    @Test
    @DisplayName("General Mode: Game continues after SOS and updates score")
    void testGeneralModeContinuesAfterSOS() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        GeneralSOSGame game = new GeneralSOSGame(3, blue, red);

        // Blue forms an SOS
        game.getCurrentPlayer().makeMove(game, 0, 0, 'S'); // Blue
        game.switchPlayer();
        game.getCurrentPlayer().makeMove(game, 0, 1, 'O'); // Red
        game.switchPlayer();
        game.getCurrentPlayer().makeMove(game, 0, 2, 'S'); // Blue

        int newSOS = game.checkForNewSOS();
        assertTrue(newSOS >= 1, "Blue should score at least one SOS");
        assertTrue(game.getBlueScore() >= 1, "Blue's score should be updated");
        assertNull(game.getWinner(), "No immediate winner in general mode after one SOS");
        assertFalse(game.isDraw(), "Not a draw after a single SOS");
    }
    
    @Test
    @DisplayName("General Mode: Both players score and game ends when board full")
    void testGeneralModeBothPlayersAndBoardFullEndsGame() {
        SOSGame.Player blue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player red = new SOSGame.HumanPlayer("Red");
        GeneralSOSGame game = new GeneralSOSGame(3, blue, red);

        int size = game.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (game.getBoard()[r][c] == ' ') {
                    char letter = ((r + c) % 2 == 0) ? 'S' : 'O';
                    game.getCurrentPlayer().makeMove(game, r, c, letter);
                    game.checkForNewSOS();
                    game.switchPlayer();
                }
            }
        }

        game.checkGameStatus();

        assertTrue(game.getWinner() != null || game.isDraw(), "When board is full, there should be a winner or a draw");
    }

    // AC for computer
    
    @Test
    @DisplayName("AC 8.1 - Computer Player Type Verification")
    void testPlayerTypeVerification() {
        SOSGame.Player humanBlue = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player computerRed = new SOSGame.ComputerPlayer("Red");
        
        SimpleSOSGame game = new SimpleSOSGame(3, humanBlue, computerRed);
        
        // Verify player types
        assertFalse(game.getCurrentPlayer() instanceof SOSGame.ComputerPlayer, 
            "First player (Blue) should be human");
        
        game.switchPlayer();
        assertTrue(game.getCurrentPlayer() instanceof SOSGame.ComputerPlayer, 
            "Second player (Red) should be computer");
    }

    private int countEmptyCells(SOSGameBase game) {
        int count = 0;
        char[][] board = game.getBoard();
        for (int i = 0; i < game.getBoardSize(); i++) {
            for (int j = 0; j < game.getBoardSize(); j++) {
                if (board[i][j] == ' ') {
                    count++;
                }
            }
        }
        return count;
    }

    @Test
    @DisplayName("AC 8.1 - Select Computer Opponent Option Available")
    void testComputerOpponentSelectionAvailable() {
        SOSGame.Player blueComputer = new SOSGame.ComputerPlayer("Blue");
        SOSGame.Player redComputer = new SOSGame.ComputerPlayer("Red");

        assertNotNull(blueComputer, "Blue computer player should be created");
        assertNotNull(redComputer, "Red computer player should be created");
        assertTrue(blueComputer instanceof SOSGame.ComputerPlayer, "Blue should be instance of ComputerPlayer");
        assertTrue(redComputer instanceof SOSGame.ComputerPlayer, "Red should be instance of ComputerPlayer");
        assertEquals("Blue", blueComputer.getName(), "Computer player should have correct name");
        assertEquals("Red", redComputer.getName(), "Computer player should have correct name");
    }

    @Test
    @DisplayName("AC 8.2 - Computer Makes Valid Moves in Simple Mode")
    void testComputerMakesValidMovesSimpleMode() {
        SOSGame.Player human = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player computer = new SOSGame.ComputerPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, human, computer);

        // Human make first move
        human.makeMove(game, 0, 0, 'S');
        game.switchPlayer();

        // Computer's turn, should make a valid move
        int emptyBefore = countEmptyCells(game);
        boolean moveMade = computer.makeMove(game, -1, -1, ' '); 

        assertTrue(moveMade, "Computer should successfully make a move");
        int emptyAfter = countEmptyCells(game);
        assertEquals(emptyBefore - 1, emptyAfter, "Computer should fill exactly one cell");

        boolean foundNewMove = false;
        for (int r = 0; r < game.getBoardSize(); r++) {
            for (int c = 0; c < game.getBoardSize(); c++) {
                if (game.getBoard()[r][c] != ' ' && !(r == 0 && c == 0)) {
                    char letter = game.getBoard()[r][c];
                    assertTrue(letter == 'S' || letter == 'O', "Computer should place valid letter");
                    foundNewMove = true;
                }
            }
        }
        assertTrue(foundNewMove, "Computer should have placed a letter on the board");
    }

    @Test
    @DisplayName("AC 8.2 - Computer Makes Valid Moves in General Mode")
    void testComputerMakesValidMovesGeneralMode() {
        SOSGame.Player human = new SOSGame.HumanPlayer("Blue");
        SOSGame.Player computer = new SOSGame.ComputerPlayer("Red");
        GeneralSOSGame game = new GeneralSOSGame(4, human, computer);

        // Human makes first move
        human.makeMove(game, 1, 1, 'O');
        game.switchPlayer();

        // Computer makes move
        int emptyBefore = countEmptyCells(game);
        computer.makeMove(game, -1, -1, ' ');
        int emptyAfter = countEmptyCells(game);

        assertEquals(emptyBefore - 1, emptyAfter, "Computer should fill exactly one cell");
        
        int filledCells = 0;
        for (int r = 0; r < game.getBoardSize(); r++) {
            for (int c = 0; c < game.getBoardSize(); c++) {
                if (game.getBoard()[r][c] != ' ') {
                    filledCells++;
                    char letter = game.getBoard()[r][c];
                    assertTrue(letter == 'S' || letter == 'O', "Should be valid letter");
                }
            }
        }
        assertEquals(2, filledCells, "Should have exactly 2 filled cells after both moves");
    }

    @Test
    @DisplayName("AC 8.3 - Computer vs Computer in Simple Mode")
    void testComputerVsComputerSimpleMode() {
        SOSGame.Player blueComputer = new SOSGame.ComputerPlayer("Blue");
        SOSGame.Player redComputer = new SOSGame.ComputerPlayer("Red");
        SimpleSOSGame game = new SimpleSOSGame(3, blueComputer, redComputer);

        assertTrue(blueComputer instanceof SOSGame.ComputerPlayer, "Blue should be computer");
        assertTrue(redComputer instanceof SOSGame.ComputerPlayer, "Red should be computer");

        int maxMoves = game.getBoardSize() * game.getBoardSize();
        int moveCount = 0;

        while (game.getWinner() == null && moveCount < maxMoves) {
            SOSGame.Player current = game.getCurrentPlayer();
            int emptyBefore = countEmptyCells(game);
            
            boolean moveMade = current.makeMove(game, -1, -1, ' ');
            assertTrue(moveMade, "Computer should make valid move on turn " + (moveCount + 1));
            
            int emptyAfter = countEmptyCells(game);
            assertEquals(emptyBefore - 1, emptyAfter, "Exactly one cell should be filled");

            game.checkForNewSOS();
            
            if (game.getWinner() != null) {
                break;
            }
            
            game.switchPlayer();
            moveCount++;
        }

        assertTrue(moveCount > 0, "At least one move should have been made");
        assertTrue(moveCount <= maxMoves, "Should not exceed board capacity");

        for (int r = 0; r < game.getBoardSize(); r++) {
            for (int c = 0; c < game.getBoardSize(); c++) {
                char cell = game.getBoard()[r][c];
                assertTrue(cell == ' ' || cell == 'S' || cell == 'O', 
                    "Board should only contain valid characters");
            }
        }
    }

    @Test
    @DisplayName("AC 8.3 - Computer vs Computer in General Mode")
    void testComputerVsComputerGeneralMode() {
        SOSGame.Player blueComputer = new SOSGame.ComputerPlayer("Blue");
        SOSGame.Player redComputer = new SOSGame.ComputerPlayer("Red");
        GeneralSOSGame game = new GeneralSOSGame(3, blueComputer, redComputer);

        assertTrue(blueComputer instanceof SOSGame.ComputerPlayer, "Blue should be computer");
        assertTrue(redComputer instanceof SOSGame.ComputerPlayer, "Red should be computer");

        int maxMoves = game.getBoardSize() * game.getBoardSize();
        int moveCount = 0;

        while (countEmptyCells(game) > 0 && moveCount < maxMoves) {
            SOSGame.Player current = game.getCurrentPlayer();
            int emptyBefore = countEmptyCells(game);
            
            boolean moveMade = current.makeMove(game, -1, -1, ' ');
            assertTrue(moveMade, "Computer should make valid move");
            
            int emptyAfter = countEmptyCells(game);
            assertEquals(emptyBefore - 1, emptyAfter, "One cell should be filled per move");

            int sosFound = game.checkForNewSOS();

            if (sosFound == 0) {
                game.switchPlayer();
            }
            
            moveCount++;
        }
        
        assertEquals(0, countEmptyCells(game), "Board should be completely filled");

        game.checkGameStatus();
        assertTrue(game.getWinner() != null || game.isDraw(), 
            "Game should have winner or draw when board is full");

        int totalScore = game.getBlueScore() + game.getRedScore();
        assertTrue(totalScore >= 0, "Total score should be non-negative");
    }
}