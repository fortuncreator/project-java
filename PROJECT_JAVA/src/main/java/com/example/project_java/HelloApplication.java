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
    private boolean isPlayer1Turn = Math.random() < 0.5;
    private double startBtnWidth = 260;
    private double startBtnHeight = 80;
    private double startBtnX = 0;
    private double startBtnY = 300;
    private boolean isHoveringStart = false; // czy myszka jest na przycisku
    private double hoverProgress = 0.0;
    private Image cloudImage;
    private double gameCloudX = 100;
    private double gameCloudY = 120;
    private double menuCloud1X = 50, menuCloud1Y = 50;
    private double menuCloud2X = 400, menuCloud2Y = 200;
    private enum GameState { MENU, PLAYING };
    private GameState state = GameState.MENU; // start w menu
    // scoreboard
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private boolean roundEnded = false;

    @Override
    public void start(Stage primaryStage) {
        GameConfig.loadSettings();
        // utworzenie planszy gdzie bedzie generowana gra
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        // ustawienia glownego okna
        primaryStage.setTitle("TANK WARS");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                state = GameState.MENU;
                return;
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
            if (event.getCode() == KeyCode.R) {
                resetGame();
            }
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
        });

        scene.setOnKeyReleased(event -> {
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
        });

        // przycisk startu
        startBtnX = WIDTH / 2 - startBtnWidth / 2;
        scene.setOnMouseMoved(event -> {
            if (state == GameState.MENU) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                // warunek czy kursor jest wewnatrz przycisku
                isHoveringStart = (mouseX >= startBtnX && mouseX <= startBtnX + startBtnWidth && mouseY >= startBtnY && mouseY <= startBtnY + startBtnHeight);
            }
        });
        scene.setOnMouseClicked(event -> {
            if (state == GameState.MENU && isHoveringStart) {
                state = GameState.PLAYING;
                resetGame();
            }
        });
        // game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();   // liczenie fizyki
                draw(gc);   // rysowanie grafiki
            }
        };
        // zaladowanie graczy oraz reszty
        player1 = new Tank(100, 100, "file:assets/tank1.png", 0, 6);
        player2 = new Tank(600, 100, "file:assets/tank2.png", 180, -6);
        terrain = new Terrain(WIDTH);
        heartImage = new Image("file:assets/heart.png");
        cloudImage = new Image("file:assets/cloud.png");
        timer.start();
    }
    // metoda do aktualizacji logiki i fizyki
    private void update() {
        // ruch chmury w grze
        gameCloudX += 0.3;
        if (gameCloudX > WIDTH) gameCloudX = -150; // reset za lewą krawędź
        // animacje menu
        if (state == GameState.MENU) {
            menuCloud1X += 0.5;
            if (menuCloud1X > WIDTH) menuCloud1X = -150;
            menuCloud2X += 0.8;
            if (menuCloud2X > WIDTH) menuCloud2X = -150;
        }
        if (state == GameState.MENU) {
            if (isHoveringStart && hoverProgress < 1.0) {
                hoverProgress += 0.05;
            } else if (!isHoveringStart && hoverProgress > 0.0) {
                hoverProgress -= 0.05;
            }
            hoverProgress = Math.max(0.0, Math.min(1.0, hoverProgress));
            return; // wyjscie z metody zeby fizyka gry nie liczyla sie w menu
        }
        // system tur i sterowanie
        if (missiles.isEmpty() && !player1.isDead() && !player2.isDead()) {
            if (isPlayer1Turn) {
                if (aPressed) player1.moveLeft(terrain);
                if (dPressed) player1.moveRight(terrain, WIDTH);
                if (wPressed) player1.aimUp();
                if (sPressed) player1.aimDown();
                if (shiftPressed) {
                    Missile newMissile = new Missile(player1.getShootX(), player1.getShootY(), player1.getAbsoluteBarrelAngle(), 10);
                    missiles.add(newMissile);
                    shiftPressed = false;
                    isPlayer1Turn = false;
                    player2.resetFuel();
                }
            }
            else {
                if (leftPressed) player2.moveLeft(terrain);
                if (rightPressed) player2.moveRight(terrain, WIDTH);
                if (upPressed) player2.aimUp();
                if (downPressed) player2.aimDown();
                if (enterPressed) {
                    Missile newMissile = new Missile(player2.getShootX(), player2.getShootY(), player2.getAbsoluteBarrelAngle(), 10);
                    missiles.add(newMissile);
                    enterPressed = false;
                    isPlayer1Turn = true;
                    player1.resetFuel();
                }
            }
        }
        // fizyka czolgow
        player1.update(terrain);
        player2.update(terrain);
        // smierc w przepasci
        if(player1.getY() > HEIGHT) {
            player1.dieInstantly();
        }
        if(player2.getY() > HEIGHT) {
            player2.dieInstantly();
        }
        // fizyka pociskow
        java.util.Iterator<Missile> iterator = missiles.iterator();
        while (iterator.hasNext()) {
            Missile m = iterator.next(); // pobranie kolejnego pocisku
            m.update(terrain);
            // sprawdzenie trafienie w gracza 1
            if(!player1.isDead() && m.hasCollidedWithTank(player1)){
                player1.takeDamage();
                iterator.remove();
                continue;
            }
            // sprawdzenie trafienie w gracza 2
            if(!player2.isDead() && m.hasCollidedWithTank(player2)){
                player2.takeDamage();
                iterator.remove();
                continue;
            }
            // sprawdzenie kolizji z ziemia
            if (m.hasCollidedWithGround(terrain)) {
                terrain.createCrater(m.getX(), m.getY(), 40);
                iterator.remove();
            } else if (m.getY() > HEIGHT || m.getX() < 0 || m.getX() > WIDTH) {
                iterator.remove();
            }
        }
        // przyznawanie punktow
        if(!roundEnded){
            if (player1.isDead()) {
                scorePlayer2++;
                roundEnded = true;
            }
            else if (player2.isDead()) {
                scorePlayer1++;
                roundEnded = true;
            }
        }
    }

    private void resetGame(){
        terrain = new Terrain(WIDTH);
        missiles.clear();
        isPlayer1Turn = Math.random() < 0.5;
        player1.resetState(100, 100);
        player2.resetState(600, 100);
        roundEnded = false;
    }
    // glowna metoda renderujaca wszystkie elementy graficzne w danej klatce
    private void draw(GraphicsContext gc) {
        if (state == GameState.MENU) {
            drawMenu(gc);
            return;
        }
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        terrain.draw(gc);
        gc.drawImage(cloudImage, gameCloudX, gameCloudY, 180, 90);
        // scoreboard
        gc.setFill(Color.rgb(40, 40, 40));
        gc.setFont(javafx.scene.text.Font.font("Impact", javafx.scene.text.FontWeight.BOLD, 46));
        gc.fillText(scorePlayer1 + " : " + scorePlayer2, WIDTH / 2 - 45, 50);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 20));
        // animacja strzalki
        double time = System.currentTimeMillis() / 150.0;
        double bounceOffset = Math.sin(time) * 6;
        // gracz 1
        gc.setFill(Color.DARKBLUE);
        gc.fillText("GRACZ 1", 20, 95);
        if (isPlayer1Turn && !roundEnded) {
            gc.fillText("◀", 115 + bounceOffset, 95);
        }
        // gracz 2
        gc.setFill(Color.DARKRED);
        gc.fillText("GRACZ 2", WIDTH - 110, 95);
        if (!isPlayer1Turn && !roundEnded) {
            gc.fillText("▶", WIDTH - 145 - bounceOffset, 95);
        }
        // czolgi
        if (!player1.isDead()) {
            player1.draw(gc);
        }
        if (!player2.isDead()) {
            player2.draw(gc);
        }
        for (Missile m : missiles) {
            m.draw(gc);
        }
        // zycia gracz 1
        for (int i = 0; i < player1.getLives(); i++) {
            gc.drawImage(heartImage, 20 + (i * 35), 20);
        }
        // zycia gracz 2
        for (int i = 0; i < player2.getLives(); i++) {
            gc.drawImage(heartImage, (WIDTH - 50) - (i * 35), 20);
        }
        // paliwo gracz 1
        if (!player1.isDead()) {
            gc.setStroke(Color.rgb(20, 20, 20));
            gc.setLineWidth(2.0);
            gc.strokeRect(20, 60, player1.getMaxFuel(), 10);
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(20, 60, player1.getMaxFuel(), 10);
            double perc1 = player1.getFuel() / player1.getMaxFuel();
            Color c1;
            if (isPlayer1Turn) {
                c1 = Color.hsb(perc1 * 120, 1.0, 1.0);
            } else {
                c1 = Color.GRAY; // Nieaktywny
            }
            gc.setFill(c1);
            gc.fillRect(20, 60, player1.getFuel(), 10);
        }
        // paliwo gracz 2
        if (!player2.isDead()) {
            double startX2 = (WIDTH - 20) - player2.getMaxFuel();
            gc.setStroke(Color.rgb(20, 20, 20));
            gc.setLineWidth(2.0);
            gc.strokeRect(startX2, 60, player2.getMaxFuel(), 10);
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(startX2, 60, player2.getMaxFuel(), 10);
            double perc2 = player2.getFuel() / player2.getMaxFuel();
            Color c2;
            if (!isPlayer1Turn) {
                c2 = Color.hsb(perc2 * 120, 1.0, 1.0);
            } else {
                c2 = Color.GRAY;
            }
            gc.setFill(c2);
            gc.fillRect(startX2, 60, player2.getFuel(), 10);
        }
        // game over
        if (player1.isDead() || player2.isDead()) {
            gc.save();
            gc.setFill(Color.DARKRED);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 48));
            String winnerText = player1.isDead() ? "GRACZ 2 WYGRYWA!" : "GRACZ 1 WYGRYWA!";
            gc.fillText(winnerText, WIDTH / 2 - 250, HEIGHT / 2);
            gc.restore();
        }
    }

    private void drawMenu(GraphicsContext gc) {
        // tlo
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.drawImage(cloudImage, menuCloud1X, menuCloud1Y, 150, 80);
        gc.drawImage(cloudImage, menuCloud2X, menuCloud2Y, 200, 100);
        // tytul
        gc.setFill(Color.DARKRED);
        gc.setFont(javafx.scene.text.Font.font("Impact", javafx.scene.text.FontWeight.BOLD, 80));
        gc.fillText("TANK WARS", WIDTH / 2 - 180, 150);
        // przycisk
        double currentWidth = startBtnWidth + (30 * hoverProgress);
        double currentHeight = startBtnHeight + (15 * hoverProgress);
        double currentX = startBtnX - ((currentWidth - startBtnWidth) / 2);
        double currentY = startBtnY - ((currentHeight - startBtnHeight) / 2);
        Color startColor = Color.ORANGE;
        Color hoverColor = Color.DARKORANGE;
        gc.setFill(startColor.interpolate(hoverColor, hoverProgress));
        gc.fillRoundRect(currentX, currentY, currentWidth, currentHeight, 25, 25);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeRoundRect(currentX, currentY, currentWidth, currentHeight, 25, 25);
        gc.setFill(Color.WHITE);
        double fontSize = 40 + (8 * hoverProgress);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
        double textOffset = 65 + (5 * hoverProgress);
        gc.fillText("START", WIDTH / 2 - textOffset, startBtnY + 54 + (3 * hoverProgress));
    }
}