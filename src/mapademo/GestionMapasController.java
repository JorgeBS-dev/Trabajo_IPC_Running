package mapademo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import java.io.File;
import java.util.List;

public class GestionMapasController {

    @FXML private ListView<MapRegion> mapasListView;
    @FXML private TextField nombreMapaField;
    @FXML private Button seleccionarJPGButton;
    @FXML private Button importarButton;

    private SportActivityApp app = SportActivityApp.getInstance();
    private File selectedJPG;

    @FXML
    public void initialize() {
        refreshList();

        seleccionarJPGButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Mapa JPG");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
            selectedJPG = fileChooser.showOpenDialog(null);
            if (selectedJPG != null) {
                seleccionarJPGButton.setText(selectedJPG.getName());
                if (nombreMapaField.getText().isEmpty()) {
                    nombreMapaField.setText(selectedJPG.getName().replace(".jpg", ""));
                }
            }
        });

        importarButton.setOnAction(e -> handleImport());

        mapasListView.setCellFactory(lv -> new ListCell<MapRegion>() {
            private final HBox content = new HBox();
            private final Label label = new Label();
            private final Button deleteBtn = new Button("✕");
            private final Region spacer = new Region();

            {
                content.setSpacing(10);
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                deleteBtn.getStyleClass().add("button-danger");
                content.getChildren().addAll(label, spacer, deleteBtn);

                deleteBtn.setOnAction(event -> {
                    MapRegion item = getItem();
                    if (item != null) {
                        app.removeMapRegion(item);
                        refreshList();
                    }
                });
            }

            @Override
            protected void updateItem(MapRegion item, boolean empty) {
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

    private void handleImport() {
        String name = nombreMapaField.getText();
        if (selectedJPG == null || name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Debes seleccionar un archivo y ponerle nombre.");
            alert.show();
            return;
        }

        // Default bounds (example: near Valencia)
        double minLat = 39.0, maxLat = 40.0, minLon = -1.0, maxLon = 0.0;
        
        // Correct method: addMapRegion(String name, File file, double minLat, double maxLat, double minLon, double maxLon)
        MapRegion newRegion = app.addMapRegion(name, selectedJPG, minLat, maxLat, minLon, maxLon);
        
        if (newRegion != null) {
            nombreMapaField.clear();
            selectedJPG = null;
            seleccionarJPGButton.setText("Seleccionar JPG");
            refreshList();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al importar el mapa.");
            alert.show();
        }
    }

    private void refreshList() {
        List<MapRegion> maps = app.getMapRegions();
        mapasListView.setItems(FXCollections.observableArrayList(maps));
    }

    @FXML
    private void handleVolver() {
        Navigation.loadScene("Dashboard.fxml", "Running la Safor - Dashboard");
    }
}
