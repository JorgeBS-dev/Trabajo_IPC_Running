package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import upv.ipc.sportlib.SportActivityApp;

public class LoginController {

    @FXML private HBox rootPane;
    @FXML private TextField identificadorField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label mensajeLabel;
    @FXML private ToggleButton togglePasswordButton;
    @FXML private VBox loginHelpBox;

    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        // Evitar que el primer campo tome el foco automáticamente
        javafx.application.Platform.runLater(() -> rootPane.requestFocus());

        identificadorField.focusedProperty().addListener((obs, oldV, newV) -> {
            loginHelpBox.setVisible(newV);
            loginHelpBox.setManaged(newV);
        });

        // Sincronizar PasswordField y TextField
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // Lógica de mostrar/ocultar
        togglePasswordButton.selectedProperty().addListener((obs, oldV, selected) -> {
            if (selected) {
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                togglePasswordButton.setText("🙈");
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                togglePasswordButton.setText("👁");
            }
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
