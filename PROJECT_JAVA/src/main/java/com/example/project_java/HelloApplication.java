package com.example.project_java;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        // Tworzymy "płótno", na którym będziemy rysować naszą grę
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Podstawowy układ okna
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Ustawienia głównego okna
        primaryStage.setTitle("Project JAVA");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // GŁÓWNA PĘTLA GRY (Game Loop) - wykonuje się ok. 60 razy na sekundę
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();   // 1. Liczenie fizyki
                draw(gc);   // 2. Rysowanie grafiki
            }
        };
        timer.start();
    }

    // Metoda do aktualizacji logiki i fizyki
    private void update() {
        // Tu będziemy przeliczać lot pocisku, ruch czołgów itp.
    }

    // Metoda do rysowania klatki na ekranie
    private void draw(GraphicsContext gc) {
        // Czyścimy ekran co klatkę (rysujemy błękitne niebo)
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Testowe rysowanie - żółte słońce
        gc.setFill(Color.YELLOW);
        gc.fillOval(50, 50, 80, 80);
    }
}