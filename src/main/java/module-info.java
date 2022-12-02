module com.example.tank_battle {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tank_battle to javafx.fxml;
    exports com.example.tank_battle;
    exports com.example.tank_battle.control;
    opens com.example.tank_battle.control to javafx.fxml;
    opens com.example.tank_battle.model to javafx.base;
}