package mapademo;

import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import upv.ipc.sportlib.*;
import java.io.File;
import java.util.List;

public class MapaConGraficaController {

    @FXML private ScrollPane map_scrollpane;
    @FXML private Pane map_pane;
    @FXML private ImageView map_imageview;
    @FXML private Slider zoom_slider;
    @FXML private Label ritmoLabel;
    @FXML private Label velocidadLabel;
    @FXML private Label altitudLabel;
    @FXML private LineChart<Number, Number> altitudChart;
    @FXML private NumberAxis ejeDistancia;
    @FXML private NumberAxis ejeAltitud;
    @FXML private ListView<Activity> map_listview;
    @FXML private Label usuarioLabel;
    @FXML private Label avatarLabel;
    @FXML private Label distanciaLabel;

    private Activity currentActivity;
    private Polyline route;
    private SportActivityApp app = SportActivityApp.getInstance();

    @FXML
    public void initialize() {
        User user = app.getCurrentUser();
        if (user != null) {
            usuarioLabel.setText("Hola, " + user.getNickName());
        }

        zoom_slider.setMin(0.5);
        zoom_slider.setMax(3.0);
        zoom_slider.setValue(1.0);

        // Listener para que el zoom se mantenga centrado en la vista actual
        zoom_slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double newScale = newVal.doubleValue();
            
            // 1. Obtener el punto central actual del visor (viewport)
            Bounds viewportBounds = map_scrollpane.getViewportBounds();
            double centerX = viewportBounds.getWidth() / 2;
            double centerY = viewportBounds.getHeight() / 2;
            
            // 2. Convertir ese punto central a coordenadas locales del mapa (map_pane)
            // Esto nos dice qué punto geográfico/del mapa estamos mirando justo ahora
            Point2D centerInScene = map_scrollpane.localToScene(centerX, centerY);
            Point2D centerInMap = map_pane.sceneToLocal(centerInScene);

            // 3. Aplicar el zoom usando una transformación con pivote fijo en (0,0)
            // Usar setScaleX/Y suele pivotar en el centro, lo que causa desplazamientos raros
            map_pane.getTransforms().setAll(new Scale(newScale, newScale, 0, 0));

            // 4. Forzar layout y re-centrar en el siguiente pulso
            javafx.application.Platform.runLater(() -> {
                // Ver dónde ha acabado nuestro punto de interés tras el zoom
                Point2D newPointInScene = map_pane.localToScene(centerInMap);
                Point2D newPointInViewport = map_scrollpane.sceneToLocal(newPointInScene);

                // Calcular cuánto se ha movido respecto al centro del visor
                double deltaX = newPointInViewport.getX() - centerX;
                double deltaY = newPointInViewport.getY() - centerY;

                // Ajustar el scroll para compensar ese movimiento exactamente
                Bounds contentBounds = map_scrollpane.getContent().getBoundsInParent();
                double hRange = contentBounds.getWidth() - map_scrollpane.getViewportBounds().getWidth();
                double vRange = contentBounds.getHeight() - map_scrollpane.getViewportBounds().getHeight();

                if (hRange > 0) {
                    map_scrollpane.setHvalue(map_scrollpane.getHvalue() + deltaX / hRange);
                }
                if (vRange > 0) {
                    map_scrollpane.setVvalue(map_scrollpane.getVvalue() + deltaY / vRange);
                }
            });
        });

        setupListView();
        refreshActivities();
    }

    private void setupListView() {
        map_listview.setCellFactory(lv -> new ListCell<Activity>() {
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

                content.setOnMouseClicked(event -> {
                    Activity item = getItem();
                    if (item != null) {
                        setActivity(item);
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

    private void refreshActivities() {
        List<Activity> activities = app.getUserActivities();
        map_listview.setItems(FXCollections.observableArrayList(activities));
    }

    private void handleDeleteActivity(Activity activity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres borrar esta actividad?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar borrado");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                app.removeActivity(activity);
                refreshActivities();
                if (currentActivity != null && currentActivity.getId() == activity.getId()) {
                    map_imageview.setImage(null);
                    map_pane.getChildren().clear();
                    altitudChart.getData().clear();
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        Navigation.loadScene("Dashboard.fxml", "Running la Safor - Dashboard");
    }

    @FXML
    private void listClicked(javafx.scene.input.MouseEvent event) {
        Activity selected = map_listview.getSelectionModel().getSelectedItem();
        if (selected != null) {
            setActivity(selected);
        }
    }

    public void setActivity(Activity activity) {
        this.currentActivity = activity;
        displayActivity();
    }

    private void displayActivity() {
        if (currentActivity == null) return;

        distanciaLabel.setText(String.format("%.2f km", currentActivity.getTotalDistance() / 1000.0));
        ritmoLabel.setText(String.format("%.2f min/km", currentActivity.getAveragePace()));
        velocidadLabel.setText(String.format("%.2f km/h", currentActivity.getAverageSpeed()));
        altitudLabel.setText(String.format("%.0f m", currentActivity.getMaxElevation()));

        MapRegion region = currentActivity.getSuggestedMap();
        if (region != null) {
            Image img = new Image(new File(region.getImagePath()).toURI().toString());
            map_imageview.setImage(img);
            map_pane.setPrefSize(img.getWidth(), img.getHeight());

            MapProjection proj = new MapProjection(region, img.getWidth(), img.getHeight());
            
            map_pane.getChildren().removeIf(node -> node instanceof Polyline || node instanceof Circle);
            
            route = new Polyline();
            route.setStroke(Color.BLUE);
            route.setStrokeWidth(3);

            List<TrackPoint> points = currentActivity.getTrackPoints();
            for (TrackPoint tp : points) {
                javafx.geometry.Point2D p = proj.project(tp);
                route.getPoints().addAll(p.getX(), p.getY());
            }
            map_pane.getChildren().add(route);

            if (!points.isEmpty()) {
                TrackPoint start = points.get(0);
                TrackPoint end = points.get(points.size() - 1);
                javafx.geometry.Point2D pStart = proj.project(start);
                javafx.geometry.Point2D pEnd = proj.project(end);
                
                Circle startCircle = new Circle(pStart.getX(), pStart.getY(), 5, Color.GREEN);
                Circle endCircle = new Circle(pEnd.getX(), pEnd.getY(), 5, Color.RED);
                map_pane.getChildren().addAll(startCircle, endCircle);

                javafx.application.Platform.runLater(() -> {
                    double h = pStart.getX() / map_pane.getWidth();
                    double v = pStart.getY() / map_pane.getHeight();
                    map_scrollpane.setHvalue(h);
                    map_scrollpane.setVvalue(v);
                });
            }
        }

        altitudChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Altitud");

        List<TrackPoint> points = currentActivity.getTrackPoints();
        double distance = 0;
        TrackPoint last = null;
        
        double minAlt = currentActivity.getMinElevation();
        double maxAlt = currentActivity.getMaxElevation();
        
        for (TrackPoint tp : points) {
            if (last != null) {
                distance += tp.distanceTo(last);
            }
            series.getData().add(new XYChart.Data<>(distance / 1000.0, tp.getElevation()));
            last = tp;
        }
        
        altitudChart.getData().add(series);
        
        ejeDistancia.setLowerBound(0);
        ejeDistancia.setUpperBound(distance / 1000.0);
        ejeAltitud.setLowerBound(Math.max(0, minAlt - 50));
        ejeAltitud.setUpperBound(maxAlt + 50);
    }

    @FXML
    private void zoomIn() {
        zoom_slider.setValue(zoom_slider.getValue() + 0.1);
    }

    @FXML
    private void zoomOut() {
        zoom_slider.setValue(zoom_slider.getValue() - 0.1);
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
                setActivity(activity);
            }
        }
    }

    @FXML
    private void handleGoToMapManagement() {
        Navigation.loadScene("GestionMapas.fxml", "Running la Safor - Gestión de Mapas");
    }

    @FXML
    private void showPosition() {
    }
}
