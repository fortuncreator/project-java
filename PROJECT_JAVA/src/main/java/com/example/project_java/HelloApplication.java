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
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Tank player1;
    private Terrain terrain;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private List<Missile> missiles = new ArrayList<>(); //lista przechowujaca wszystkie aktywne przyciski
    private boolean enterPressed = false;

    @Override
    public void start(Stage primaryStage) {
        // Tworzymy "płótno", na którym będziemy rysować naszą grę
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                leftPressed = true;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                rightPressed = true;
            }
            if (event.getCode() == KeyCode.UP) {
                upPressed = true;
            }
            if (event.getCode() == KeyCode.DOWN) {
                downPressed = true;
            }
            if (event.getCode() == KeyCode.ENTER) {
                enterPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                leftPressed = false;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                rightPressed = false;
            }
            if (event.getCode() == KeyCode.UP) {
                upPressed = false;
            }
            if (event.getCode() == KeyCode.DOWN) {
                downPressed = false;
            }
            if (event.getCode() == KeyCode.ENTER) {
                enterPressed = false;
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
        if (upPressed){
            player1.aimUp();
        }
        if (downPressed){
            player1.aimDown();
        }
        // 1. Logika strzału - zabezpieczenie by nie strzelac ciaglym strumieniem,
        // na razie dla uproszczenia wystrzeli, gdy trzymasz spację)
        if (enterPressed){
            Missile newMissile = new Missile(
                    player1.getShootX(),               // Dokładny punkt X z końca lufy
                    player1.getShootY(),               // Dokładny punkt Y z końca lufy
                    player1.getAbsoluteBarrelAngle(),  // Kąt lotu uwzględniający nachylenie góry!
                    10                                 // Moc strzału
            );
            missiles.add(newMissile);

            enterPressed = false; //reset zeby wystrzelic tylko jeden pocisk
        }
        //aktualizacja wszystkich pociskow na liscie
        for (Missile m : missiles){
            m.update(terrain);
        }
    }

    // Metoda do rysowania klatki na ekranie
    private void draw(GraphicsContext gc) {
        // Czyścimy ekran co klatkę (rysujemy błękitne niebo)
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        terrain.draw(gc);
        player1.draw(gc);

        for (Missile m : missiles){
            m.draw(gc);
        }
    }
}