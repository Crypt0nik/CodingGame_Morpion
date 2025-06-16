package common;

/**
 * Classe contenant la logique du jeu de Morpion
 */
public class TicTacToeGame {
    private char[][] board;
    private char currentPlayer;
    private boolean gameWon;
    private char winner;
    private int moveCount;
    
    public TicTacToeGame() {
        board = new char[3][3];
        initializeBoard();
        currentPlayer = 'X'; // X commence toujours
        gameWon = false;
        winner = ' ';
        moveCount = 0;
    }
    
    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }
    
    public boolean makeMove(int row, int col, char player) {
        // Vérifier si le coup est valide
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            return false;
        }
        
        if (board[row][col] != ' ') {
            return false;
        }
        
        if (player != currentPlayer) {
            return false;
        }
        
        if (gameWon) {
            return false;
        }
        
        // Effectuer le coup
        board[row][col] = player;
        moveCount++;
        
        // Vérifier la victoire
        checkWin();
        
        // Changer de joueur
        if (!gameWon) {
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
        
        return true;
    }
    
    private void checkWin() {
        // Vérifier les lignes
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                gameWon = true;
                winner = board[i][0];
                return;
            }
        }
        
        // Vérifier les colonnes
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != ' ' && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                gameWon = true;
                winner = board[0][j];
                return;
            }
        }
        
        // Vérifier les diagonales
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            gameWon = true;
            winner = board[0][0];
            return;
        }
        
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            gameWon = true;
            winner = board[0][2];
            return;
        }
        
        // Vérifier le match nul
        if (moveCount == 9 && !gameWon) {
            gameWon = true;
            winner = 'D'; // Draw (match nul)
        }
    }
    
    // Getters
    public char[][] getBoard() {
        // Retourner une copie pour éviter les modifications externes
        char[][] copy = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }
    
    public char getCurrentPlayer() { return currentPlayer; }
    public boolean isGameWon() { return gameWon; }
    public char getWinner() { return winner; }
    public int getMoveCount() { return moveCount; }
    
    public void reset() {
        initializeBoard();
        currentPlayer = 'X';
        gameWon = false;
        winner = ' ';
        moveCount = 0;
    }
}
