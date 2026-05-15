package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import upv.ipc.sportlib.SportActivityApp;

public class LoginController {

    @FXML private TextField identificadorField;
    @FXML private PasswordField passwordField;
    @FXML private Label mensajeLabel;
    @FXML private ToggleButton togglePasswordButton;
    @FXML private VBox loginHelpBox;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        identificadorField.focusedProperty().addListener((obs, oldV, newV) -> {
            loginHelpBox.setVisible(newV);
            loginHelpBox.setManaged(newV);
        });
    }

    @FXML
    private void handleLogin() {
        String id = identificadorField.getText();
        String pass = passwordField.getText();

        if (id.isEmpty() || pass.isEmpty()) {
            mensajeLabel.setText("Por favor, rellena todos los campos.");
            return;
        }

        boolean ok = app.login(id, pass);
        if (ok) {
            Navigation.loadScene("Dashboard.fxml", "Running la Safor - Dashboard");
        } else {
            mensajeLabel.setText("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void handleGoToRegister() {
        Navigation.loadScene("RegistroV2.fxml", "Running la Safor - Registro");
    }
}
