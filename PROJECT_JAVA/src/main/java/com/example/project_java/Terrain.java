package com.example.project_java;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Terrain {
    private double[] heights; // tablica przechowujaca 800 wysokosci

    // konstruktor - generowanie gor
    public Terrain(int screenWidth) {
        heights = new double[screenWidth]; // tworzenie 800 szufladek w tablicy

        double offset = Math.random() * 1000; // losowe ustawienie gor
        for (int i = 0; i < screenWidth; i++) {
            heights[i] = 400 + Math.sin((i + offset) * 0.01) * 50; // 400 - bazowa wysokosc ziemi,
            // sinus rysuje fale, i * 0.01 to czestotliwosc fali a *50 to amplituda
        }
    }
    //metoda do rysowania ziemi
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.FORESTGREEN); // ustawiam kolor na fajny zielony

        for (int i = 0; i < heights.length; i++) {
            gc.fillRect(i, heights[i], 1, 600 - heights[i]);
        }
    }

    public double getHeight(int x){
        // zabezpieczenia zeby nie wyjsc poza ekran
        if (x < 0) return heights[0];
        if (x >= heights.length) return heights[heights.length - 1];
        // zwraca wysokosc ziemi w zadanym punkcie x
        return heights[x];
    }
}
