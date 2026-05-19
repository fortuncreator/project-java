package com.example.project_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameConfig {
    // domyslne ustawienia
    public static int startingLives = 3;
    public static double maxFuel = 200.0;

    public static void loadSettings() {
        try {
            File file = new File("settings.txt");
            Scanner scanner = new Scanner(file);

            // 1 linijka - zycia
            if (scanner.hasNextInt()) {
                startingLives = scanner.nextInt();
                startingLives = Math.max(1, Math.min(8, startingLives));
            }

            // 2 linijka - paliwo
            if (scanner.hasNextDouble()) {
                maxFuel = scanner.nextDouble();
                maxFuel = Math.max(50.0, Math.min(350.0, maxFuel));
            }
            scanner.close();
            System.out.println("Wczytano ustawienia - Życia: " + startingLives + ", Paliwo: " + maxFuel);
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku settings.txt. Wczytano ustawienia domyślne.");
        } catch (Exception e) {
            System.out.println("Błąd podczas czytania pliku: " + e.getMessage());
        }
    }
}