package com.example.project_java;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Terrain {
    private final double[] heights; // tablica przechowujaca 800 wysokosci
    // konstruktor - generowanie gor
    public Terrain(int screenWidth) {
        heights = new double[screenWidth];
        double offset = Math.random() * 1000;
        for (int i = 0; i < screenWidth; i++) {
            heights[i] = 400 + Math.sin((i + offset) * 0.01) * 50;
        }
    }
    // metoda do rysowania ziemi
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.FORESTGREEN);
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

    public void createCrater(double impactX, double impactY, double radius){
        // zabezpieczenie krawedzi ekranu zeby nie wyjsc poza tablice
        int startX = (int) Math.max(0, impactX - radius);
        int endX = (int) Math.min(heights.length - 1, impactX + radius);

        for (int x = startX; x <= endX; x++) {
            double distanceX = Math.abs(x - impactX);
            double depth = Math.sqrt((radius * radius) - (distanceX * distanceX));
            double craterBottomY = impactY + depth;
            if(heights[x] < craterBottomY){
                heights[x] = craterBottomY;
            }
        }
    }
}
