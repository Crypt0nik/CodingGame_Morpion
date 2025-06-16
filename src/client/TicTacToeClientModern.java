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

/**
 * Client du jeu de Morpion avec interface graphique moderne
 */
public class TicTacToeClientModern extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    
    // Couleurs ultra-modernes avec dégradés
    private static final Color PRIMARY = new Color(99, 102, 241);        // Indigo vibrant
    private static final Color SECONDARY = new Color(139, 92, 246);      // Violet
    private static final Color SUCCESS = new Color(16, 185, 129);        // Émeraude
    private static final Color DANGER = new Color(244, 63, 94);          // Rose
    private static final Color WARNING = new Color(251, 191, 36);        // Ambre
    private static final Color BACKGROUND = new Color(15, 23, 42);       // Slate foncé
    private static final Color CARD_BG = new Color(30, 41, 59);          // Slate moyen
    private static final Color SURFACE = new Color(51, 65, 85);          // Slate clair
    private static final Color TEXT_LIGHT = new Color(248, 250, 252);    // Blanc cassé
    private static final Color ACCENT = new Color(34, 211, 238);         // Cyan
    
    // Variables pour animations
    private Timer pulseTimer;
    private float pulseOpacity = 1.0f;
    private boolean pulseIncreasing = false;
    
    // Composants
    private JButton[][] gameButtons;
    private JLabel statusLabel;
    private JLabel playerLabel;
    private JButton connectButton;
    private JButton newGameButton;
    
    // Communication
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connected = false;
    
    // État du jeu
    private char playerSymbol;
    private boolean myTurn = false;
    private char[][] board;
    
    public TicTacToeClientModern() {
        initializeGUI();
        board = new char[3][3];
        clearBoard();
    }
    
    private void initializeGUI() {
        setTitle("✨ Morpion Multijoueur - Ultra Design ✨");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);
        
        // Utiliser un custom JPanel avec dégradé de fond
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé de fond diagonal
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND,
                    getWidth(), getHeight(), CARD_BG
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Effet de brillance
                g2d.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, 10),
                    getWidth(), getHeight(), new Color(255, 255, 255, 2)
                ));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        
        createGlowHeader(mainPanel);
        createUltraGamePanel(mainPanel);
        createNeonControls(mainPanel);
        
        add(mainPanel);
        
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Initialiser les animations
        startPulseAnimation();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (pulseTimer != null) pulseTimer.stop();
                disconnect();
            }
        });
    }
    
    private void createGlowHeader(JPanel parent) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé principal avec effet holographique
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 102, 241, 200),
                    getWidth(), getHeight(), new Color(139, 92, 246, 150)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Effet de lueur pulsante
                int alpha = (int) (pulseOpacity * 100);
                g2d.setPaint(new Color(34, 211, 238, alpha));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);
                
                // Reflets métalliques
                g2d.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, 80),
                    0, getHeight()/3, new Color(255, 255, 255, 0)
                ));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()/3, 20, 20);
                
                g2d.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Titre avec effet de texte 3D
        JLabel title = new JLabel("✨ MORPION MULTIJOUEUR ✨", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Ombre du texte
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 3, getHeight()/2 + 3);
                
                // Texte principal avec dégradé
                GradientPaint textGradient = new GradientPaint(
                    0, 0, TEXT_LIGHT,
                    0, getHeight(), ACCENT
                );
                g2d.setPaint(textGradient);
                g2d.drawString(getText(), 0, getHeight()/2);
                
                g2d.dispose();
            }
        };
        title.setForeground(TEXT_LIGHT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        // Panel d'infos avec style glassmorphism
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond glassmorphism
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                g2d.dispose();
            }
        };
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        playerLabel = createGlowLabel("🔗 Non connecté", 16);
        statusLabel = createGlowLabel("Cliquez sur 'Se connecter' pour commencer", 14);
        
        infoPanel.add(playerLabel);
        infoPanel.add(statusLabel);
        
        header.add(title, BorderLayout.CENTER);
        header.add(infoPanel, BorderLayout.SOUTH);
        
        parent.add(header, BorderLayout.NORTH);
    }
    
    private JLabel createGlowLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Effet de lueur autour du texte
                g2d.setColor(new Color(34, 211, 238, 50));
                for (int i = 1; i <= 3; i++) {
                    g2d.setFont(getFont());
                    g2d.drawString(getText(), i, getHeight()/2 + i);
                    g2d.drawString(getText(), -i, getHeight()/2 - i);
                }
                
                // Texte principal
                g2d.setColor(TEXT_LIGHT);
                g2d.drawString(getText(), 0, getHeight()/2);
                
                g2d.dispose();
            }
        };
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        return label;
    }
    
    private void createUltraGamePanel(JPanel parent) {
        JPanel gameContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec effet de profondeur
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 25, 25);
                
                // Ombre externe
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(15, 15, getWidth()-20, getHeight()-20, 25, 25);
                
                g2d.dispose();
            }
        };
        gameContainer.setOpaque(false);
        gameContainer.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JPanel gamePanel = new JPanel(new GridLayout(3, 3, 12, 12));
        gamePanel.setOpaque(false);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        gameButtons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j] = createUltraGameButton(i, j);
                gamePanel.add(gameButtons[i][j]);
            }
        }
        
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        parent.add(gameContainer, BorderLayout.CENTER);
    }
    
    private JButton createUltraGameButton(int row, int col) {
        JButton button = new JButton("") {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Fond avec dégradé et effet 3D
                if (isPressed) {
                    // Effet enfoncé
                    GradientPaint pressedGradient = new GradientPaint(
                        0, 0, SURFACE.darker(),
                        width, height, CARD_BG
                    );
                    g2d.setPaint(pressedGradient);
                } else if (isHovered && getText().isEmpty() && myTurn && connected) {
                    // Effet de survol avec lueur
                    GradientPaint hoverGradient = new GradientPaint(
                        0, 0, new Color(34, 211, 238, 100),
                        width, height, new Color(99, 102, 241, 80)
                    );
                    g2d.setPaint(hoverGradient);
                } else {
                    // État normal avec dégradé subtil
                    GradientPaint normalGradient = new GradientPaint(
                        0, 0, TEXT_LIGHT,
                        width, height, new Color(226, 232, 240)
                    );
                    g2d.setPaint(normalGradient);
                }
                
                g2d.fillRoundRect(0, 0, width, height, 15, 15);
                
                // Bordure avec effet de profondeur
                if (getText().isEmpty()) {
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(new Color(148, 163, 184, 100));
                    g2d.drawRoundRect(1, 1, width-2, height-2, 13, 13);
                    
                    // Reflet en haut
                    g2d.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 60),
                        0, height/4, new Color(255, 255, 255, 0)
                    ));
                    g2d.fillRoundRect(2, 2, width-4, height/4, 12, 12);
                } else {
                    // Bordure colorée pour les cases occupées
                    g2d.setStroke(new BasicStroke(3));
                    Color borderColor = getText().equals("X") ? 
                        new Color(59, 130, 246) : new Color(244, 63, 94);
                    g2d.setColor(borderColor);
                    g2d.drawRoundRect(1, 1, width-2, height-2, 13, 13);
                }
                
                // Dessiner le texte avec effet 3D
                if (!getText().isEmpty()) {
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 64));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (width - fm.stringWidth(getText())) / 2;
                    int textY = (height + fm.getAscent()) / 2 - 5;
                    
                    // Ombre du texte
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.drawString(getText(), textX + 3, textY + 3);
                    
                    // Texte principal avec dégradé
                    Color textColor = getText().equals("X") ? 
                        new Color(59, 130, 246) : new Color(244, 63, 94);
                    GradientPaint textGradient = new GradientPaint(
                        textX, textY - 30, textColor.brighter(),
                        textX, textY + 20, textColor.darker()
                    );
                    g2d.setPaint(textGradient);
                    g2d.drawString(getText(), textX, textY);
                    
                    // Effet de brillance sur le texte
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.drawString(getText(), textX - 1, textY - 1);
                }
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Pas de bordure par défaut, on dessine la nôtre
            }
        };
        
        button.setPreferredSize(new Dimension(140, 140));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets d'interaction améliorés
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getText().isEmpty() && myTurn && connected) {
                    button.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.repaint();
            }
        });
        
        button.addActionListener(e -> {
            // Animation au clic
            Timer clickTimer = new Timer(100, evt -> button.repaint());
            clickTimer.setRepeats(false);
            clickTimer.start();
            
            makeMove(row, col);
        });
        
        return button;
    }
    
    private void createNeonControls(JPanel parent) {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        
        connectButton = createNeonButton("🔗 Se connecter", SUCCESS);
        connectButton.addActionListener(e -> connectToServer());
        
        newGameButton = createNeonButton("🎮 Nouvelle partie", PRIMARY);
        newGameButton.setEnabled(false);
        newGameButton.addActionListener(e -> requestNewGame());
        
        JButton aboutButton = createNeonButton("✨ À propos", WARNING);
        aboutButton.addActionListener(e -> showAbout());
        
        controls.add(connectButton);
        controls.add(newGameButton);
        controls.add(aboutButton);
        
        parent.add(controls, BorderLayout.SOUTH);
    }
    
    private JButton createNeonButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Effet de lueur néon
                if (isEnabled()) {
                    // Lueur externe
                    for (int i = 5; i >= 1; i--) {
                        int alpha = isHovered ? 60 : 30;
                        g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha / i));
                        g2d.fillRoundRect(-i, -i, width + 2*i, height + 2*i, 25 + i, 25 + i);
                    }
                }
                
                // Fond principal avec dégradé
                Color startColor = isPressed ? baseColor.darker() : 
                                 isHovered ? baseColor.brighter() : baseColor;
                Color endColor = startColor.darker();
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, height, endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, width, height, 20, 20);
                
                // Reflet en haut
                g2d.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, isEnabled() ? 100 : 50),
                    0, height/3, new Color(255, 255, 255, 0)
                ));
                g2d.fillRoundRect(0, 0, width, height/3, 20, 20);
                
                // Bordure lumineuse
                if (isEnabled()) {
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(new Color(255, 255, 255, isHovered ? 150 : 100));
                    g2d.drawRoundRect(1, 1, width-2, height-2, 18, 18);
                }
                
                // Texte avec effet de profondeur
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height + fm.getAscent()) / 2 - 2;
                
                // Ombre du texte
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), textX + 1, textY + 1);
                
                // Texte principal
                g2d.setColor(isEnabled() ? TEXT_LIGHT : new Color(TEXT_LIGHT.getRed(), TEXT_LIGHT.getGreen(), TEXT_LIGHT.getBlue(), 120));
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Pas de bordure par défaut
            }
        };
        
        button.setPreferredSize(new Dimension(160, 45));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effets d'interaction
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void startPulseAnimation() {
        pulseTimer = new Timer(50, e -> {
            if (pulseIncreasing) {
                pulseOpacity += 0.05f;
                if (pulseOpacity >= 1.0f) {
                    pulseOpacity = 1.0f;
                    pulseIncreasing = false;
                }
            } else {
                pulseOpacity -= 0.05f;
                if (pulseOpacity <= 0.3f) {
                    pulseOpacity = 0.3f;
                    pulseIncreasing = true;
                }
            }
            repaint();
        });
        pulseTimer.start();
    }
    
    private void showAbout() {
        String about = "✨ Morpion Multijoueur - Ultra Design ✨\n\n" +
                      "🚀 Version: 3.0 - Interface Ultra-Moderne\n" +
                      "💻 Développé en Java avec Swing\n\n" +
                      "🎨 Nouvelles Fonctionnalités:\n" +
                      "• Design holographique avec dégradés\n" +
                      "• Effets de lueur et animations fluides\n" +
                      "• Boutons avec effet néon\n" +
                      "• Grille 3D interactive\n" +
                      "• Glassmorphism et profondeur\n" +
                      "• Animations de pulsation\n" +
                      "• Interface futuriste\n\n" +
                      "🌟 Une expérience visuelle exceptionnelle !\n\n" +
                      "© 2025 - Ultra Morpion";
        
        JOptionPane.showMessageDialog(this, about, "✨ À propos - Ultra Design", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void connectToServer() {
        try {
            statusLabel.setText("Connexion au serveur...");
            connectButton.setEnabled(false);
            
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), 5000);
            
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            
            connected = true;
            new Thread(this::listenToServer).start();
            
            statusLabel.setText("Connecté - En attente...");
            
        } catch (IOException e) {
            connectButton.setEnabled(true);
            JOptionPane.showMessageDialog(this, 
                "Impossible de se connecter: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
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
                    statusLabel.setText("Connexion perdue");
                    
                    Object[] options = {"🔌 Reconnecter", "❌ Fermer"};
                    int response = JOptionPane.showOptionDialog(this,
                        "🔌 Connexion perdue avec le serveur !\n\n" + e.getMessage() + 
                        "\n\nVoulez-vous vous reconnecter ?",
                        "❌ Connexion interrompue",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        options,
                        options[0]);
                    
                    disconnect();
                    
                    if (response == 0) { // "Reconnecter" sélectionné
                        connectToServer();
                    }
                });
            }
        }
    }
    
    private void handleServerMessage(GameMessage message) {
        switch (message.getType()) {
            case PLAYER_CONNECTED:
                playerSymbol = message.getPlayerSymbol();
                playerLabel.setText("🎮 Joueur " + playerSymbol);
                break;
                
            case WAITING_PLAYER:
                statusLabel.setText("⏳ " + message.getContent());
                break;
                
            case GAME_START:
                statusLabel.setText("🎯 Partie commencée !");
                newGameButton.setEnabled(true);
                updateBoard(message.getBoard());
                enableGameButtons(true);
                break;
                
            case GAME_UPDATE:
                updateBoard(message.getBoard());
                break;
                
            case YOUR_TURN:
                myTurn = true;
                statusLabel.setText("✨ C'est votre tour !");
                break;
                
            case OPPONENT_TURN:
                myTurn = false;
                statusLabel.setText("⏰ Tour de l'adversaire");
                break;
                
            case INVALID_MOVE:
                JOptionPane.showMessageDialog(this, 
                    message.getContent(), "Coup invalide", JOptionPane.WARNING_MESSAGE);
                break;
                
            case GAME_OVER:
                myTurn = false;
                enableGameButtons(false);
                newGameButton.setEnabled(true); // Activer le bouton nouvelle partie
                
                String statusText = "";
                String popupTitle = "";
                String popupMessage = "";
                int messageType = JOptionPane.INFORMATION_MESSAGE;
                
                if (message.isGameWon()) {
                    char winner = message.getWinner();
                    if (winner == 'D') {
                        statusText = "🤝 Match nul !";
                        popupTitle = "🤝 Match nul";
                        popupMessage = "⚖️ Égalité ! Personne n'a gagné cette partie.\n\nVoulez-vous jouer une nouvelle partie ?";
                        messageType = JOptionPane.INFORMATION_MESSAGE;
                    } else if (winner == playerSymbol) {
                        statusText = "🎉 Vous avez gagné !";
                        popupTitle = "🎉 Victoire !";
                        popupMessage = "🏆 Félicitations ! Vous avez remporté cette partie !\n\nVoulez-vous jouer une nouvelle partie ?";
                        messageType = JOptionPane.INFORMATION_MESSAGE;
                    } else {
                        statusText = "💔 Vous avez perdu !";
                        popupTitle = "💔 Défaite";
                        popupMessage = "😔 Dommage ! Votre adversaire a gagné cette fois.\n\nVoulez-vous tenter une revanche ?";
                        messageType = JOptionPane.WARNING_MESSAGE;
                    }
                } else {
                    statusText = "❌ " + message.getContent();
                    popupTitle = "❌ Fin de partie";
                    popupMessage = message.getContent() + "\n\nVoulez-vous jouer une nouvelle partie ?";
                    messageType = JOptionPane.ERROR_MESSAGE;
                }
                
                statusLabel.setText(statusText);
                
                // Afficher un popup avec option de nouvelle partie
                Object[] options = {"🎮 Nouvelle partie", "❌ Fermer"};
                int response = JOptionPane.showOptionDialog(this,
                    popupMessage,
                    popupTitle,
                    JOptionPane.YES_NO_OPTION,
                    messageType,
                    null, // pas d'icône par défaut
                    options,
                    options[0]); // option par défaut
                
                if (response == 0) { // "Nouvelle partie" sélectionnée
                    // L'utilisateur veut jouer une nouvelle partie
                    requestNewGame();
                }
                break;
        }
    }
    
    private void makeMove(int row, int col) {
        if (!connected || !myTurn || board[row][col] != ' ') {
            return;
        }
        
        try {
            GameMessage moveMessage = new GameMessage(GameMessage.MessageType.PLAYER_MOVE, 
                    row, col, playerSymbol);
            output.writeObject(moveMessage);
            output.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBoard(char[][] newBoard) {
        if (newBoard == null) return;
        
        this.board = newBoard;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = (board[i][j] == ' ') ? "" : String.valueOf(board[i][j]);
                gameButtons[i][j].setText(text);
                
                if (board[i][j] == 'X') {
                    gameButtons[i][j].setForeground(new Color(59, 130, 246)); // Bleu
                } else if (board[i][j] == 'O') {
                    gameButtons[i][j].setForeground(new Color(239, 68, 68)); // Rouge
                }
            }
        }
    }
    
    private void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                if (gameButtons != null && gameButtons[i][j] != null) {
                    gameButtons[i][j].setText("");
                    gameButtons[i][j].setForeground(PRIMARY);
                }
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
        if (!connected) {
            // Si pas connecté, proposer de se reconnecter
            Object[] options = {"🔌 Se reconnecter", "❌ Annuler"};
            int result = JOptionPane.showOptionDialog(this,
                "🔌 Vous n'êtes pas connecté au serveur.\n\nVoulez-vous vous reconnecter pour une nouvelle partie ?",
                "🎮 Nouvelle partie", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
                
            if (result == 0) { // "Se reconnecter" sélectionné
                clearBoard();
                enableGameButtons(false);
                myTurn = false;
                playerLabel.setText("🔗 Non connecté");
                statusLabel.setText("Cliquez sur 'Se connecter' pour commencer");
                connectButton.setEnabled(true);
                newGameButton.setEnabled(false);
                connectToServer();
            }
        } else {
            // Si connecté, demander confirmation pour redémarrer
            Object[] options = {"🎮 Nouvelle partie", "❌ Annuler"};
            int result = JOptionPane.showOptionDialog(this,
                "🎮 Voulez-vous commencer une nouvelle partie ?\n\n⚠️ Cela va vous déconnecter du serveur actuel.",
                "🎮 Nouvelle partie", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
                
            if (result == 0) { // "Nouvelle partie" sélectionné
                disconnect();
                clearBoard();
                enableGameButtons(false);
                myTurn = false;
                playerLabel.setText("🔗 Non connecté");
                statusLabel.setText("Reconnectez-vous pour une nouvelle partie");
                connectButton.setEnabled(true);
                newGameButton.setEnabled(false);
            }
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
        
        // Réactiver les boutons appropriés après déconnexion
        SwingUtilities.invokeLater(() -> {
            connectButton.setEnabled(true);
            newGameButton.setEnabled(true); // Permettre de demander une nouvelle partie même déconnecté
            enableGameButtons(false);
            playerLabel.setText("🔗 Non connecté");
            if (statusLabel.getText().equals("Connexion perdue")) {
                // Ne pas changer le message si c'est une perte de connexion
            } else {
                statusLabel.setText("Déconnecté du serveur");
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToeClientModern().setVisible(true);
        });
    }
}
