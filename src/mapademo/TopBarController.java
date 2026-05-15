package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class TopBarController {

    @FXML private HBox windowBar;
    @FXML private Button btnMaximize;
    
    private double xOffset = 0;
    private double yOffset = 0;
    private double lastX, lastY, lastWidth, lastHeight;
    private boolean isMaximized = false;

    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) windowBar.getScene().getWindow();
        if (isMaximized) {
            // Si arrastramos mientras está maximizado, restauramos
            handleMaximize();
            return;
        }
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) windowBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) windowBar.getScene().getWindow();
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        if (!isMaximized) {
            // Guardar estado previo
            lastX = stage.getX();
            lastY = stage.getY();
            lastWidth = stage.getWidth();
            lastHeight = stage.getHeight();

            // Maximizar respetando la barra de tareas
            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
            
            btnMaximize.setText("🗗"); // Icono de restaurar (dos cuadrados)
            isMaximized = true;
        } else {
            // Restaurar estado previo
            stage.setX(lastX);
            stage.setY(lastY);
            stage.setWidth(lastWidth);
            stage.setHeight(lastHeight);
            
            btnMaximize.setText("⬜"); // Icono de maximizar
            isMaximized = false;
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) windowBar.getScene().getWindow();
        stage.close();
    }
}
