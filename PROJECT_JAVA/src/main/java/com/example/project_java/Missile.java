package com.example.project_java;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Missile extends GameObject implements Collidable{
    private double velocityX;
    private double velocityY;
    private final double GRAVITY = 0.2;

    private static Image missileImage;
    static{
        try{
            missileImage = new Image("file:assets/missile.png");
        } catch (Exception e){
            System.err.println("Failed to load Missile Image");
        }
    }
    //konstruktor przyjmuje pozycje startowa, kat lufy i sile strzalu
    public Missile(double startX, double startY, double angle, double power){
        super(startX, startY);

        //funkcje tryg. wymagaja w javie radianow
        double radians = Math.toRadians(angle);

        //wektory predkosci na podstawie kata i sily
        this.velocityX = Math.cos(radians) * power;
        this.velocityY = Math.sin(radians) * power;
    }

    @Override
    public void update(Terrain terrain){
        this.velocityY += GRAVITY; //grawitacja z kazda klatka zwieksza predkosc opadania

        //aktualizacja wspolrzednych pocisku
        this.x += velocityX;
        this.y += velocityY;
    }

    public void draw(GraphicsContext gc){
        if(missileImage == null){
            return;
        }
        gc.save();
        gc.translate(this.x, this.y);
        double radians = Math.atan2(velocityY, velocityX);
        gc.rotate(Math.toRadians(radians));
        gc.drawImage(missileImage, -4, -4, 8, 8);
        gc.restore();

    }
    @Override
    public boolean hasCollidedWithGround(Terrain terrain){
        if(this.x < 0 || this.x >= 800){
            return true;
        }
        double groundY = terrain.getHeight((int) this.x);

        return this.y >=groundY;
    }
    public boolean hasCollidedWithTank(Tank enemy) {
        // liczenie odleglosci miedzy pociskiem a srodkiem czolgu
        double dx = this.x - enemy.getCenterX();
        double dy = this.y - enemy.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < 20;
    }

}
