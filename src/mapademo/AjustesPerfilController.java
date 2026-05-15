package mapademo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import java.io.File;

public class AjustesPerfilController {

    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView avatarImageView;
    @FXML private Label mensajeLabel;
    @FXML private Button cambiarFotoButton;
    @FXML private Button guardarCambiosButton;
    @FXML private Button cerrarSesionButton;

    private SportActivityApp app = SportActivityApp.getInstance();
    private User currentUser;
    private String newAvatarPath = "";

    @FXML
    public void initialize() {
        currentUser = app.getCurrentUser();
        if (currentUser != null) {
            nicknameField.setText(currentUser.getNickName());
            nicknameField.setEditable(false); // Nickname cannot be changed in this library version
            nicknameField.setTooltip(new Tooltip("El nickname no puede ser modificado."));
            
            emailField.setText(currentUser.getEmail());
            if (currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
                File file = new File(currentUser.getAvatarPath());
                if (file.exists()) {
                    avatarImageView.setImage(new Image(file.toURI().toString()));
                }
            }
        }

        cambiarFotoButton.setOnAction(e -> handleUploadAvatar());
        guardarCambiosButton.setOnAction(e -> handleSave());
        cerrarSesionButton.setOnAction(e -> handleLogout());
    }

    private void handleUploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Avatar");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            newAvatarPath = selectedFile.getAbsolutePath();
            avatarImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void handleSave() {
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (email.isEmpty()) {
            mensajeLabel.setText("El email es obligatorio.");
            return;
        }

        if (!pass.isEmpty() && !pass.equals(confirm)) {
            mensajeLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        // Validate formats
        if (!User.checkEmail(email)) {
            mensajeLabel.setText("Formato de email incorrecto.");
            return;
        }

        if (!pass.isEmpty() && !User.checkPassword(pass)) {
            mensajeLabel.setText("Contraseña demasiado débil.");
            return;
        }

        String finalPass = pass.isEmpty() ? currentUser.getPassword() : pass;
        String finalAvatar = newAvatarPath.isEmpty() ? currentUser.getAvatarPath() : newAvatarPath;

        // Use the correct library method: updateCurrentUser(email, pass, birthDate, avatarPath)
        boolean ok = app.updateCurrentUser(email, finalPass, currentUser.getBirthDate(), finalAvatar);
        if (ok) {
            mensajeLabel.setText("¡Cambios guardados con éxito!");
            mensajeLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } else {
            mensajeLabel.setText("Error al guardar los cambios.");
        }
    }

    private void handleLogout() {
        app.logout();
        Navigation.loadScene("InicioSesionV2.fxml", "Running la Safor - Inicio de Sesión");
    }

    @FXML
    private void handleVolver() {
        Navigation.loadScene("Dashboard.fxml", "Running la Safor - Dashboard");
    }
}
