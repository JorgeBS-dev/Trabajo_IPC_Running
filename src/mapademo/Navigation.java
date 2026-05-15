package mapademo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Navigation {
    private static Stage stage;

    public static void setStage(Stage stage) {
        Navigation.stage = stage;
        Navigation.stage.setMaximized(true);
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
        stage.setMaximized(true);
        javafx.application.Platform.runLater(() -> stage.setMaximized(true));
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
            stage.setMaximized(true);
            javafx.application.Platform.runLater(() -> stage.setMaximized(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
