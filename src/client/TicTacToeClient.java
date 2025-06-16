package client;

import common.GameMessage;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Client du jeu de Morpion avec interface graphique Swing moderne
 */
public class TicTacToeClient extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    
    // Couleurs modernes
    private static final Color PRIMARY_COLOR = new Color(67, 56, 202);      // Indigo
    private static final Color SECONDARY_COLOR = new Color(139, 92, 246);   // Violet
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Vert
    private static final Color DANGER_COLOR = new Color(239, 68, 68);       // Rouge
    private static final Color WARNING_COLOR = new Color(245, 158, 11);     // Orange
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Gris clair
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 41, 59);          // Gris foncé
    
    // Composants de l'interface
    private JButton[][] gameButtons;
    private JLabel statusLabel;
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JButton connectButton;
    private JButton newGameButton;
    private JButton settingsButton;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendChatButton;
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JPanel sidePanel;
    
    // Statistiques et historique
    private int wins = 0;
    private int losses = 0;
    private int draws = 0;
    private List<String> gameHistory;
    
    // Animation et effets
    private Timer animationTimer;
    private int animationStep = 0;
    
    // Communication réseau
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connected = false;
    
    // État du jeu
    private char playerSymbol;
    private boolean myTurn = false;
    private char[][] board;
    
    public TicTacToeClient() {
        gameHistory = new ArrayList<>();
        initializeGUI();
        board = new char[3][3];
        clearBoard();
        
        // Définir l'icône de l'application
        setIconImage(createGameIcon());
    }
    
    private Image createGameIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner un mini morpion comme icône
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRoundRect(0, 0, 32, 32, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        
        // Grille
        g2d.drawLine(10, 0, 10, 32);
        g2d.drawLine(22, 0, 22, 32);
        g2d.drawLine(0, 10, 32, 10);
        g2d.drawLine(0, 22, 32, 22);
        
        // X et O
        g2d.drawLine(2, 2, 8, 8);
        g2d.drawLine(8, 2, 2, 8);
        g2d.drawOval(24, 24, 6, 6);
        
        g2d.dispose();
        return icon;
    }
    
    private void initializeGUI() {
        setTitle("🎮 Morpion Multijoueur - Édition Deluxe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configuration de la fenêtre principale
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Panel principal avec layout moderne
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header avec titre et informations
        createHeaderPanel();
        
        // Panel central avec le jeu
        createGamePanel();
        
        // Panel latéral avec chat et statistiques
        createSidePanel();
        
        // Panel de contrôles en bas
        createControlPanel();
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Configuration finale
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        pack();
        setLocationRelativeTo(null);
        
        // Gérer la fermeture de la fenêtre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                disconnect();
            }
        });
        
        // Appliquer le thème moderne
        applyModernTheme();
    }
    
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Titre principal avec gradient
        JLabel titleLabel = new JLabel("🎮 MORPION MULTIJOUEUR", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        // Informations du joueur
        JPanel playerInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        playerInfoPanel.setBackground(PRIMARY_COLOR);
        
        playerLabel = new JLabel("🔗 Non connecté", JLabel.CENTER);
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        scoreLabel = new JLabel("📊 V: 0 | D: 0 | N: 0", JLabel.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        playerInfoPanel.add(playerLabel);
        playerInfoPanel.add(scoreLabel);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(playerInfoPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }
        
    private void createGamePanel() {
        JPanel gameSectionPanel = new JPanel(new BorderLayout());
        gameSectionPanel.setBackground(BACKGROUND_COLOR);
        
        // Titre de section
        JLabel gameTitleLabel = new JLabel("🎯 PLATEAU DE JEU", JLabel.CENTER);
        gameTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gameTitleLabel.setForeground(TEXT_COLOR);
        gameTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Status avec style moderne
        statusLabel = new JLabel("🔄 Cliquez sur 'Se connecter' pour commencer", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(WARNING_COLOR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Grille de jeu avec design moderne
        gamePanel = new JPanel(new GridLayout(3, 3, 8, 8));
        gamePanel.setBackground(BACKGROUND_COLOR);
        gamePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        gameButtons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j] = createModernGameButton(i, j);
                gamePanel.add(gameButtons[i][j]);
            }
        }
        
        gameSectionPanel.add(gameTitleLabel, BorderLayout.NORTH);
        gameSectionPanel.add(statusLabel, BorderLayout.CENTER);
        gameSectionPanel.add(gamePanel, BorderLayout.SOUTH);
        
        mainPanel.add(gameSectionPanel, BorderLayout.CENTER);
    }
    
    private JButton createModernGameButton(int row, int col) {
        JButton button = new JButton("");
        button.setFont(new Font("Segoe UI", Font.BOLD, 72));
        button.setPreferredSize(new Dimension(120, 120));
        button.setBackground(CARD_COLOR);
        button.setForeground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getText().isEmpty() && myTurn && connected) {
                    button.setBackground(new Color(139, 92, 246, 50));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getText().isEmpty()) {
                    button.setBackground(CARD_COLOR);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
        });
        
        button.addActionListener(e -> makeMove(row, col));
        return button;
    }
    
    private void connectToServer() {
        try {
            statusLabel.setText("Connexion au serveur...");
            connectButton.setEnabled(false);
            
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(SERVER_HOST, SERVER_PORT), 5000); // Timeout de 5 secondes
            
            // Important: créer l'ObjectOutputStream avant l'ObjectInputStream
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            
            connected = true;
            
            // Démarrer le thread d'écoute des messages du serveur
            new Thread(this::listenToServer).start();
            
            statusLabel.setText("Connecté au serveur - En attente...");
            
        } catch (IOException e) {
            connectButton.setEnabled(true);
            JOptionPane.showMessageDialog(this, 
                "Impossible de se connecter au serveur: " + e.getMessage() + 
                "\nAssurez-vous que le serveur est démarré.",
                "Erreur de connexion", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Échec de la connexion");
        }
    }
    
    private void listenToServer() {
        try {
            while (connected && socket != null && !socket.isClosed()) {
                GameMessage message = (GameMessage) input.readObject();
                SwingUtilities.invokeLater(() -> handleServerMessage(message));
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Connexion perdue avec le serveur");
                    JOptionPane.showMessageDialog(this, 
                        "Connexion perdue avec le serveur: " + e.getMessage(),
                        "Erreur de connexion", 
                        JOptionPane.ERROR_MESSAGE);
                    disconnect();
                });
            }
        }
    }
    
    private void handleServerMessage(GameMessage message) {
        switch (message.getType()) {
            case PLAYER_CONNECTED:
                playerSymbol = message.getPlayerSymbol();
                playerLabel.setText("🎮 Joueur " + playerSymbol + " connecté");
                playerLabel.setForeground(Color.WHITE);
                addChatMessage("Système", "Vous êtes le joueur " + playerSymbol, SUCCESS_COLOR);
                break;
                
            case WAITING_PLAYER:
                statusLabel.setText("⏳ " + message.getContent());
                statusLabel.setForeground(WARNING_COLOR);
                addChatMessage("Système", message.getContent(), WARNING_COLOR);
                break;
                
            case GAME_START:
                statusLabel.setText("🎯 Partie commencée ! C'est parti !");
                statusLabel.setForeground(SUCCESS_COLOR);
                newGameButton.setEnabled(true);
                updateBoard(message.getBoard());
                enableGameButtons(true);
                addChatMessage("Système", "La partie commence !", SUCCESS_COLOR);
                break;
                
            case GAME_UPDATE:
                updateBoard(message.getBoard());
                addChatMessage("Jeu", "Coup joué !", PRIMARY_COLOR);
                break;
                
            case YOUR_TURN:
                myTurn = true;
                statusLabel.setText("✨ C'est votre tour !");
                statusLabel.setForeground(SUCCESS_COLOR);
                addChatMessage("Système", "À vous de jouer !", SUCCESS_COLOR);
                break;
                
            case OPPONENT_TURN:
                myTurn = false;
                statusLabel.setText("⏰ Tour de l'adversaire");
                statusLabel.setForeground(WARNING_COLOR);
                break;
                
            case INVALID_MOVE:
                addChatMessage("Erreur", message.getContent(), DANGER_COLOR);
                JOptionPane.showMessageDialog(this, 
                    message.getContent(),
                    "Coup invalide", 
                    JOptionPane.WARNING_MESSAGE);
                break;
                
            case GAME_OVER:
                myTurn = false;
                enableGameButtons(false);
                
                if (message.isGameWon()) {
                    char winner = message.getWinner();
                    if (winner == 'D') {
                        statusLabel.setText("🤝 Match nul !");
                        statusLabel.setForeground(WARNING_COLOR);
                        addChatMessage("Fin", "Match nul !", WARNING_COLOR);
                        draws++;
                    } else if (winner == playerSymbol) {
                        statusLabel.setText("🎉 Vous avez gagné !");
                        statusLabel.setForeground(SUCCESS_COLOR);
                        addChatMessage("Victoire", "Félicitations ! Vous avez gagné !", SUCCESS_COLOR);
                        wins++;
                    } else {
                        statusLabel.setText("💔 Vous avez perdu !");
                        statusLabel.setForeground(DANGER_COLOR);
                        addChatMessage("Défaite", "Dommage, vous avez perdu...", DANGER_COLOR);
                        losses++;
                    }
                } else {
                    statusLabel.setText("❌ " + message.getContent());
                    statusLabel.setForeground(DANGER_COLOR);
                    addChatMessage("Système", message.getContent(), DANGER_COLOR);
                }
                
                updateStatistics();
                addGameToHistory(message);
                
                JOptionPane.showMessageDialog(this, 
                    message.getContent(),
                    "Fin de partie", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
    
    private void makeMove(int row, int col) {
        if (!connected || !myTurn) {
            return;
        }
        
        if (board[row][col] != ' ') {
            return;
        }
        
        try {
            GameMessage moveMessage = new GameMessage(GameMessage.MessageType.PLAYER_MOVE, 
                    row, col, playerSymbol);
            output.writeObject(moveMessage);
            output.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'envoi du coup: " + e.getMessage(),
                "Erreur de communication", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBoard(char[][] newBoard) {
        if (newBoard == null) return;
        
        this.board = newBoard;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = (board[i][j] == ' ') ? "" : String.valueOf(board[i][j]);
                gameButtons[i][j].setText(text);
                
                // Coloration des symboles
                if (board[i][j] == 'X') {
                    gameButtons[i][j].setForeground(Color.BLUE);
                } else if (board[i][j] == 'O') {
                    gameButtons[i][j].setForeground(Color.RED);
                }
            }
        }
    }
    
    private void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                gameButtons[i][j].setText("");
                gameButtons[i][j].setForeground(Color.BLACK);
            }
        }
    }
    
    private void enableGameButtons(boolean enabled) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j].setEnabled(enabled);
            }
        }
    }
    
    private void requestNewGame() {
        int result = JOptionPane.showConfirmDialog(this,
            "Voulez-vous commencer une nouvelle partie?\n" +
            "Cela nécessite que l'autre joueur se reconnecte.",
            "Nouvelle partie",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            disconnect();
            clearBoard();
            enableGameButtons(false);
            myTurn = false;
            statusLabel.setText("Reconnectez-vous pour une nouvelle partie");
            statusLabel.setForeground(Color.BLACK);
            connectButton.setEnabled(true);
            newGameButton.setEnabled(false);
        }
    }
    
    private void disconnect() {
        connected = false;
        
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
    
    private void createSidePanel() {
        sidePanel = new JPanel(new BorderLayout(0, 15));
        sidePanel.setBackground(BACKGROUND_COLOR);
        sidePanel.setPreferredSize(new Dimension(350, 0));
        
        // Section Chat
        JPanel chatPanel = createChatPanel();
        
        // Section Statistiques
        JPanel statsPanel = createStatsPanel();
        
        // Section Historique
        JPanel historyPanel = createHistoryPanel();
        
        // Assembler le panel latéral
        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setBackground(BACKGROUND_COLOR);
        topSection.add(statsPanel, BorderLayout.NORTH);
        topSection.add(chatPanel, BorderLayout.CENTER);
        
        sidePanel.add(topSection, BorderLayout.CENTER);
        sidePanel.add(historyPanel, BorderLayout.SOUTH);
        
        mainPanel.add(sidePanel, BorderLayout.EAST);
    }
    
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout(0, 10));
        chatPanel.setBackground(CARD_COLOR);
        chatPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
                "💬 Chat", 0, 0, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Zone de chat
        chatArea = new JTextArea(8, 25);
        chatArea.setEditable(false);
        chatArea.setBackground(BACKGROUND_COLOR);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Panel d'input
        JPanel chatInputPanel = new JPanel(new BorderLayout(5, 0));
        chatInputPanel.setBackground(CARD_COLOR);
        
        chatInput = new JTextField();
        chatInput.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chatInput.addActionListener(e -> sendChatMessage());
        
        sendChatButton = new JButton("📤");
        sendChatButton.setPreferredSize(new Dimension(40, 30));
        sendChatButton.addActionListener(e -> sendChatMessage());
        styleButton(sendChatButton, PRIMARY_COLOR);
        
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendChatButton, BorderLayout.EAST);
        
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
        
        // Messages par défaut
        addChatMessage("Système", "Bienvenue dans le chat !", SUCCESS_COLOR);
        addChatMessage("Système", "Connectez-vous pour commencer à jouer.", WARNING_COLOR);
        
        return chatPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBackground(CARD_COLOR);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
                "📊 Statistiques", 0, 0, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Cartes de statistiques
        JPanel winsCard = createStatCard("🏆 Victoires", "0", SUCCESS_COLOR);
        JPanel lossesCard = createStatCard("💔 Défaites", "0", DANGER_COLOR);
        JPanel drawsCard = createStatCard("🤝 Nuls", "0", WARNING_COLOR);
        JPanel gamesCard = createStatCard("🎮 Parties", "0", PRIMARY_COLOR);
        
        statsPanel.add(winsCard);
        statsPanel.add(lossesCard);
        statsPanel.add(drawsCard);
        statsPanel.add(gamesCard);
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color.brighter().brighter());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(color.darker());
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(CARD_COLOR);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
                "📜 Historique", 0, 0, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        historyPanel.setPreferredSize(new Dimension(0, 150));
        
        JTextArea historyArea = new JTextArea(6, 25);
        historyArea.setEditable(false);
        historyArea.setBackground(BACKGROUND_COLOR);
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        historyArea.setText("Aucune partie jouée encore...");
        
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createLoweredBevelBorder());
        
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        
        return historyPanel;
    }
    
    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        // Boutons avec style moderne
        connectButton = new JButton("🔗 Se connecter");
        connectButton.addActionListener(e -> connectToServer());
        styleButton(connectButton, SUCCESS_COLOR);
        
        newGameButton = new JButton("🎮 Nouvelle partie");
        newGameButton.setEnabled(false);
        newGameButton.addActionListener(e -> requestNewGame());
        styleButton(newGameButton, PRIMARY_COLOR);
        
        settingsButton = new JButton("⚙️ Paramètres");
        settingsButton.addActionListener(e -> showSettings());
        styleButton(settingsButton, SECONDARY_COLOR);
        
        JButton aboutButton = new JButton("ℹ️ À propos");
        aboutButton.addActionListener(e -> showAbout());
        styleButton(aboutButton, WARNING_COLOR);
        
        controlPanel.add(connectButton);
        controlPanel.add(newGameButton);
        controlPanel.add(settingsButton);
        controlPanel.add(aboutButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    private void applyModernTheme() {
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            // Utiliser le look and feel par défaut
        }
        
        // Personnaliser les composants globaux
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void addChatMessage(String sender, String message, Color color) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        chatArea.append("[" + timestamp + "] " + sender + ": " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty() && connected) {
            addChatMessage("Vous", message, PRIMARY_COLOR);
            chatInput.setText("");
            // TODO: Envoyer le message au serveur (à implémenter si nécessaire)
        }
    }
    
    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "⚙️ Paramètres", true);
        settingsDialog.setLayout(new BorderLayout());
        settingsDialog.setSize(400, 300);
        settingsDialog.setLocationRelativeTo(this);
        
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Options de paramètres
        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(new JLabel("🎵 Sons:"), gbc);
        gbc.gridx = 1;
        JCheckBox soundCheckBox = new JCheckBox("Activé", true);
        settingsPanel.add(soundCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        settingsPanel.add(new JLabel("🎨 Thème:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Moderne", "Classique", "Sombre"});
        settingsPanel.add(themeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        settingsPanel.add(new JLabel("🖥️ Serveur:"), gbc);
        gbc.gridx = 1;
        JTextField serverField = new JTextField(SERVER_HOST, 15);
        settingsPanel.add(serverField, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("💾 Sauvegarder");
        JButton cancelButton = new JButton("❌ Annuler");
        
        saveButton.addActionListener(e -> {
            // TODO: Sauvegarder les paramètres
            settingsDialog.dispose();
            addChatMessage("Système", "Paramètres sauvegardés !", SUCCESS_COLOR);
        });
        
        cancelButton.addActionListener(e -> settingsDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }
    
    private void showAbout() {
        String aboutText = "🎮 Morpion Multijoueur - Arthur Chessé\n\n" +
                          "Développé en Java avec Swing\n\n" +
                          "Fonctionnalités:\n" +
                          "• Interface graphique\n" +
                          "• Multijoueur en réseau\n" +
                          "• Animations fluides\n\n" +
                          "© 2025 - CodingGame";
        
        JOptionPane.showMessageDialog(this, aboutText, "À propos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatistics() {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText(String.format("📊 V: %d | D: %d | N: %d", wins, losses, draws));
            
            // Mettre à jour les cartes de statistiques
            Component[] components = sidePanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    updateStatCards((JPanel) comp);
                }
            }
        });
    }
    
    private void updateStatCards(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                Component[] cardComponents = card.getComponents();
                if (cardComponents.length >= 2 && cardComponents[1] instanceof JLabel) {
                    JLabel valueLabel = (JLabel) cardComponents[1];
                    JLabel titleLabel = (JLabel) cardComponents[0];
                    String title = titleLabel.getText();
                    
                    if (title.contains("Victoires")) {
                        valueLabel.setText(String.valueOf(wins));
                    } else if (title.contains("Défaites")) {
                        valueLabel.setText(String.valueOf(losses));
                    } else if (title.contains("Nuls")) {
                        valueLabel.setText(String.valueOf(draws));
                    } else if (title.contains("Parties")) {
                        valueLabel.setText(String.valueOf(wins + losses + draws));
                    }
                }
            }
        }
    }
    
    private void addGameToHistory(GameMessage message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String result;
        
        if (message.isGameWon()) {
            char winner = message.getWinner();
            if (winner == 'D') {
                result = "Match nul";
            } else if (winner == playerSymbol) {
                result = "Victoire";
            } else {
                result = "Défaite";
            }
        } else {
            result = "Partie interrompue";
        }
        
        String historyEntry = String.format("[%s] %s - %s", timestamp, result, message.getContent());
        gameHistory.add(historyEntry);
        
        // Mettre à jour l'affichage de l'historique
        updateHistoryDisplay();
    }
    
    private void updateHistoryDisplay() {
        // Trouver la zone d'historique et la mettre à jour
        // Cette méthode sera appelée pour rafraîchir l'affichage
        SwingUtilities.invokeLater(() -> {
            StringBuilder historyText = new StringBuilder();
            if (gameHistory.isEmpty()) {
                historyText.append("Aucune partie jouée encore...");
            } else {
                // Afficher les 10 dernières parties
                int start = Math.max(0, gameHistory.size() - 10);
                for (int i = start; i < gameHistory.size(); i++) {
                    historyText.append(gameHistory.get(i)).append("\n");
                }
            }
            
            // Mettre à jour l'affichage (à implémenter selon l'architecture des composants)
        });
    }
}
