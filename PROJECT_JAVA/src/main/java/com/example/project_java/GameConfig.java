package com.example.project_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameConfig {
    // Domyślne wartości, gdyby pliku nie było
    public static int startingLives = 3;
    public static double maxFuel = 200.0;

    public static void loadSettings() {
        // Obsługa wyjątków - blok TRY
        try {
            File file = new File("settings.txt");
            Scanner scanner = new Scanner(file);

            // 1 linijka (życia)
            if (scanner.hasNextInt()) {
                startingLives = scanner.nextInt();
                // ZABEZPIECZENIE: Życia muszą być w przedziale od 1 do 8
                startingLives = Math.max(1, Math.min(8, startingLives));
            }

            // 2 linijka (paliwo)
            if (scanner.hasNextDouble()) {
                maxFuel = scanner.nextDouble();
                // ZABEZPIECZENIE: Paliwo musi być w przedziale od 50 do 350
                // (powyżej 350 lewy i prawy pasek paliwa zaczną na siebie nachodzić na środku!)
                maxFuel = Math.max(50.0, Math.min(350.0, maxFuel));
            }
            scanner.close(); // Pamiętamy o zamknięciu strumienia!
            System.out.println("Wczytano ustawienia - Życia: " + startingLives + ", Paliwo: " + maxFuel);

            // Przechwycenie błędu braku pliku
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku settings.txt. Gramy na domyślnych ustawieniach.");
        } catch (Exception e) {
            System.out.println("Błąd podczas czytania pliku: " + e.getMessage());
        }
    }
}