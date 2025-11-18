package Game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SOS_GUI extends JFrame {
    private SOSGameBase gameLogic;
    private JPanel setupPanel;
    private JPanel gamePanel;
    private JButton[][] boardButtons;
    private BoardPanel boardPanel;
    private JLabel currentTurnLabel;
    private JComboBox<Integer> boardSizeCombo;
    private JComboBox<String> gameModeCombo;
    private JButton startGameButton;
    private JLabel blueScoreLabel;
    private JLabel redScoreLabel;

    // Added for setup screen
    private JRadioButton blueHumanButton;
    private JRadioButton blueComputerButton;
    private JRadioButton redHumanButton;
    private JRadioButton redComputerButton;

    private char selectedLetter = ' ';

    public SOS_GUI() {
        setTitle("SOS Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        showSetupScreen();
    }

    private void showSetupScreen() {
        setupPanel = createSetupPanel();
        setContentPane(setupPanel);
        setVisible(true);
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("SOS Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        addBoardSizeSelector(panel, gbc);
        addGameModeSelector(panel, gbc);
        addPlayerTypeSelectors(panel, gbc); // <<< new
        addStartButton(panel, gbc);

        return panel;
    }

    private void addBoardSizeSelector(JPanel panel, GridBagConstraints gbc) {
        JLabel boardSizeLabel = new JLabel("Choose Board Size:");
        boardSizeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(boardSizeLabel, gbc);

        Integer[] sizes = {3,4,5,6,7,8};
        boardSizeCombo = new JComboBox<>(sizes);
        boardSizeCombo.setSelectedIndex(3);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(boardSizeCombo, gbc);
    }

    private void addGameModeSelector(JPanel panel, GridBagConstraints gbc) {
        JLabel gameModeLabel = new JLabel("Choose Game Mode:");
        gameModeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(gameModeLabel, gbc);

        String[] modes = {"Simple", "General"};
        gameModeCombo = new JComboBox<>(modes);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(gameModeCombo, gbc);
    }

    // NEW: Player type selection for setup screen
    private void addPlayerTypeSelectors(JPanel panel, GridBagConstraints gbc) {
        // Blue player
        JLabel blueLabel = new JLabel("Blue player:");
        blueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(blueLabel, gbc);

        blueHumanButton = new JRadioButton("Human", true);
        blueComputerButton = new JRadioButton("Computer");
        ButtonGroup blueGroup = new ButtonGroup();
        blueGroup.add(blueHumanButton);
        blueGroup.add(blueComputerButton);

        JPanel bluePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bluePanel.add(blueHumanButton);
        bluePanel.add(blueComputerButton);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(bluePanel, gbc);

        // Red player
        JLabel redLabel = new JLabel("Red player:");
        redLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(redLabel, gbc);

        redHumanButton = new JRadioButton("Human", true);
        redComputerButton = new JRadioButton("Computer");
        ButtonGroup redGroup = new ButtonGroup();
        redGroup.add(redHumanButton);
        redGroup.add(redComputerButton);

        JPanel redPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        redPanel.add(redHumanButton);
        redPanel.add(redComputerButton);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(redPanel, gbc);
    }

    private void addStartButton(JPanel panel, GridBagConstraints gbc) {
        startGameButton = new JButton("Start Game");
        startGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        startGameButton.setPreferredSize(new Dimension(150, 40));
        startGameButton.addActionListener(e -> {
            int size = (Integer) boardSizeCombo.getSelectedItem();
            String mode = ((String) gameModeCombo.getSelectedItem()).toLowerCase();
            startGame(size, mode);
        });
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(startGameButton, gbc);
    }

    private void startGame(int boardSize, String gameMode) {
        SOSGame.Player player1 = blueComputerButton.isSelected()
                ? new SOSGame.ComputerPlayer("Blue")
                : new SOSGame.HumanPlayer("Blue");

        SOSGame.Player player2 = redComputerButton.isSelected()
                ? new SOSGame.ComputerPlayer("Red")
                : new SOSGame.HumanPlayer("Red");

        if (gameMode.equals("simple")) {
            gameLogic = new SimpleSOSGame(boardSize, player1, player2);
        } else {
            gameLogic = new GeneralSOSGame(boardSize, player1, player2);
        }

        showGameScreen();

        // If first player is computer, move automatically
        if (gameLogic.getCurrentPlayer() instanceof SOSGame.ComputerPlayer) {
            makeComputerMove();
        }
    }
    
    private void makeComputerMove() {
        setButtonsEnabled(false);

        Timer timer = new Timer(500, e -> {
            SOSGame.ComputerPlayer computer = (SOSGame.ComputerPlayer) gameLogic.getCurrentPlayer();
            SOSGame.ComputerPlayer.Move move = computer.computeBestMove(gameLogic);

            if (move != null) {
                gameLogic.getBoard()[move.row][move.col] = move.letter;
                boardButtons[move.row][move.col].setText(String.valueOf(move.letter));
                boardButtons[move.row][move.col].setForeground(
                        computer.getName().equals("Blue") ? Color.BLUE : Color.RED);
                boardButtons[move.row][move.col].setEnabled(false);

                int newSOS = gameLogic.checkForNewSOS();
                updateScores();
                boardPanel.repaint();

                // Check if game ended
                if (checkEndGame()) return;  // stop immediately if winner/draw

                // In general mode, if SOS was formed, computer goes again
                if (gameLogic.getGameMode().equals("general") && newSOS > 0) {
                    currentTurnLabel.setText("Current turn: " + computer.getName() + " (again!)");
                    makeComputerMove(); // recursively move again
                } else {
                    gameLogic.switchPlayer();
                    currentTurnLabel.setText("Current turn: " + gameLogic.getCurrentPlayer().getName());

                    if (gameLogic.getCurrentPlayer() instanceof SOSGame.ComputerPlayer) {
                        makeComputerMove(); // next computer turn
                    } else {
                        setButtonsEnabled(true);
                    }
                }
            } else {
                setButtonsEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[i].length; j++) {
                if (gameLogic.getBoard()[i][j] == ' ') {
                    boardButtons[i][j].setEnabled(enabled);
                }
            }
        }
    }
    
    private void showGameScreen() {
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(new Color(240, 240, 240));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gamePanel.add(createTopPanel(), BorderLayout.NORTH);
        gamePanel.add(createCenterPanel(), BorderLayout.CENTER);
        gamePanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(gamePanel);
        revalidate();
        repaint();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        boardPanel = new BoardPanel(gameLogic.getBoardSize());
        center.add(boardPanel, BorderLayout.CENTER);

        JPanel left = createPlayerPanel("Blue");
        JPanel right = createPlayerPanel("Red");

        center.add(left, BorderLayout.WEST);
        center.add(right, BorderLayout.EAST);

        return center;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));

        JPanel leftSpacer = new JPanel();
        leftSpacer.setBackground(new Color(240, 240, 240));
        leftSpacer.setPreferredSize(new Dimension(120, 0));
        bottomPanel.add(leftSpacer, BorderLayout.WEST);

        currentTurnLabel = new JLabel("Current turn: " + gameLogic.getCurrentPlayer().getName());
        currentTurnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentTurnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(currentTurnLabel, BorderLayout.CENTER);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 12));
        newGameButton.addActionListener(e -> {
            setContentPane(setupPanel);
            revalidate();
            repaint();
        });
        bottomPanel.add(newGameButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private JPanel createPlayerPanel(String color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setPreferredSize(new Dimension(100, 0));

        JLabel playerLabel = new JLabel(color + " player");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(playerLabel);
        panel.add(Box.createVerticalStrut(10));

        JRadioButton sButton = new JRadioButton("S");
        sButton.setForeground(color.equals("Blue") ? Color.BLUE : Color.RED);
        sButton.setFont(new Font("Arial", Font.BOLD, 12));
        JRadioButton oButton = new JRadioButton("O");
        oButton.setForeground(color.equals("Blue") ? Color.BLUE : Color.RED);
        oButton.setFont(new Font("Arial", Font.BOLD, 12));

        ButtonGroup group = new ButtonGroup();
        group.add(sButton);
        group.add(oButton);
        panel.add(sButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(oButton);
        panel.add(Box.createVerticalStrut(20));

        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 12));
        if (gameLogic.getGameMode().equals("general")) {
            panel.add(scoreLabel);
            if (color.equals("Blue")) blueScoreLabel = scoreLabel;
            else redScoreLabel = scoreLabel;
        }

        sButton.addActionListener(e -> handleLetterSelection(color, 'S', group));
        oButton.addActionListener(e -> handleLetterSelection(color, 'O', group));

        return panel;
    }

    private void handleLetterSelection(String color, char letter, ButtonGroup group) {
        if (gameLogic.getCurrentPlayer().getName().equals(color)) selectLetter(letter);
        else {
            JOptionPane.showMessageDialog(this, "It's " + (color.equals("Blue") ? "Red" : "Blue") + "'s turn!");
            group.clearSelection();
        }
    }


    private class BoardPanel extends JPanel {
        private int boardSize;
        private JLayeredPane layeredPane;
        private JPanel buttonGrid;

        public BoardPanel(int boardSize) {
            this.boardSize = boardSize;
            setLayout(new BorderLayout());
            setBackground(new Color(200, 200, 200));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(400, 400));

            // Button grid layer
            buttonGrid = new JPanel(new GridLayout(boardSize, boardSize, 3, 3));
            buttonGrid.setBackground(new Color(200, 200, 200));

            boardButtons = new JButton[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    JButton btn = new JButton();
                    btn.setFont(new Font("Arial", Font.BOLD, 24));
                    btn.setBackground(Color.WHITE);
                    btn.setFocusPainted(false);
                    final int row = i;
                    final int col = j;
                    btn.addActionListener(e -> handleCellClick(row, col));
                    boardButtons[i][j] = btn;
                    buttonGrid.add(btn);
                }
            }

            add(layeredPane, BorderLayout.CENTER);
        }

        @Override
        public void doLayout() {
            super.doLayout();
            if (layeredPane != null && buttonGrid != null) {
                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                
                layeredPane.setBounds(insets.left, insets.top, w, h);
                layeredPane.removeAll();
                
                buttonGrid.setBounds(0, 0, w, h);
                layeredPane.add(buttonGrid, JLayeredPane.DEFAULT_LAYER);
                
                buttonGrid.revalidate();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(4));

            List<SOSGame.SOSLine> lines = gameLogic.getSOSLines();
            for (SOSGame.SOSLine line : lines) {
                if (line.player.equals("Blue")) {
                    g2d.setColor(new Color(0, 100, 255));
                } else {
                    g2d.setColor(new Color(255, 0, 0));
                }

                JButton startBtn = boardButtons[line.startRow][line.startCol];
                JButton endBtn = boardButtons[line.endRow][line.endCol];

                Point startLoc = SwingUtilities.convertPoint(startBtn.getParent(), startBtn.getX(), startBtn.getY(), this);
                Point endLoc = SwingUtilities.convertPoint(endBtn.getParent(), endBtn.getX(), endBtn.getY(), this);

                int startX = startLoc.x + startBtn.getWidth() / 2;
                int startY = startLoc.y + startBtn.getHeight() / 2;
                int endX = endLoc.x + endBtn.getWidth() / 2;
                int endY = endLoc.y + endBtn.getHeight() / 2;

                g2d.drawLine(startX, startY, endX, endY);
            }
        }

        public void updateLines() {
            repaint();
        }
    }

    private void selectLetter(char letter) {
        selectedLetter = letter;
    }

    private void handleCellClick(int row, int col) {
        if (!validateMove(row, col)) return;

        updateBoard(row, col);
        int newSOS = gameLogic.checkForNewSOS();
        updateScores();
        boardPanel.updateLines();

        // Check if game ended
        if (checkEndGame()) return;

        // In General mode, if SOS was formed, same player goes again
        if (gameLogic.getGameMode().equals("general") && newSOS > 0) {
            currentTurnLabel.setText("Current turn: " + gameLogic.getCurrentPlayer().getName() + " (again!)");
        } else {
            gameLogic.switchPlayer();
            currentTurnLabel.setText("Current turn: " + gameLogic.getCurrentPlayer().getName());
        }

        selectedLetter = ' ';
        disableLetterButtons();

        // Immediately let computer move if it’s now the computer's turn
        if (gameLogic.getCurrentPlayer() instanceof SOSGame.ComputerPlayer) {
            makeComputerMove();
        }
    }

    private void disableLetterButtons() {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel)c).getComponents()) {
                    if (inner instanceof JRadioButton) {
                        ((JRadioButton)inner).setSelected(false);
                    }
                }
            }
        }
    }

    private boolean validateMove(int row, int col) {
        if (selectedLetter == ' ') {
            JOptionPane.showMessageDialog(this, "Please select a letter first (S or O).");
            return false;
        }
        SOSGame.Player current = gameLogic.getCurrentPlayer();
        if (!current.makeMove(gameLogic, row, col, selectedLetter)) {
            JOptionPane.showMessageDialog(this, "Invalid move! Cell is already occupied or out of bounds.");
            return false;
        }
        return true;
    }

    private void updateBoard(int row, int col) {
        boardButtons[row][col].setText(String.valueOf(selectedLetter));
        boardButtons[row][col].setEnabled(false);
        boardButtons[row][col].setForeground(gameLogic.getCurrentPlayer().getName().equals("Blue") ? Color.BLUE : Color.RED);
    }

    private void updateScores() {
        if (blueScoreLabel != null) {
            blueScoreLabel.setText("Score: " + gameLogic.getBlueScore());
        }
        if (redScoreLabel != null) {
            redScoreLabel.setText("Score: " + gameLogic.getRedScore());
        }
    }
    
    private boolean checkEndGame() {
        gameLogic.checkGameStatus();
        if (gameLogic.getWinner() != null || gameLogic.isDraw()) {
            disableBoard();
            String message = gameLogic.getWinner() != null
                ? "Game Over! Winner: " + gameLogic.getWinner() + "\nBlue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore()
                : "Game Over! It's a draw!\nBlue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore();
            JOptionPane.showMessageDialog(this, message);
            setContentPane(setupPanel);
            revalidate();
            repaint();
            return true;
        }
        return false;
    }


    private void disableBoard() {
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[i].length; j++) {
                boardButtons[i][j].setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SOS_GUI::new);
    }
}