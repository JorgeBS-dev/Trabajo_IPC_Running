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
        
        // Navigation.stage.setWidth(visualBounds.getWidth());
        // Navigation.stage.setHeight(visualBounds.getHeight());
        
        // Establecer un tamaño mínimo por defecto
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
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
        
        ResizeHelper.addResizeListener(stage);
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
            
            ResizeHelper.addResizeListener(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
