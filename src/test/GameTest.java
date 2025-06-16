package test;

import common.TicTacToeGame;

/**
 * Classe de test simple pour valider la logique du jeu
 */
public class GameTest {
    public static void main(String[] args) {
        System.out.println("=== Test de la logique du jeu de Morpion ===");
        
        TicTacToeGame game = new TicTacToeGame();
        
        // Test 1: Partie normale
        System.out.println("\nTest 1: Partie normale");
        System.out.println("Joueur actuel: " + game.getCurrentPlayer()); // Doit être X
        
        // X joue en (0,0)
        boolean move1 = game.makeMove(0, 0, 'X');
        System.out.println("X joue en (0,0): " + move1); // true
        System.out.println("Joueur actuel: " + game.getCurrentPlayer()); // Doit être O
        
        // O joue en (1,1)
        boolean move2 = game.makeMove(1, 1, 'O');
        System.out.println("O joue en (1,1): " + move2); // true
        System.out.println("Joueur actuel: " + game.getCurrentPlayer()); // Doit être X
        
        // Test 2: Coup invalide (case déjà occupée)
        System.out.println("\nTest 2: Coup invalide");
        boolean move3 = game.makeMove(0, 0, 'X');
        System.out.println("X rejoue en (0,0): " + move3); // false
        
        // Test 3: Victoire en ligne
        System.out.println("\nTest 3: Test de victoire");
        game.reset();
        
        // X fait une ligne horizontale
        game.makeMove(0, 0, 'X'); // X _ _
        game.makeMove(1, 0, 'O'); // O _ _
        game.makeMove(0, 1, 'X'); // X X _
        game.makeMove(1, 1, 'O'); // O O _
        game.makeMove(0, 2, 'X'); // X X X <- Victoire!
        
        System.out.println("Partie terminée: " + game.isGameWon());
        System.out.println("Gagnant: " + game.getWinner());
        
        // Afficher la grille finale
        System.out.println("\nGrille finale:");
        char[][] board = game.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print("[" + board[i][j] + "]");
            }
            System.out.println();
        }
        
        System.out.println("\n=== Tous les tests sont terminés ===");
    }
}
