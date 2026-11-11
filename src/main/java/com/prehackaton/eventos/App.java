package com.prehackaton.eventos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.prehackaton.eventos.controller.EventoController;
import com.prehackaton.eventos.controller.ParticipanteController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Ventana principal UNIFICADA de la aplicación desktop del equipo.
 *
 * Muestra un TabPane con una sección (pestaña) por módulo:
 *  - "Eventos"       : módulo del Integrante 1 (EventoView.fxml + EventoController)
 *  - "Participantes" : módulo del Integrante 2 (ParticipanteView.fxml + ParticipanteController)
 *
 * Cada módulo se carga de forma diferida (la primera vez que se abre su pestaña),
 * de modo que un problema en un módulo (p. ej. base de datos caída) no afecte al otro.
 *
 * NOTA: esta clase NO debe ejecutarse directamente; lanzarla desde Main.
 */
public class App extends Application {

    private static final String FXML_EVENTOS =
            "/com/prehackaton/eventos/view/EventoView.fxml";

    private static final String FXML_PARTICIPANTES =
            "/com/prehackaton/eventos/view/ParticipanteView.fxml";

    /** Controladores ya cargados, para refrescar el listado al volver a una pestaña. */
    private final Map<Tab, Object> controladores = new HashMap<>();

    @Override
    public void start(Stage stage) {

        Tab tabEventos = new Tab("📅 Eventos (Integrante 1)");
        tabEventos.setUserData(FXML_EVENTOS);

        Tab tabParticipantes = new Tab("👥 Participantes (Integrante 2)");
        tabParticipantes.setUserData(FXML_PARTICIPANTES);

        TabPane pestanas = new TabPane();
        pestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Carga diferida + refresco: al abrir una pestaña se carga su módulo
        // (una sola vez) y al VOLVER a ella se recarga su listado desde la BD
        pestanas.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionada) -> {
                    boolean yaCargada = seleccionada != null
                            && seleccionada.getContent() != null;
                    cargarModulo(seleccionada);
                    if (yaCargada) {
                        refrescar(seleccionada);
                    }
                });

        pestanas.getTabs().addAll(tabParticipantes, tabEventos);

        Scene scene = new Scene(pestanas);

        // Hoja de estilos global: unifica el look de las pestañas y ambos módulos
        URL css = getClass().getResource("/com/prehackaton/eventos/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Gestión de Eventos y Participantes — Equipo G01");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setMaximized(true);
        stage.show();

        // Carga la pestaña inicial (Participantes)
        cargarModulo(pestanas.getSelectionModel().getSelectedItem());
    }

    /** Carga el FXML del módulo de la pestaña la primera vez que se abre. */
    private void cargarModulo(Tab pestana) {
        if (pestana == null || pestana.getContent() != null) {
            return;
        }

        String fxml = (String) pestana.getUserData();

        try {
            URL ubicacion = getClass().getResource(fxml);

            if (ubicacion == null) {
                throw new IllegalStateException(
                        "No se encontró el recurso " + fxml + ". Verifica la ruta."
                );
            }

            FXMLLoader loader = new FXMLLoader(ubicacion);
            Parent vista = loader.load();
            controladores.put(pestana, loader.getController());
            pestana.setContent(vista);

        } catch (Exception e) {

            // El error se muestra dentro de la pestaña para no tumbar toda la app
            Label aviso = new Label(
                    "No se pudo cargar el módulo \"" + pestana.getText() + "\".\n\n"
                            + "Causa: " + mensajeRaiz(e)
            );
            aviso.setWrapText(true);
            aviso.setStyle("-fx-padding: 30; -fx-text-fill: #B00020; -fx-font-size: 14px;");

            StackPane contenedor = new StackPane(aviso);
            contenedor.setStyle("-fx-background-color: #FAFAFA;");
            pestana.setContent(contenedor);
        }
    }

    /** Al volver a una pestaña ya cargada, recarga su listado desde la BD. */
    private void refrescar(Tab pestana) {
        Object controlador = controladores.get(pestana);
        if (controlador == null) {
            return;
        }
        try {
            if (controlador instanceof ParticipanteController) {
                ((ParticipanteController) controlador).refrescar();
            } else if (controlador instanceof EventoController) {
                ((EventoController) controlador).cargarTabla();
            }
        } catch (Exception ignored) {
            // Si la BD no está disponible, el propio controlador muestra el aviso
        }
    }

    /** Recorre la cadena de causas hasta llegar al mensaje original del error. */
    private String mensajeRaiz(Throwable e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() != null ? causa.getMessage() : causa.toString();
    }
}
