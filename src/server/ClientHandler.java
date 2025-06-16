package server;

import common.GameMessage;
import java.io.*;
import java.net.Socket;

/**
 * Gestionnaire de client côté serveur (un thread par client)
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private char playerSymbol;
    private TicTacToeServer server;
    private boolean connected;
    
    public ClientHandler(Socket clientSocket, char playerSymbol, TicTacToeServer server) {
        this.clientSocket = clientSocket;
        this.playerSymbol = playerSymbol;
        this.server = server;
        this.connected = true;
        
        try {
            // Important: créer l'ObjectOutputStream avant l'ObjectInputStream
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation des streams: " + e.getMessage());
            connected = false;
        }
    }
    
    @Override
    public void run() {
        try {
            // Petit délai pour s'assurer que le client est prêt
            Thread.sleep(100);
            
            // Envoyer au client son symbole
            GameMessage playerConnectedMessage = new GameMessage(GameMessage.MessageType.PLAYER_CONNECTED, 
                    "Vous êtes le joueur " + playerSymbol);
            playerConnectedMessage.setPlayerSymbol(playerSymbol);
            sendMessage(playerConnectedMessage);
            
            System.out.println("Joueur " + playerSymbol + " initialisé");
            
            // Écouter les messages du client
            while (connected && !clientSocket.isClosed()) {
                try {
                    GameMessage message = (GameMessage) input.readObject();
                    
                    if (message.getType() == GameMessage.MessageType.PLAYER_MOVE) {
                        server.handleMove(message.getRow(), message.getCol(), 
                                playerSymbol, this);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Erreur de désérialisation: " + e.getMessage());
                } catch (EOFException e) {
                    // Client déconnecté proprement
                    System.out.println("Client " + playerSymbol + " déconnecté proprement");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur de communication avec le client " + playerSymbol + ": " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Thread interrompu pour le client " + playerSymbol);
        } finally {
            disconnect();
        }
    }
    
    public void sendMessage(GameMessage message) {
        if (connected && output != null && !clientSocket.isClosed()) {
            try {
                synchronized (output) {
                    output.writeObject(message);
                    output.flush();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi du message au client " + playerSymbol + ": " + e.getMessage());
                disconnect();
            }
        }
    }
    
    public void disconnect() {
        if (connected) {
            connected = false;
            
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
            }
            
            server.playerDisconnected(this);
        }
    }
    
    public boolean isConnected() {
        return connected && !clientSocket.isClosed();
    }
    
    public char getPlayerSymbol() {
        return playerSymbol;
    }
}
