package com.example.project_java;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tank extends GameObject {
    @Override
    public void update() {

    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(tankImage, x, y);

    }

    private Image tankImage;

    public Tank(double startX, double startY) {
        super(startX, startY); // wywolanie konstruktora GameObject(startX, startY)
        tankImage = new Image("file:assets/tank1.png");

    }
}