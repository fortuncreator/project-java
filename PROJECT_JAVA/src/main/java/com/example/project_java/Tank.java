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
    private double barrelOffsetX; // przechowuje informacje z ktorej strony jest zawias lufy

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

            gc.translate(this.barrelOffsetX, -tankImage.getHeight() + 5);
            gc.rotate(this.barrelAngle);
            gc.drawImage(barrelImage, 0, -(barrelImage.getHeight() / 2));
            gc.restore();

        gc.restore();
    }

    protected double angle = 0;

    private Image tankImage;
    private int lives = 3; // start z 3 zyciami
    private double fuel = 200; // start z pelnym bakiem
    private double MAX_FUEL = 200;
    private double MIN_FUEL = 0;

    public Tank(double startX, double startY, String imagePath, double startBarrelAngle, double barrelOffsetX) {
        super(startX, startY); // wywolanie konstruktora GameObject(startX, startY)
        tankImage = new Image(imagePath);
        barrelImage = new Image("file:assets/barrel.png");

        this.barrelAngle = startBarrelAngle;
        this.barrelOffsetX = barrelOffsetX; // zapis przesuniecia zawiasu na lufe

        // sprawdzam czy czolg patrzy w prawo na podstawie kata poczatkowego
        this.facingRight = (startBarrelAngle == 0);

        if(this.facingRight) {
            this.MIN_ANGLE = startBarrelAngle - 80; // w gore dla prawego czolgu
            this.MAX_ANGLE = startBarrelAngle + 20; // w dol dla prawego
        }
        else{
            this.MIN_ANGLE = startBarrelAngle - 20; // w dol dla lewego czolgu
            this.MAX_ANGLE = startBarrelAngle + 80; // w gore dla lewego
        }
    }

    public void takeDamage(){
        this.lives--;
        System.out.println("Czolg oberwal, zostalo zyc:" + this.lives);
    }
    public int getLives() {
        return this.lives;
    }
    public boolean isDead() {
        return this.lives <= 0;
    }
    // hitboxy ( zwraca srodek czolgu do obliczen kolizji )
    public double getCenterX() {
        return this.x + (tankImage.getWidth() / 2);
    }
    public double getCenterY() {
        return this.y + (tankImage.getHeight() / 2);
    }


    // maksymalne wartosci nachylenia lufy
    private double MIN_ANGLE;
    private double MAX_ANGLE;
    private boolean facingRight;

    // podnoszenie i opuszczanie lufy
    public void aimUp(){
        if(this.facingRight){
            this.barrelAngle -= 1; // prawy czolg podnosi lufe odejmujac
            if (this.barrelAngle < MIN_ANGLE) {
                this.barrelAngle = MIN_ANGLE;
            }
        }
        else{
            this.barrelAngle += 1;//lewy czolg podnosi lufe DODAJAC
            if(this.barrelAngle > MAX_ANGLE){
                this.barrelAngle = MAX_ANGLE;
            }
        }
    }
    public void aimDown(){
        if(this.facingRight){
            this.barrelAngle += 1;
            if (this.barrelAngle > MAX_ANGLE) {
                this.barrelAngle = MAX_ANGLE;
            }

        }
        else{
            this.barrelAngle -= 1;
            if(this.barrelAngle < MIN_ANGLE){
                this.barrelAngle = MIN_ANGLE;
            }
        }
    }
    // ruch czolgu
    public void moveLeft(Terrain terrain){
        if (this.fuel > 0){
            double nextX = this.x -2;
            //zabezpieczenie lewej krawedzi ekranu
            if(nextX < 0) return;
            //zabezpieczenie przed pionowa wspinaczka
            int leftBumper = 0;
            double currentY = terrain.getHeight((int) (this.x + leftBumper));
            double nextY = terrain.getHeight((int) (nextX + leftBumper));
            //jesli nastepny krok jest o wiecej niz 4 piksele wyzej to blokada
            if(currentY - nextY > 16) return;

            this.fuel -= 1.5; // spalanie paliwa
            this.x = nextX;
        }
    }
    public void moveRight(Terrain terrain, double screenWidth){
        if (this.fuel > 0) {
            double nextX = this.x +2;
            //zabezpieczenie prawej krawedzi ekranu
            if(nextX > screenWidth - 24) return;
            //zabezpieczenie przed pionowa wspinaczka
            int rightBumper = 16;
            double currentY = terrain.getHeight((int) (this.x) + rightBumper);
            double nextY = terrain.getHeight((int) (nextX +  rightBumper));
            //jesli nastepny krok jest o wiecej niz 4 piksele wyzej to blokada
            if(currentY - nextY > 16) return;

            this.fuel -= 1.5; // spalanie paliwa
            this.x = nextX;
        }
    }

    public double getAbsoluteBarrelAngle() {
        return this.angle + this.barrelAngle;
    }

    public double getShootX() {
        // 1. Środek obrotu czołgu
        double pivotX = this.x + (tankImage.getWidth() / 2);
        double pivotY = this.y + tankImage.getHeight();

        // 2. Pozycja zawiasu (idealnie zgrana z Twoim gc.translate w metodzie draw!)
        double localHingeX = this.barrelOffsetX;
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

        double localHingeX = this.barrelOffsetX;
        double localHingeY = -tankImage.getHeight() + 5;

        double tankAngleRad = Math.toRadians(this.angle);
        double hingeY = pivotY + (localHingeX * Math.sin(tankAngleRad)) + (localHingeY * Math.cos(tankAngleRad));

        double absAngleRad = Math.toRadians(getAbsoluteBarrelAngle());
        return hingeY + (barrelImage.getWidth() * Math.sin(absAngleRad));
    }

    public void resetFuel(){
        this.fuel = MAX_FUEL;
    }
    public double getFuel() {
        return this.fuel;
    }
    public double getMaxFuel() {
        return this.MAX_FUEL;
    }

    public void dieInstantly(){
        this.lives = 0;
        this.fuel = 0;
        System.out.println("Czolg spadl w przepasc!");
    }
}
