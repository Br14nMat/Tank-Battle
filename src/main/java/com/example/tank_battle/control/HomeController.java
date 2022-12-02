package com.example.tank_battle.control;

import com.example.tank_battle.TankApplication;
import com.example.tank_battle.model.Game;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private TextField tfTank1;

    @FXML
    private TextField tfTank2;

    public void start(){
        Game.getInstance().setPlayer1(tfTank1.getText());
        Game.getInstance().setPlayer2(tfTank2.getText());
        Game.getInstance().launch();

        TankApplication.open("map.fxml");
        Stage stage = (Stage) tfTank1.getScene().getWindow();
        stage.close();

    }

}