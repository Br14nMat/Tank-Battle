package com.example.tank_battle.control;

import com.example.tank_battle.TankApplication;
import com.example.tank_battle.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private boolean isRunning = true;


    //Elementos gráficos

    private List<Avatar> avatars;
    private List<Obstacle> obstacles;

    //Tank 1
    @FXML
    private Label nameTank1;
    private Avatar avatar1;
    private Image avatar1Img;

    @FXML
    private ProgressBar tankLife1;

    //Tank 2
    @FXML
    private Label nameTank2;
    private Avatar avatar2;
    private Image avatar2Img;

    @FXML
    private ProgressBar tankLife2;

    //CPU

    private Avatar cpu;
    private Image avatar3Img;

    @FXML
    private ProgressBar tankLife3;

    //Tank 1
    boolean Wpressed = false;
    boolean Apressed = false;
    boolean Spressed = false;
    boolean Dpressed = false;

    //Tank 2
    boolean UPPressed = false;
    boolean DOWNPressed = false;
    boolean RIGHTPressed = false;
    boolean LEFTPressed = false;

    //Config
    public static final int RELOAD_FACTOR = 10;

    public MapController(){
        avatar1Img = new Image("file:"+ TankApplication.class.getResource("tank1.png").getPath());
        avatar2Img = new Image("file:"+ TankApplication.class.getResource("tank2.png").getPath());
        avatar3Img = new Image("file:"+ TankApplication.class.getResource("tank3.png").getPath());

        avatars = new ArrayList<>();
        obstacles = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);

        avatar1 = new Avatar(
                Game.getInstance().getPlayer1(),
                canvas,
                avatar1Img,
                Color.YELLOW,
                new Vector(50, 50),
                new Vector(2, 2));

        avatar2 = new Avatar(
                Game.getInstance().getPlayer2(),
                canvas,
                avatar2Img,
                Color.BLUE,
                new Vector(canvas.getWidth() - 50, canvas.getHeight() - 50),
                new Vector(-2, -2));

        cpu = new Avatar(
                "CPU",
                canvas,
                avatar3Img,
                Color.RED,
                new Vector(50, canvas.getHeight() - 100),
                new Vector(2, -2)
        );

        avatars.add(avatar1);
        avatars.add(avatar2);
        avatars.add(cpu);

        Game.getInstance().setAvatars(avatars);

        setUpWalls();

        nameTank1.setText(avatar1.getName());
        nameTank2.setText(avatar2.getName());

        tankLife1.setProgress(1);
        tankLife2.setProgress(1);
        tankLife3.setProgress(1);

        cpuAI();

        draw();
    }

    public void draw() {

        new Thread(
                () -> {
                    while (isRunning) {
                        Platform.runLater(() -> {
                            gc.setFill(Color.BLACK);
                            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                            drawObstacles();

                            renderAvatar(avatar1, tankLife1);
                            renderAvatar(avatar2, tankLife2);
                            renderAvatar(cpu, tankLife3);

                            int count = 0;
                            for (Avatar avatar : avatars) {
                                if(!avatar.isAlive) count++;
                            }

                            if(count == 2){
                                isRunning = false;
                                for (Avatar avatar : avatars)
                                    if(avatar.isAlive)
                                        Game.getInstance().winner(avatar);

                                Game.getInstance().update();
                            }

                            doKeyboardActions();

                            if(!isRunning){
                                TankApplication.open("score.fxml");
                                Stage stage = (Stage) canvas.getScene().getWindow();
                                stage.close();
                            }

                        });
                        //Sleep
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
        ).start();


    }

    private void doKeyboardActions() {

        int angle = 4;

        //Tank 1
        if (Wpressed && !detectCollisionForward(avatar1))
            avatar1.moveForward();

        if (Spressed && !detectCollisionBackward(avatar1))
            avatar1.moveBackward();

        if (Apressed) avatar1.changeAngle(-angle);
        if (Dpressed) avatar1.changeAngle(angle);

        //Tank 2

        if(UPPressed && !detectCollisionForward(avatar2))
            avatar2.moveForward();

        if (DOWNPressed && !detectCollisionBackward(avatar2))
            avatar2.moveBackward();

        if (LEFTPressed) avatar2.changeAngle(-angle);
        if (RIGHTPressed) avatar2.changeAngle(angle);

    }

    private void onKeyReleased(KeyEvent keyEvent) {

        //Tank 1
        if (keyEvent.getCode() == KeyCode.W) Wpressed = false;
        if (keyEvent.getCode() == KeyCode.A) Apressed = false;
        if (keyEvent.getCode() == KeyCode.S) Spressed = false;
        if (keyEvent.getCode() == KeyCode.D) Dpressed = false;

        //Tank 2
        if(keyEvent.getCode() == KeyCode.UP) UPPressed = false;
        if(keyEvent.getCode() == KeyCode.DOWN) DOWNPressed = false;
        if(keyEvent.getCode() == KeyCode.LEFT) LEFTPressed = false;
        if(keyEvent.getCode() == KeyCode.RIGHT) RIGHTPressed = false;

    }

    private void onKeyPressed(KeyEvent keyEvent) {

        //Tank 1
        if (keyEvent.getCode() == KeyCode.W) Wpressed = true;
        if (keyEvent.getCode() == KeyCode.A) Apressed = true;
        if (keyEvent.getCode() == KeyCode.S) Spressed = true;
        if (keyEvent.getCode() == KeyCode.D) Dpressed = true;
        if (keyEvent.getCode() == KeyCode.R) avatar1.reload();

        if (keyEvent.getCode() == KeyCode.SPACE)
            if(avatar1.hasBullets()) avatar1.shoot();


        //Tank 2
        if (keyEvent.getCode() == KeyCode.UP) UPPressed = true;
        if (keyEvent.getCode() == KeyCode.DOWN) DOWNPressed = true;
        if (keyEvent.getCode() == KeyCode.LEFT) LEFTPressed = true;
        if (keyEvent.getCode() == KeyCode.RIGHT) RIGHTPressed = true;
        if (keyEvent.getCode() == KeyCode.SHIFT) avatar2.reload();

        if (keyEvent.getCode() == KeyCode.CONTROL)
            if(avatar2.hasBullets()) avatar2.shoot();

    }

    private void setUpWalls(){

        Obstacle obstacle1 = new Obstacle(canvas, 150, 150);
        Obstacle obstacle2 = new Obstacle(canvas, 150, obstacle1.bounds.getY() + obstacle1.HEIGHT);
        Obstacle obstacle3 = new Obstacle(canvas, 150, obstacle2.bounds.getY() + obstacle1.HEIGHT);
        Obstacle obstacle4 = new Obstacle(canvas, 190, obstacle1.bounds.getX());
        Obstacle obstacle5 = new Obstacle(canvas, 230, obstacle1.bounds.getX());

        Obstacle obstacle6 = new Obstacle(canvas,150,450);
        Obstacle obstacle7 = new Obstacle(canvas, 150, 410);
        Obstacle obstacle8 = new Obstacle(canvas, 150, 370);
        Obstacle obstacle9 = new Obstacle(canvas, 190, 450);
        Obstacle obstacle10 = new Obstacle(canvas, 230, 450);

        Obstacle obstacle11 = new Obstacle(canvas, 604, 150);
        Obstacle obstacle12 = new Obstacle(canvas, 604, 190);
        Obstacle obstacle13 = new Obstacle(canvas, 604, 230);
        Obstacle obstacle14 = new Obstacle(canvas, 564, 150);
        Obstacle obstacle15 = new Obstacle(canvas, 524, 150);

        Obstacle obstacle16 = new Obstacle(canvas, 604, 450);
        Obstacle obstacle17 = new Obstacle(canvas, 604, 410);
        Obstacle obstacle18 = new Obstacle(canvas, 604, 370);
        Obstacle obstacle19 = new Obstacle(canvas, 564, 450);
        Obstacle obstacle20 = new Obstacle(canvas, 524, 450);

        Obstacle obstacle21 = new Obstacle(canvas, 377, 300);
        Obstacle obstacle22 = new Obstacle(canvas, 377, 260);
        Obstacle obstacle23 = new Obstacle(canvas, 377, 340);
        Obstacle obstacle24 = new Obstacle(canvas, 337, 300);
        Obstacle obstacle25 = new Obstacle(canvas, 417, 300);





        obstacles.add(obstacle1);
        obstacles.add(obstacle2);
        obstacles.add(obstacle3);
        obstacles.add(obstacle4);
        obstacles.add(obstacle5);
        obstacles.add(obstacle6);
        obstacles.add(obstacle7);
        obstacles.add(obstacle8);
        obstacles.add(obstacle9);
        obstacles.add(obstacle10);
        obstacles.add(obstacle11);
        obstacles.add(obstacle12);
        obstacles.add(obstacle13);
        obstacles.add(obstacle14);
        obstacles.add(obstacle15);
        obstacles.add(obstacle16);
        obstacles.add(obstacle17);
        obstacles.add(obstacle18);
        obstacles.add(obstacle19);
        obstacles.add(obstacle20);
        obstacles.add(obstacle21);
        obstacles.add(obstacle22);
        obstacles.add(obstacle23);
        obstacles.add(obstacle24);
        obstacles.add(obstacle25);

    }

    private void drawObstacles(){
        for(int i = 0; i < obstacles.size(); i++){
            obstacles.get(i).draw();
        }
    }

    private void renderAvatar(Avatar avatar, ProgressBar life){
        if(avatar.isAlive){
            avatar.draw();
            avatar.manageBullets(avatars);
            life.setProgress((double) avatar.getHearts() / 5.0);
        }else
            life.setProgress(0);
    }

    public boolean detectCollisionForward(Avatar avatar){

        for(int i = 0; i < obstacles.size(); i++){

            if(obstacles.get(i).bounds.intersects(avatar.pos.x + avatar.direction.x - 25, avatar.pos.y + avatar.direction.y - 25, 50, 50))
                return true;

        }

        return false;

    }

    public boolean detectCollisionBackward(Avatar avatar){

        for(int i = 0; i < obstacles.size(); i++){

            if(obstacles.get(i).bounds.intersects(avatar.pos.x - avatar.direction.x - 25, avatar.pos.y - avatar.direction.y - 25, 50, 50))
                return true;

        }

        return false;

    }

    private void cpuAI(){

        new Thread(()->{

            while (cpu.isAlive){

                if(!cpu.hasBullets()) cpu.reload();

                //(1-3)
                int random = (int)(Math.random()*(3-1+1)+1);

                if(random == 1){
                    for (int i = 0; i < 20; i++){
                        if(!detectCollisionForward(cpu))
                            cpu.moveForward();
                        else
                            cpu.changeAngle(180);
                    }
                }

                if(random == 2){
                    cpu.changeAngle(-25);
                    cpu.shoot();
                }

                if(random == 3){
                    cpu.changeAngle(25);
                    cpu.shoot();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();

    }

}