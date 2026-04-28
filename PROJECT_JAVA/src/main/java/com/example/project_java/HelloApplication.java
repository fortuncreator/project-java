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
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Tank player1;
    private Terrain terrain;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean shiftPressed = false;

    private Tank player2;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean enterPressed = false;
    private List<Missile> missiles = new ArrayList<>(); //lista przechowujaca wszystkie aktywne przyciski
    private Image heartImage;
    private boolean isPlayer1Turn = true;


    @Override
    public void start(Stage primaryStage) {
        // Tworzymy "płótno", na którym będziemy rysować naszą grę
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(event -> {
            // gracz 2
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
            // gracz 1
            if (event.getCode() == KeyCode.A) {
                aPressed = true;
            }
            if (event.getCode() == KeyCode.D) {
                dPressed = true;
            }
            if (event.getCode() == KeyCode.W) {
                wPressed = true;
            }
            if (event.getCode() == KeyCode.S) {
                sPressed = true;
            }
            if (event.getCode() == KeyCode.SHIFT) {
                shiftPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            // gracz 2
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
            // gracz 1
            if (event.getCode() == KeyCode.A) {
                aPressed = false;
            }
            if (event.getCode() == KeyCode.D) {
                dPressed = false;
            }
            if (event.getCode() == KeyCode.W) {
                wPressed = false;
            }
            if (event.getCode() == KeyCode.S) {
                sPressed = false;
            }
            if (event.getCode() == KeyCode.SHIFT) {
                shiftPressed = false;
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

        // start gracza 1: pozycja X=100, tank1, lufa patrzy w prawo (kąt 0)
        player1 = new Tank(100, 100, "file:assets/tank1.png", 0, 6);

        // start gracza 2: pozycja X=600, tank2, lufa patrzy w lewo (kąt 180)
        player2 = new Tank(600, 100, "file:assets/tank2.png", 180, -6);
        terrain = new Terrain(WIDTH);

        heartImage = new Image("file:assets/heart.png");

        timer.start();
    }

    // Metoda do aktualizacji logiki i fizyki
    private void update() {

        // GRACZ 1 STEROWANIE i STRZAL
        if (aPressed){
            player1.moveLeft();
        }
        if (dPressed){
            player1.moveRight();
        }
        if (wPressed){
            player1.aimUp();
        }
        if (sPressed){
            player1.aimDown();
        }
        // 1. Logika strzału - zabezpieczenie by nie strzelac ciaglym strumieniem,
        // na razie dla uproszczenia wystrzeli, gdy trzymasz spację)
        if (shiftPressed){
            Missile newMissile = new Missile(
                    player1.getShootX(),               // Dokładny punkt X z końca lufy
                    player1.getShootY(),               // Dokładny punkt Y z końca lufy
                    player1.getAbsoluteBarrelAngle(),  // Kąt lotu uwzględniający nachylenie góry!
                    10                                 // Moc strzału
            );
            missiles.add(newMissile);
            shiftPressed = false; //reset zeby wystrzelic tylko jeden pocisk
        }
        // lista pomocnicza na pociski ktore wybuchly i trzeba je usunac
        List<Missile> missilesToRemove = new ArrayList<>();

        for (Missile m : missiles) {
            m.update(terrain);

            // sprawdzamy trafienie w gracza 1
            if(!player1.isDead() && m.hasCollidedWithTank(player1)){
                player1.takeDamage();
                missilesToRemove.add(m); // pocisk do usuniecia
            }
            // sprawdzamy trafienie w gracza 2
            if(!player2.isDead() && m.hasCollidedWithTank(player2)){
                player2.takeDamage();
                missilesToRemove.add(m);
            }
            if(m.hasCollidedWithGround(terrain)){
                missilesToRemove.add(m);
            }
        }
        // fizycznie usuwamy wszystkie trafione i zniszczone pociski z gry
        missiles.removeAll(missilesToRemove);

        // GRACZ 2 STEROWANIE i STRZAL
        if (leftPressed){
            player2.moveLeft();
        }
        if (rightPressed){
            player2.moveRight();
        }
        if (upPressed){
            player2.aimUp();
        }
        if (downPressed){
            player2.aimDown();
        }
        // 1. Logika strzału - zabezpieczenie by nie strzelac ciaglym strumieniem,
        // na razie dla uproszczenia wystrzeli, gdy trzymasz spację)
        if (enterPressed){
            Missile newMissile = new Missile(
                    player2.getShootX(),               // Dokładny punkt X z końca lufy
                    player2.getShootY(),               // Dokładny punkt Y z końca lufy
                    player2.getAbsoluteBarrelAngle(),  // Kąt lotu uwzględniający nachylenie góry!
                    10                                 // Moc strzału
            );
            missiles.add(newMissile);
            enterPressed = false; //reset zeby wystrzelic tylko jeden pocisk
        }
        // --- AKTUALIZACJA FIZYKI ---
        player1.update(terrain);
        player2.update(terrain);
        //aktualizacja wszystkich pociskow na liscie
        for (Missile m : missiles){
            m.update(terrain);
        }
        missiles.removeIf(m -> m.hasCollidedWithGround(terrain) || m.getY() > HEIGHT);
    }

    // Metoda do rysowania klatki na ekranie
    private void draw(GraphicsContext gc) {
        // Czyścimy ekran co klatkę (rysujemy błękitne niebo)
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // ... (wyżej masz gc.fillRect do czyszczenia tła) ...

        terrain.draw(gc);

        // RYSOWANIE CZOŁGÓW (Tylko jeśli żyją)
        if (!player1.isDead()) {
            player1.draw(gc);
        }
        if (!player2.isDead()) {
            player2.draw(gc);
        }

        for (Missile m : missiles) {
            m.draw(gc);
        }

        // ==========================================
        // HUD: RYSOWANIE SERDUSZEK (Zamiast brzydkiego tekstu!)
        // ==========================================
        // Życia Gracza 1 (lewy górny róg)
        for (int i = 0; i < player1.getLives(); i++) {
            gc.drawImage(heartImage, 20 + (i * 35), 20);
        }

        // Życia Gracza 2 (prawy górny róg)
        for (int i = 0; i < player2.getLives(); i++) {
            gc.drawImage(heartImage, (WIDTH - 50) - (i * 35), 20);
        }

        // ==========================================
        // EKRAN KOŃCOWY (GAME OVER)
        // ==========================================
        if (player1.isDead() || player2.isDead()) {
            gc.save(); // Zapisujemy "normalny" stan malarza (żeby nie psuł HUDu w tle)

            gc.setFill(Color.DARKRED);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 48));

            String winnerText = player1.isDead() ? "GRACZ 2 WYGRYWA!" : "GRACZ 1 WYGRYWA!";

            // Rysujemy wielki tekst
            gc.fillText(winnerText, WIDTH / 2 - 220, HEIGHT / 2);

            gc.restore(); // Przywracamy malarzowi jego domyślny, mały pędzel!
        }
    }
}