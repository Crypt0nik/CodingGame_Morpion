package server;

import common.GameMessage;
import common.TicTacToeGame;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Serveur de jeu de Morpion gérant deux clients simultanément
 */
public class TicTacToeServer {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private TicTacToeGame game;
    private ClientHandler player1;
    private ClientHandler player2;
    private boolean gameStarted = false;
    private final Object gameLock = new Object();
    
    public TicTacToeServer() {
        game = new TicTacToeGame();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur démarré sur le port " + PORT);
            System.out.println("En attente de connexions...");
            
            while (true) {
                try {
                    // Attendre deux connexions
                    waitForPlayers();
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'acceptation des connexions: " + e.getMessage());
                    // Réinitialiser les joueurs en cas d'erreur
                    resetPlayers();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
    }
    
    private void waitForPlayers() throws IOException {
        // Attendre la connexion du premier joueur
        if (player1 == null || !player1.isConnected()) {
            System.out.println("En attente du joueur 1...");
            Socket client1 = serverSocket.accept();
            player1 = new ClientHandler(client1, 'X', this);
            new Thread(player1).start();
            System.out.println("Joueur 1 (X) connecté");
            
            // Informer le joueur qu'il attend un adversaire
            player1.sendMessage(new GameMessage(GameMessage.MessageType.WAITING_PLAYER, 
                    "En attente d'un adversaire..."));
        }
        
        // Attendre la connexion du second joueur
        if (player2 == null || !player2.isConnected()) {
            System.out.println("En attente du joueur 2...");
            Socket client2 = serverSocket.accept();
            player2 = new ClientHandler(client2, 'O', this);
            new Thread(player2).start();
            System.out.println("Joueur 2 (O) connecté");
            
            // Démarrer la partie
            startGame();
        }
    }
    
    private void resetPlayers() {
        synchronized (gameLock) {
            if (player1 != null) {
                player1.disconnect();
                player1 = null;
            }
            if (player2 != null) {
                player2.disconnect();
                player2 = null;
            }
            gameStarted = false;
        }
    }
    
    private void startGame() {
        synchronized (gameLock) {
            if (player1 != null && player2 != null && player1.isConnected() && player2.isConnected()) {
                gameStarted = true;
                game.reset();
                
                System.out.println("Démarrage de la partie...");
                
                // Informer les joueurs du début de partie
                GameMessage startMessage = new GameMessage(GameMessage.MessageType.GAME_START);
                startMessage.setBoard(game.getBoard());
                
                try {
                    player1.sendMessage(startMessage);
                    player2.sendMessage(startMessage);
                    
                    // Informer le joueur X que c'est son tour
                    player1.sendMessage(new GameMessage(GameMessage.MessageType.YOUR_TURN, "C'est votre tour !"));
                    player2.sendMessage(new GameMessage(GameMessage.MessageType.OPPONENT_TURN, "Tour de l'adversaire"));
                    
                    System.out.println("Partie démarrée avec succès !");
                } catch (Exception e) {
                    System.err.println("Erreur lors du démarrage de la partie: " + e.getMessage());
                    resetPlayers();
                }
            } else {
                System.err.println("Impossible de démarrer la partie - joueurs non connectés");
            }
        }
    }
    
    public void handleMove(int row, int col, char player, ClientHandler sender) {
        synchronized (gameLock) {
            if (!gameStarted) {
                sender.sendMessage(new GameMessage(GameMessage.MessageType.INVALID_MOVE, 
                        "La partie n'a pas encore commencé"));
                return;
            }
            
            // Vérifier que c'est bien le tour du joueur
            if (player != game.getCurrentPlayer()) {
                sender.sendMessage(new GameMessage(GameMessage.MessageType.INVALID_MOVE, 
                        "Ce n'est pas votre tour"));
                return;
            }
            
            // Tenter de faire le coup
            if (game.makeMove(row, col, player)) {
                // Coup valide, informer les deux joueurs
                GameMessage updateMessage = new GameMessage(GameMessage.MessageType.GAME_UPDATE);
                updateMessage.setBoard(game.getBoard());
                updateMessage.setRow(row);
                updateMessage.setCol(col);
                updateMessage.setPlayerSymbol(player);
                
                player1.sendMessage(updateMessage);
                player2.sendMessage(updateMessage);
                
                // Vérifier si la partie est terminée
                if (game.isGameWon()) {
                    GameMessage gameOverMessage = new GameMessage(GameMessage.MessageType.GAME_OVER);
                    gameOverMessage.setGameWon(true);
                    gameOverMessage.setWinner(game.getWinner());
                    
                    String resultMessage;
                    if (game.getWinner() == 'D') {
                        resultMessage = "Match nul !";
                    } else {
                        resultMessage = "Victoire du joueur " + game.getWinner() + " !";
                    }
                    gameOverMessage.setContent(resultMessage);
                    
                    player1.sendMessage(gameOverMessage);
                    player2.sendMessage(gameOverMessage);
                    
                    gameStarted = false;
                    System.out.println("Partie terminée: " + resultMessage);
                } else {
                    // Informer les joueurs du changement de tour
                    char nextPlayer = game.getCurrentPlayer();
                    if (nextPlayer == 'X') {
                        player1.sendMessage(new GameMessage(GameMessage.MessageType.YOUR_TURN, "C'est votre tour !"));
                        player2.sendMessage(new GameMessage(GameMessage.MessageType.OPPONENT_TURN, "Tour de l'adversaire"));
                    } else {
                        player2.sendMessage(new GameMessage(GameMessage.MessageType.YOUR_TURN, "C'est votre tour !"));
                        player1.sendMessage(new GameMessage(GameMessage.MessageType.OPPONENT_TURN, "Tour de l'adversaire"));
                    }
                }
            } else {
                // Coup invalide
                sender.sendMessage(new GameMessage(GameMessage.MessageType.INVALID_MOVE, 
                        "Coup invalide"));
            }
        }
    }
    
    public void playerDisconnected(ClientHandler player) {
        synchronized (gameLock) {
            System.out.println("Joueur " + player.getPlayerSymbol() + " déconnecté");
            
            if (player == player1) {
                player1 = null;
            } else if (player == player2) {
                player2 = null;
            }
            
            gameStarted = false;
            
            // Informer l'autre joueur de la déconnexion
            if (player1 != null && player1.isConnected()) {
                player1.sendMessage(new GameMessage(GameMessage.MessageType.GAME_OVER, 
                        "L'adversaire s'est déconnecté"));
            }
            if (player2 != null && player2.isConnected()) {
                player2.sendMessage(new GameMessage(GameMessage.MessageType.GAME_OVER, 
                        "L'adversaire s'est déconnecté"));
            }
        }
    }
    
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        
        // Gérer l'arrêt propre du serveur
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt du serveur...");
            server.stop();
        }));
        
        server.start();
    }
}
