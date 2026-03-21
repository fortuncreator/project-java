package com.example.project_java;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Tank player1;
    private Terrain terrain;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    @Override
    public void start(Stage primaryStage) {
        // Tworzymy "płótno", na którym będziemy rysować naszą grę
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Podstawowy układ okna
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT){
                leftPressed = true;
            }
            if (event.getCode() == KeyCode.RIGHT){
                rightPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT){
                leftPressed = false;
            }
            if (event.getCode() == KeyCode.RIGHT){
                rightPressed = false;
            }
        });

        // Ustawienia głównego okna
        primaryStage.setTitle("Project JAVA");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Game Loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();   // 1. liczenie fizyki
                player1.update(terrain);
                draw(gc);   // 2. rysowanie grafiki
            }
        };

        player1 = new Tank(400, 300);
        terrain = new Terrain(WIDTH);

        timer.start();
    }

    // Metoda do aktualizacji logiki i fizyki
    private void update() {
        if (leftPressed){
            player1.moveLeft();
        }
        if (rightPressed){
            player1.moveRight();
        }
    }

    // Metoda do rysowania klatki na ekranie
    private void draw(GraphicsContext gc) {
        // Czyścimy ekran co klatkę (rysujemy błękitne niebo)
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        terrain.draw(gc);
        player1.draw(gc);
    }
}