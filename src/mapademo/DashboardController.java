package mapademo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private Label usuarioLabel;
    @FXML private Label distanciaLabel;
    @FXML private Label tiempoLabel;
    @FXML private Label ascensoLabel;
    @FXML private Label descensoLabel;
    @FXML private ListView<Activity> actividadesListView;

    private SportActivityApp app = SportActivityApp.getInstance();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = app.getCurrentUser();
        if (currentUser != null) {
            usuarioLabel.setText("Hola, " + currentUser.getNickName());
            refreshActivities();
            calculateTotals();
        }

        actividadesListView.setCellFactory(lv -> new ListCell<Activity>() {
            private final HBox content = new HBox();
            private final Label label = new Label();
            private final Button deleteBtn = new Button("✕");
            private final Region spacer = new Region();

            {
                content.setSpacing(10);
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                deleteBtn.getStyleClass().add("button-danger");
                deleteBtn.setStyle("-fx-padding: 2 6; -fx-font-size: 10;");
                content.getChildren().addAll(label, spacer, deleteBtn);
                
                deleteBtn.setOnAction(e -> {
                    Activity item = getItem();
                    if (item != null) {
                        handleDeleteActivity(item);
                    }
                });

                // Add direct listener to content to ensure click works on the graphic
                content.setOnMouseClicked(event -> {
                    Activity item = getItem();
                    if (item != null) {
                        goToActivity(item);
                    }
                });
            }

            @Override
            protected void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item.getName());
                    setGraphic(content);
                }
            }
        });
    }

    @FXML
    private void handleListClick(javafx.scene.input.MouseEvent event) {
        Activity selected = actividadesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            goToActivity(selected);
        }
    }

    private void refreshActivities() {
        List<Activity> activities = app.getUserActivities();
        actividadesListView.setItems(FXCollections.observableArrayList(activities));
    }

    private void handleDeleteActivity(Activity activity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres borrar esta actividad?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar borrado");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                app.removeActivity(activity);
                refreshActivities();
                calculateTotals();
            }
        });
    }

    private void calculateTotals() {
        List<Activity> activities = app.getUserActivities();
        double totalDist = 0;
        long totalSeconds = 0;
        double totalAscent = 0;
        double totalDescent = 0;

        for (Activity a : activities) {
            totalDist += a.getTotalDistance();
            totalSeconds += a.getDuration().getSeconds();
            totalAscent += a.getElevationGain();
            totalDescent += a.getElevationLoss();
        }

        distanciaLabel.setText(String.format("%.2f Km", totalDist / 1000.0));
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        tiempoLabel.setText(String.format("%d h %d min", hours, minutes));
        ascensoLabel.setText(String.format("+%.0f m", totalAscent));
        descensoLabel.setText(String.format("-%.0f m", totalDescent));
    }

    @FXML
    private void handleImportGPX() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Actividad GPX");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Activity activity = app.importActivity(file);
            if (activity != null) {
                TextInputDialog dialog = new TextInputDialog(activity.getName());
                dialog.setTitle("Nombre de la actividad");
                dialog.setHeaderText("¿Qué nombre quieres darle a la actividad?");
                dialog.setContentText("Nombre:");
                dialog.showAndWait().ifPresent(name -> {
                    if (!name.trim().isEmpty()) {
                        app.renameActivity(activity, name);
                    }
                });
                
                refreshActivities();
                calculateTotals();
                goToActivity(activity);
            }
        }
    }

    @FXML
    private void handleVolver() {
        refreshActivities();
        calculateTotals();
    }

    @FXML
    private void handleLogout() {
        app.logout();
        Navigation.loadScene("InicioSesionV2.fxml", "Running la Safor - Inicio de Sesión");
    }

    @FXML
    private void handleGoToProfile() {
        Navigation.loadScene("AjustesPerfil.fxml", "Running la Safor - Ajustes de Perfil");
    }

    @FXML
    private void handleGoToMapManagement() {
        Navigation.loadScene("GestionMapas.fxml", "Running la Safor - Gestión de Mapas");
    }

    private void goToActivity(Activity activity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MapaConGraficaV2.fxml"));
            Parent root = loader.load();
            MapaConGraficaController controller = loader.getController();
            controller.setActivity(activity);
            
            Navigation.showParent(root, "Running la Safor - Análisis de Actividad");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
