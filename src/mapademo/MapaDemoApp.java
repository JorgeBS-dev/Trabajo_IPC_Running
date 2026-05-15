/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapademo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author jose
 */
public class MapaDemoApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.UNDECORATED);
        Navigation.setStage(stage);
        Navigation.loadScene("InicioSesionV2.fxml", "Running la Safor - Inicio de Sesión");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
