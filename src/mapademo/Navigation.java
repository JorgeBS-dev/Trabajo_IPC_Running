package mapademo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class Navigation {
    private static Stage stage;

    public static void setStage(Stage stage) {
        Navigation.stage = stage;
        
        // Ajustar al tamaño de la pantalla respetando la barra de tareas
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(visualBounds.getMinX());
        stage.setY(visualBounds.getMinY());
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());
        
        // Set app icon
        try {
            Navigation.stage.getIcons().add(new Image(Navigation.class.getResourceAsStream("iconoapp2.png")));
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void showParent(Parent root, String title) {
        if (stage.getScene() == null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
        stage.setTitle(title);
        stage.show();
        
        // Reforzar el tamaño en cada cambio de escena
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());
    }

    public static void loadScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlFile));
            Parent root = loader.load();
            if (stage.getScene() == null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }
            stage.setTitle(title);
            stage.show();
            
            // Reforzar el tamaño en cada cambio de escena
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
