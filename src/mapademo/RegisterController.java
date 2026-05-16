package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import java.io.File;
import java.time.LocalDate;

public class RegisterController {

    @FXML private VBox rootPane;
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField passwordTextField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private ToggleButton togglePasswordButton;
    @FXML private ToggleButton toggleConfirmPasswordButton;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label mensajeLabel;
    @FXML private ImageView avatarImageView;
    @FXML private Label emailStatus;

    @FXML private VBox nickRequirementsBox;
    @FXML private Label nickLenLabel;
    @FXML private Label nickCharLabel;

    @FXML private VBox passRequirementsBox;
    @FXML private Label passLenLabel;
    @FXML private Label passUpperLabel;
    @FXML private Label passLowerLabel;
    @FXML private Label passDigitLabel;
    @FXML private Label passSymbolLabel;

    private String avatarPath = "";
    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        // Evitar que el primer campo tome el foco automáticamente
        javafx.application.Platform.runLater(() -> rootPane.requestFocus());

        // Nickname focus and validation
        nicknameField.focusedProperty().addListener((obs, oldV, newV) -> {
            nickRequirementsBox.setVisible(newV);
            nickRequirementsBox.setManaged(newV);
        });

        nicknameField.textProperty().addListener((obs, oldV, newV) -> {
            validateNickname(newV);
        });

        // Email validation
        emailField.textProperty().addListener((obs, oldV, newV) -> {
            if (User.checkEmail(newV)) {
                emailStatus.setText("✅");
                emailStatus.setTextFill(Color.GREEN);
            } else {
                emailStatus.setText("❌");
                emailStatus.setTextFill(Color.RED);
            }
        });

        // Password focus and validation
        passRequirementsBox.visibleProperty().bind(
            passwordField.focusedProperty().or(passwordTextField.focusedProperty())
        );
        passRequirementsBox.managedProperty().bind(passRequirementsBox.visibleProperty());

        passwordField.textProperty().addListener((obs, oldV, newV) -> {
            validatePassword(newV);
        });

        // Sincronizar PasswordField y TextField
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // Lógica de mostrar/ocultar contraseña
        togglePasswordButton.selectedProperty().addListener((obs, oldV, selected) -> {
            if (selected) {
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                passwordTextField.requestFocus();
                togglePasswordButton.setText("🙈");
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
                passwordField.requestFocus();
                togglePasswordButton.setText("👁");
            }
        });

        toggleConfirmPasswordButton.selectedProperty().addListener((obs, oldV, selected) -> {
            if (selected) {
                confirmPasswordField.setVisible(false);
                confirmPasswordField.setManaged(false);
                confirmPasswordTextField.setVisible(true);
                confirmPasswordTextField.setManaged(true);
                confirmPasswordTextField.requestFocus();
                toggleConfirmPasswordButton.setText("🙈");
            } else {
                confirmPasswordField.setVisible(true);
                confirmPasswordField.setManaged(true);
                confirmPasswordTextField.setVisible(false);
                confirmPasswordTextField.setManaged(false);
                confirmPasswordField.requestFocus();
                toggleConfirmPasswordButton.setText("👁");
            }
        });
    }

    private void validateNickname(String nick) {
        boolean lenOk = nick.length() >= 6 && nick.length() <= 15;
        boolean charOk = nick.matches("[a-zA-Z0-9\\-_]+");

        updateLabel(nickLenLabel, lenOk, "• 6-15 caracteres");
        updateLabel(nickCharLabel, charOk, "• Letras, números, - o _");
    }

    private void validatePassword(String pass) {
        boolean lenOk = pass.length() >= 8 && pass.length() <= 20;
        boolean upperOk = pass.chars().anyMatch(Character::isUpperCase);
        boolean lowerOk = pass.chars().anyMatch(Character::isLowerCase);
        boolean digitOk = pass.chars().anyMatch(Character::isDigit);
        boolean symbolOk = pass.matches(".*[!@#$%&*()\\-+=].*");

        updateLabel(passLenLabel, lenOk, "• 8-20 caracteres");
        updateLabel(passUpperLabel, upperOk, "• Una mayúscula");
        updateLabel(passLowerLabel, lowerOk, "• Una minúscula");
        updateLabel(passDigitLabel, digitOk, "• Un dígito");
        updateLabel(passSymbolLabel, symbolOk, "• Un símbolo (!@#$%&*()-+=)");
    }

    private void updateLabel(Label label, boolean ok, String baseText) {
        if (ok) {
            label.setText("✅ " + baseText);
            label.setTextFill(Color.GREEN);
        } else {
            label.setText("❌ " + baseText);
            label.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleRegister() {
        String nick = nicknameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();
        LocalDate birthDate = birthDatePicker.getValue();

        if (nick.isEmpty() || email.isEmpty() || pass.isEmpty() || birthDate == null) {
            mensajeLabel.setText("Por favor, rellena los campos obligatorios.");
            return;
        }

        if (!pass.equals(confirmPass)) {
            mensajeLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        if (!User.checkNickName(nick) || !User.checkEmail(email) || !User.checkPassword(pass) || !User.isOlderThan(birthDate, 12)) {
            mensajeLabel.setText("Comprueba los requisitos marcados en rojo.");
            return;
        }

        boolean ok = app.registerUser(nick, email, pass, birthDate, avatarPath);
        if (ok) {
            Navigation.loadScene("InicioSesionV2.fxml", "Running la Safor - Inicio de Sesión");
        } else {
            mensajeLabel.setText("Error en el registro. ¿Nickname ya en uso?");
        }
    }

    @FXML
    private void handleUploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Avatar");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            avatarPath = selectedFile.getAbsolutePath();
            avatarImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleGoToLogin() {
        Navigation.loadScene("InicioSesionV2.fxml", "Running la Safor - Inicio de Sesión");
    }
}
