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

    // maszyna stanow
    private enum GameState { MENU, PLAYING };
    private GameState state = GameState.MENU; // start w menu

    private double startBtnWidth = 260;
    private double startBtnHeight = 80;
    private double startBtnX = 0;
    private double startBtnY = 300;
    private boolean isHoveringStart = false; // czy myszka jest na przycisku
    private double hoverProgress = 0.0;

    // --- TABLICA WYNIKÓW ---
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private boolean roundEnded = false; // Zapobiega nabijaniu punktów co klatkę

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
            if (event.getCode() == KeyCode.R) {
                    resetGame();
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

        // Wyśrodkowanie X przycisku na podstawie zmiennej WIDTH
        startBtnX = WIDTH / 2 - startBtnWidth / 2;

        // 1. Sprawdzanie, czy myszka najechała na przycisk (Hover)
        scene.setOnMouseMoved(event -> {
            if (state == GameState.MENU) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                // Prosty warunek sprawdzający, czy kursor jest wewnątrz prostokąta przycisku
                isHoveringStart = (mouseX >= startBtnX && mouseX <= startBtnX + startBtnWidth &&
                        mouseY >= startBtnY && mouseY <= startBtnY + startBtnHeight);
            }
        });

        // 2. Kliknięcie w przycisk
        scene.setOnMouseClicked(event -> {
            if (state == GameState.MENU && isHoveringStart) {
                state = GameState.PLAYING; // Przełączamy stan na GRĘ!
                resetGame(); // Upewniamy się, że generujemy czystą mapę na start
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

        // animacje menu
        if (state == GameState.MENU) {
            // Zakładając, że gra działa w 60 FPS, dodanie 0.05 co klatkę
            // daje nam dojście od 0 do 1 w 20 klatek, czyli ok. 333ms (idealnie!)
            if (isHoveringStart && hoverProgress < 1.0) {
                hoverProgress += 0.05;
            } else if (!isHoveringStart && hoverProgress > 0.0) {
                hoverProgress -= 0.05;
            }

            // Zabezpieczenie, żeby progres nie wyszedł poza 0.0 - 1.0
            hoverProgress = Math.max(0.0, Math.min(1.0, hoverProgress));

            return; // Ważne: Blokujemy kod fizyki i czołgów, wychodząc z metody!
        }

        // 1. SYSTEM TUR I STEROWANIE
        if (missiles.isEmpty() && !player1.isDead() && !player2.isDead()) {

            // TURA GRACZA 1
            if (isPlayer1Turn) {
                if (aPressed) player1.moveLeft(terrain);
                if (dPressed) player1.moveRight(terrain, WIDTH);
                if (wPressed) player1.aimUp();
                if (sPressed) player1.aimDown();

                if (shiftPressed) {
                    Missile newMissile = new Missile(player1.getShootX(), player1.getShootY(), player1.getAbsoluteBarrelAngle(), 10);
                    missiles.add(newMissile);
                    shiftPressed = false;
                    isPlayer1Turn = false; // Zmiana tury!
                    player2.resetFuel(); // dodanie paliwa gracza 2
                }
            }
            // TURA GRACZA 2
            else {
                if (leftPressed) player2.moveLeft(terrain);
                if (rightPressed) player2.moveRight(terrain, WIDTH);
                if (upPressed) player2.aimUp();
                if (downPressed) player2.aimDown();

                if (enterPressed) {
                    Missile newMissile = new Missile(player2.getShootX(), player2.getShootY(), player2.getAbsoluteBarrelAngle(), 10);
                    missiles.add(newMissile);
                    enterPressed = false;
                    isPlayer1Turn = true; // Zmiana tury!
                    player1.resetFuel(); // dodanie paliwa gracza 1
                }
            }
        }

        // 2. FIZYKA CZOŁGÓW
        player1.update(terrain);
        player2.update(terrain);

        // smierc w przepasci
        if(player1.getY() > HEIGHT) {
            player1.dieInstantly();
        }
        if(player2.getY() > HEIGHT) {
            player2.dieInstantly();
        }

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
            if (m.hasCollidedWithGround(terrain)) {
                // Pocisk uderzył w ziemię -> robimy krater o promieniu 40 pikseli!
                terrain.createCrater(m.getX(), m.getY(), 40);
                missilesToRemove.add(m);
            } else if (m.getY() > HEIGHT || m.getX() < 0 || m.getX() > WIDTH) {
                // Pocisk wyleciał za ekran (w kosmos) -> usuwamy go, żeby oddać turę drugiemu graczowi
                missilesToRemove.add(m);
            }
        }
        // fizycznie usuwamy wszystkie trafione i zniszczone pociski z gry
        missiles.removeAll(missilesToRemove);

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
        terrain = new Terrain((int) WIDTH);
        missiles.clear();
        isPlayer1Turn = true;
        player1.resetState(100, 100);
        player2.resetState(600, 100);

        roundEnded = false;
    }

    // Metoda do rysowania klatki na ekranie
    private void draw(GraphicsContext gc) {

        if (state == GameState.MENU) {
            drawMenu(gc);
            return; // Kończymy rysowanie, żeby nie rysować mapy i czołgów pod menu!
        }

        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        terrain.draw(gc);

        // ==========================================
        // SCOREBOARD (Tablica wyników na środku)
        // ==========================================
        gc.setFill(Color.rgb(40, 40, 40)); // Ciemnoszary
        gc.setFont(javafx.scene.text.Font.font("Impact", javafx.scene.text.FontWeight.BOLD, 46));

        // Zależnie od tego, jakie masz wymiary ekranu, WIDTH/2 - 45 powinno być na środku
        gc.fillText(scorePlayer1 + " : " + scorePlayer2, WIDTH / 2 - 45, 50);

        // ==========================================
        // NAPISY GRACZY I ANIMOWANE STRZAŁKI (Pod paliwem)
        // ==========================================
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 20));

        // Matematyczna magia dla płynnej animacji strzałki
        // Math.sin z czasu zwraca wartość płynnie falującą od -1.0 do 1.0. Mnożymy to razy 6 pikseli wychylenia.
        double time = System.currentTimeMillis() / 150.0;
        double bounceOffset = Math.sin(time) * 6;

        // --- GRACZ 1 ---
        gc.setFill(Color.DARKBLUE);
        gc.fillText("GRACZ 1", 20, 95);
        // Jeśli jest tura gracza 1 i nikt nie zginął - rysuj strzałkę!
        if (isPlayer1Turn && !roundEnded) {
            gc.fillText("◀", 115 + bounceOffset, 95); // Strzałka celuje w napis i pulsuje
        }

        // --- GRACZ 2 ---
        gc.setFill(Color.DARKRED);
        gc.fillText("GRACZ 2", WIDTH - 110, 95);
        if (!isPlayer1Turn && !roundEnded) {
            gc.fillText("▶", WIDTH - 145 - bounceOffset, 95); // Strzałka celuje w napis i pulsuje
        }

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


        // RYSOWANIE SERDUSZEK

        // Życia Gracza 1 (lewy górny róg)
        for (int i = 0; i < player1.getLives(); i++) {
            gc.drawImage(heartImage, 20 + (i * 35), 20);
        }

        // Życia Gracza 2 (prawy górny róg)
        for (int i = 0; i < player2.getLives(); i++) {
            gc.drawImage(heartImage, (WIDTH - 50) - (i * 35), 20);
        }

        // PASEK PALIWA

        // GRACZ 1
        if (!player1.isDead()) {
            // 1. TŁO I CZARNE OBRAMOWANIE
            gc.setStroke(Color.rgb(20, 20, 20)); // Ciemnoszary / czarny kolor obramowania
            gc.setLineWidth(2.0); // Lekka grubość

            // Rysujemy obramowanie prostokąta, który ma rozmiar MAX_FUEL
            gc.strokeRect(20, 60, player1.getMaxFuel(), 10);

            // Wypełniamy tło (czarne, półprzezroczyste, żeby nie zasłaniało terenu)
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(20, 60, player1.getMaxFuel(), 10);

            // 2. OBLICZANIE KOLORU GRADIENTU (Z Javy 1.0 -> 0.0)
            double perc1 = player1.getFuel() / player1.getMaxFuel();
            Color c1;
            if (isPlayer1Turn) {
                // MAGIA HSB: Gradient zielony (120) -> pomarańczowy (60) -> czerwony (0)
                c1 = Color.hsb(perc1 * 120, 1.0, 1.0);
            } else {
                c1 = Color.GRAY; // Nieaktywny
            }

            // 3. WYPEŁNIENIE AKTYWNEGO PALIWA
            gc.setFill(c1);
            gc.fillRect(20, 60, player1.getFuel(), 10);
        }

        // GRACZ 2 (Prawa góra)
        if (!player2.isDead()) {
            // Obliczamy punkt startowy (lewy róg paska) tak, żeby zmieścił się MAX_FUEL od prawej krawędzi
            // Przykładowo: 800 (Width) - 20 (Margin) - 200 (MAX_FUEL) = 580
            double startX2 = (WIDTH - 20) - player2.getMaxFuel();

            // 1. TŁO I CZARNE OBRAMOWANIE (MAX_FUEL)
            gc.setStroke(Color.rgb(20, 20, 20));
            gc.setLineWidth(2.0);
            gc.strokeRect(startX2, 60, player2.getMaxFuel(), 10);

            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(startX2, 60, player2.getMaxFuel(), 10);

            // 2. OBLICZANIE KOLORU (Identyczna matematyka HSB)
            double perc2 = player2.getFuel() / player2.getMaxFuel();
            Color c2;
            if (!isPlayer1Turn) {
                c2 = Color.hsb(perc2 * 120, 1.0, 1.0);
            } else {
                c2 = Color.GRAY;
            }

            // 3. WYPEŁNIENIE PALIWA (Napełnia się klasycznie od startX2 w PRAWO, używając dodatniej szerokości)
            gc.setFill(c2);
            // Używamy gc.getFuel(), czyli dodatniej wartości. Pasek będzie maleć w LEWĄ stronę (odkrywać czarne tło po prawej).
            gc.fillRect(startX2, 60, player2.getFuel(), 10);
        }

        // EKRAN KOŃCOWY (GAME OVER)

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

    private void drawMenu(GraphicsContext gc) {
        // Tło
        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Tytuł
        gc.setFill(Color.DARKRED);
        gc.setFont(javafx.scene.text.Font.font("Impact", javafx.scene.text.FontWeight.BOLD, 80));
        gc.fillText("TANK WARS", WIDTH / 2 - 180, 150);

        // 1. PŁYNNA ZMIANA ROZMIARU (Mnożymy przyrost przez nasz "progress")
        // Maksymalnie przycisk urośnie o 30px na szerokość i 15px na wysokość
        double currentWidth = startBtnWidth + (30 * hoverProgress);
        double currentHeight = startBtnHeight + (15 * hoverProgress);

        // Zawsze na środku
        double currentX = startBtnX - ((currentWidth - startBtnWidth) / 2);
        double currentY = startBtnY - ((currentHeight - startBtnHeight) / 2);

        // 2. PŁYNNE PRZEJŚCIE KOLORU
        Color startColor = Color.ORANGE;
        Color hoverColor = Color.DARKORANGE;
        // Metoda interpolate sama miesza kolory w zależności od progressu (0.0 - 1.0)!
        gc.setFill(startColor.interpolate(hoverColor, hoverProgress));

        gc.fillRoundRect(currentX, currentY, currentWidth, currentHeight, 25, 25);

        // Obramowanie
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeRoundRect(currentX, currentY, currentWidth, currentHeight, 25, 25);

        // 3. PŁYNNA ZMIANA CZCIONKI
        gc.setFill(Color.WHITE);
        // Czcionka płynnie rośnie z 40 do 48
        double fontSize = 40 + (8 * hoverProgress);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));

        // Płynne centrowanie tekstu (też zależy od progressu)
        double textOffset = 65 + (5 * hoverProgress);
        gc.fillText("START", WIDTH / 2 - textOffset, startBtnY + 54 + (3 * hoverProgress));
    }

}