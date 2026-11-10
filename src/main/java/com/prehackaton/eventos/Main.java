package com.prehackaton.eventos;

import com.prehackaton.eventos.controller.EventoController;
import com.prehackaton.eventos.dao.EventoDAO;
import com.prehackaton.eventos.dao.EventoDAOImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación JavaFX del módulo Integrante 1:
 * Gestión de Eventos (CRUD + Reporte JasperReports).
 */
public class Main extends Application {

    private static final String FXML_VIEW_PATH =
            "/com/prehackaton/eventos/view/EventoView.fxml";

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_VIEW_PATH));
        Parent root = loader.load();

        EventoController controller = loader.getController();
        controller.inicializar();

        Scene scene = new Scene(root);
        // Hoja de estilos de la aplicación
        String css = getClass().getResource("/com/prehackaton/eventos/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Gestión de Eventos - Integrante 1 (MVC + DAO)");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();

        // Cerrar la conexión DAO al cerrar la ventana
        primaryStage.setOnCloseRequest(event -> controller.cerrar());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
