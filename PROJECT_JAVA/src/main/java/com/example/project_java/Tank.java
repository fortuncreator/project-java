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

        gc.restore();
    }

    protected double angle = 0;

    private Image tankImage;

    public Tank(double startX, double startY) {
        super(startX, startY); // wywolanie konstruktora GameObject(startX, startY)
        tankImage = new Image("file:assets/tank1.png");

    }

    public void moveLeft(){
        this.x -= 2;
    }
    public void moveRight(){
        this.x += 2;
    }
}