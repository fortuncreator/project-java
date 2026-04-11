package com.example.project_java;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected double x, y;

    public GameObject(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public abstract void update(Terrain terrain);
    public abstract void draw(GraphicsContext gc);
}