package com.example.tank_battle.control;

import com.example.tank_battle.TankApplication;
import com.example.tank_battle.model.Avatar;
import com.example.tank_battle.model.AvatarDAO;
import com.example.tank_battle.model.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreController implements Initializable {

    @FXML
    private TableView<AvatarDAO> tableWins;

    @FXML
    private TableColumn<AvatarDAO, String> colName;

    @FXML
    private TableColumn<AvatarDAO, Integer> colWins;

    private ObservableList<AvatarDAO> list;

    public ScoreController(){

        list = FXCollections.observableArrayList();
        list.addAll(Game.getInstance().getDaos());

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colName.setCellValueFactory(new PropertyValueFactory<AvatarDAO, String>("name"));
        colWins.setCellValueFactory(new PropertyValueFactory<AvatarDAO, Integer>("wins"));

        tableWins.setItems(list);

    }

    public void playAgain(){

        TankApplication.open("map.fxml");

        Stage stage = (Stage) tableWins.getScene().getWindow();
        stage.close();

    }

}
