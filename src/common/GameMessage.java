package common;

import java.io.Serializable;

/**
 * Classe représentant les messages échangés entre le client et le serveur
 */
public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        PLAYER_MOVE,        // Coup d'un joueur
        GAME_UPDATE,        // Mise à jour de l'état du jeu
        GAME_START,         // Début de partie
        GAME_OVER,          // Fin de partie
        PLAYER_CONNECTED,   // Joueur connecté
        WAITING_PLAYER,     // En attente d'un autre joueur
        INVALID_MOVE,       // Coup invalide
        YOUR_TURN,          // C'est votre tour
        OPPONENT_TURN       // Tour de l'adversaire
    }
    
    private MessageType type;
    private String content;
    private int row;
    private int col;
    private char playerSymbol;
    private char[][] board;
    private boolean gameWon;
    private char winner;
    
    public GameMessage(MessageType type) {
        this.type = type;
    }
    
    public GameMessage(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }
    
    public GameMessage(MessageType type, int row, int col, char playerSymbol) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.playerSymbol = playerSymbol;
    }
    
    // Getters et setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    
    public char getPlayerSymbol() { return playerSymbol; }
    public void setPlayerSymbol(char playerSymbol) { this.playerSymbol = playerSymbol; }
    
    public char[][] getBoard() { return board; }
    public void setBoard(char[][] board) { this.board = board; }
    
    public boolean isGameWon() { return gameWon; }
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }
    
    public char getWinner() { return winner; }
    public void setWinner(char winner) { this.winner = winner; }
}
