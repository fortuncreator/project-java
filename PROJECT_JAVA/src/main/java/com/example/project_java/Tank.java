package com.example.project_java;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tank extends GameObject {
    @Override
    public void update(Terrain terrain) {
        // srodek czolgu w poziomie
        double centerX = this.x + (tankImage.getWidth() / 2);

        // wysokosc trawy pod srodkowym punktem
        double groundY = terrain.getHeight((int) centerX);

        // grawitacja - jak jest w powietrzu to spada
        double tankBottom = this.y + tankImage.getHeight();
        if (tankBottom < groundY) {
            this.y += 2; // spada
        } else {
            this.y = groundY - tankImage.getHeight(); // stoi na ziemi
        }

        // kat na podstawie lewej i prawej krawedzi (tylko do grafiki)
        double groundYLeft = terrain.getHeight((int) this.x);
        double groundYRight = terrain.getHeight((int) (this.x + tankImage.getWidth()));
        // roznica poziomow
        double dy = groundYRight - groundYLeft;
        // odleglosc miedzy krawedziami
        double dx = tankImage.getWidth();
        // kat pochylenia za pomoca arctg
        this.angle = Math.toDegrees(Math.atan2(dy, dx));
    }

    // lufa i jej nachylenie
    private Image barrelImage;
    private double barrelAngle = 0;

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        // przesuniecie plotna na srodek-dol czolgu - tam gdzie gasienice dotykaja ziemi
        double pivotX = this.x + (tankImage.getWidth() / 2);
        double pivotY = this.y + tankImage.getHeight();
        gc.translate(pivotX, pivotY);

        // obrot
        gc.rotate(this.angle);

        // skoro nasz srodek (0,0) jest teraz na dole pośrodku,
        // rysujemy obrazek przesuniety w lewo o pol szerokosci i w gore o cala wysokosc
        gc.drawImage(tankImage, -(tankImage.getWidth() / 2), -tankImage.getHeight());

            // wstawienie lufy
            gc.save();

            gc.translate(6, -tankImage.getHeight() + 5);
            gc.rotate(this.barrelAngle);
            gc.drawImage(barrelImage, 0, -(barrelImage.getHeight() / 2));
            gc.restore();

        gc.restore();
    }

    protected double angle = 0;

    private Image tankImage;

    public Tank(double startX, double startY) {
        super(startX, startY); // wywolanie konstruktora GameObject(startX, startY)
        tankImage = new Image("file:assets/tank1.png");
        barrelImage = new Image("file:assets/barrel.png");
    }

    // maksymalne wartosci nachylenia lufy
    private final double MIN_ANGLE = -80;
    private final double MAX_ANGLE = 20;

    // podnoszenie i opuszczanie lufy
    public void aimUp(){
        this.barrelAngle -= 1;
        if(this.barrelAngle < MIN_ANGLE){
            this.barrelAngle = MIN_ANGLE;
        }
    }
    public void aimDown(){
        this.barrelAngle += 1;
        if(this.barrelAngle > MAX_ANGLE){
            this.barrelAngle = MAX_ANGLE;
        }
    }
    // ruch czolgu
    public void moveLeft(){
        this.x -= 2;
    }
    public void moveRight(){
        this.x += 2;
    }

    public double getAbsoluteBarrelAngle() {
        return this.angle + this.barrelAngle;
    }

    public double getShootX() {
        // 1. Środek obrotu czołgu
        double pivotX = this.x + (tankImage.getWidth() / 2);
        double pivotY = this.y + tankImage.getHeight();

        // 2. Pozycja zawiasu (idealnie zgrana z Twoim gc.translate w metodzie draw!)
        double localHingeX = 6;
        double localHingeY = -tankImage.getHeight() + 5;

        // 3. Obliczamy pozycję zawiasu po przechyleniu czołgu
        double tankAngleRad = Math.toRadians(this.angle);
        double hingeX = pivotX + (localHingeX * Math.cos(tankAngleRad)) - (localHingeY * Math.sin(tankAngleRad));

        // 4. Pozycja końcówki lufy (odległość = szerokość Twojego obrazka lufy)
        double absAngleRad = Math.toRadians(getAbsoluteBarrelAngle());
        return hingeX + (barrelImage.getWidth() * Math.cos(absAngleRad));
    }

    public double getShootY() {
        double pivotX = this.x + (tankImage.getWidth() / 2);
        double pivotY = this.y + tankImage.getHeight();

        double localHingeX = 6;
        double localHingeY = -tankImage.getHeight() + 5;

        double tankAngleRad = Math.toRadians(this.angle);
        double hingeY = pivotY + (localHingeX * Math.sin(tankAngleRad)) + (localHingeY * Math.cos(tankAngleRad));

        double absAngleRad = Math.toRadians(getAbsoluteBarrelAngle());
        return hingeY + (barrelImage.getWidth() * Math.sin(absAngleRad));
    }
}