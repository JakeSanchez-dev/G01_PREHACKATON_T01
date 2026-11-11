package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.EventoDAO;
import com.prehackaton.eventos.model.EventoDAOImpl;
import com.prehackaton.eventos.model.Evento;
import com.prehackaton.eventos.util.ReportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador de la vista de Gestión de Eventos.
 *
 * Contiene la lógica de control, eventos e interacción con el usuario.
 * NO contiene consultas SQL: todo el acceso a datos se delega en EventoDAO.
 */
public class EventoController {

    // ======================== Componentes de la vista (FXML) ========================

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Integer> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colDescripcion;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colLugar;
    @FXML private TableColumn<Evento, String> colModalidad;
    @FXML private TableColumn<Evento, String> colEstado;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private DatePicker fechaPicker;
    @FXML private TextField txtLugar;
    @FXML private ComboBox<String> cbModalidad;
    @FXML private ComboBox<String> cbEstado;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnReporte;

    @FXML private Label lblContador;
    @FXML private Label lblFechaHora;
    @FXML private VBox formularioContainer;

    // ======================== Capa DAO y estado ========================

    private final EventoDAO eventoDAO;
    private final ObservableList<Evento> datosTabla = FXCollections.observableArrayList();
    private Evento eventoSeleccionado = null;

    // Valores válidos para modalidad y estado
    private static final String[] MODALIDADES = {"Presencial", "Virtual", "Híbrido"};
    private static final String[] ESTADOS = {"Activo", "Inactivo", "Programado", "Completado", "Cancelado"};

    public EventoController() throws Exception {
        this.eventoDAO = new EventoDAOImpl();
    }

    /** Inicializa el controlador: columnas, combos, datos y listeners. */
    @FXML
    public void initialize() {
        configurarColumnas();
        cargarComboBoxes();
        cargarTabla();
        actualizarFechaHora();

        // Selección de fila -> cargar en el formulario
        tablaEventos.getSelectionModel().selectedItemProperty()
                .addListener((obs, antigua, seleccion) -> cargarSeleccionEnFormulario(seleccion));
    }

    private void configurarColumnas() {
        // Mapeo explícito de cada columna a las propiedades de Evento
        colId.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdEvento() != null ? cell.getValue().getIdEvento() : 0).asObject());
        colNombre.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre() != null ? cell.getValue().getNombre() : ""));
        colDescripcion.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion() != null ? cell.getValue().getDescripcion() : ""));
        colFecha.setCellValueFactory(cell -> {
            Evento e = cell.getValue();
            String texto = e.getFecha() != null
                    ? e.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";
            return new javafx.beans.property.SimpleStringProperty(texto);
        });
        colFecha.setCellFactory(col -> new TableCell<Evento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });
        colLugar.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getLugar() != null ? cell.getValue().getLugar() : ""));
        colModalidad.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getModalidad() != null ? cell.getValue().getModalidad() : ""));
        colEstado.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstado() != null ? cell.getValue().getEstado() : ""));

        // Ancho fijo de columnas para que se muestren correctamente
        colId.setPrefWidth(50);
        colNombre.setPrefWidth(200);
        colDescripcion.setPrefWidth(250);
        colFecha.setPrefWidth(120);
        colLugar.setPrefWidth(140);
        colModalidad.setPrefWidth(100);
        colEstado.setPrefWidth(120);

        tablaEventos.setItems(datosTabla);
        tablaEventos.refresh();
    }

    private void cargarComboBoxes() {
        cbModalidad.getItems().setAll(MODALIDADES);
        cbEstado.getItems().setAll(ESTADOS);
    }

    /** Carga los datos desde la DAO y actualiza la tabla y el contador. */
    public void cargarTabla() {
        datosTabla.clear();
        try {
            List<Evento> lista = eventoDAO.listar();
            if (lista != null) {
                datosTabla.addAll(lista);
            }
            lblContador.setText("Total de eventos: " + datosTabla.size());
            tablaEventos.refresh();
        } catch (Exception e) {
            lblContador.setText("Error al cargar eventos");
            mostrarError("Error al cargar eventos", "No se pudo obtener el listado de eventos: " + e.getMessage());
        }
    }

    private void cargarSeleccionEnFormulario(Evento evento) {
        if (evento == null) {
            limpiarFormulario(false);
            return;
        }
        this.eventoSeleccionado = evento;
        txtNombre.setText(evento.getNombre());
        txtDescripcion.setText(evento.getDescripcion() != null ? evento.getDescripcion() : "");
        fechaPicker.setValue(evento.getFecha());
        txtLugar.setText(evento.getLugar() != null ? evento.getLugar() : "");
        cbModalidad.setValue(evento.getModalidad());
        cbEstado.setValue(evento.getEstado());

        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
    }

    private void actualizarFechaHora() {
        lblFechaHora.setText(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    // ======================== Acciones de botones ========================

    @FXML
    private void handleNuevo() {
        limpiarFormulario(true);
        tablaEventos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        String nombre = txtNombre.getText().trim();

        // Control de duplicados (nombre único)
        try {
            if (eventoDAO.existeNombre(nombre)) {
                mostrarAdvertencia("Nombre duplicado",
                        "Ya existe un evento registrado con el nombre \"" + nombre + "\".");
                return;
            }
        } catch (Exception e) {
            mostrarError("Error de validación", "No se pudo verificar el nombre del evento: " + e.getMessage());
            return;
        }

        Evento nuevo = construirEventoDesdeFormulario(null);
        try {
            eventoDAO.insertar(nuevo);
            mostrarInformación("Evento registrado",
                    "El evento \"" + nuevo.getNombre() + "\" se creó correctamente.");
            cargarTabla();
            limpiarFormulario(true);
        } catch (Exception e) {
            mostrarError("Error al guardar", "No se pudo registrar el evento: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (eventoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un evento",
                    "Por favor seleccione un evento de la tabla para actualizar.");
            return;
        }
        if (!validarCampos()) {
            return;
        }

        String nombre = txtNombre.getText().trim();
        try {
            if (eventoDAO.existeNombreExcluyendo(nombre, eventoSeleccionado.getIdEvento())) {
                mostrarAdvertencia("Nombre duplicado",
                        "Ya existe otro evento registrado con el nombre \"" + nombre + "\".");
                return;
            }
        } catch (Exception e) {
            mostrarError("Error de validación", "No se pudo verificar el nombre del evento: " + e.getMessage());
            return;
        }

        Evento actualizado = construirEventoDesdeFormulario(eventoSeleccionado.getIdEvento());
        try {
            eventoDAO.actualizar(actualizado);
            mostrarInformación("Evento actualizado",
                    "El evento \"" + actualizado.getNombre() + "\" se actualizó correctamente.");
            cargarTabla();
            limpiarFormulario(true);
        } catch (Exception e) {
            mostrarError("Error al actualizar", "No se pudo actualizar el evento: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (eventoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un evento",
                    "Por favor seleccione un evento de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar evento");
        confirmacion.setContentText("¿Está seguro de que desea eliminar el evento \"" +
                eventoSeleccionado.getNombre() + "\"? Esta acción no se puede deshacer.");
        confirmacion.initOwner(getStage());

        if (confirmacion.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            try {
                eventoDAO.eliminar(eventoSeleccionado.getIdEvento());
                mostrarInformación("Evento eliminado",
                        "El evento se eliminó correctamente.");
                cargarTabla();
                limpiarFormulario(true);
            } catch (Exception e) {
                mostrarError("Error al eliminar", "No se pudo eliminar el evento: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario(true);
        tablaEventos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleReporte() {
        try {
            List<Evento> eventos = eventoDAO.listar();
            if (eventos.isEmpty()) {
                mostrarAdvertencia("Sin datos",
                        "No hay eventos registrados para generar el reporte.");
                return;
            }
            ReportUtil.mostrarReporte(eventos);
        } catch (Exception e) {
            mostrarError("Error al generar reporte",
                    "No se pudo generar el reporte de eventos: " + e.getMessage());
        }
    }

    // ======================== Validaciones y helpers ========================

    /** Valida que los campos obligatorios estén completos y con formato correcto. */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            errores.append("• El nombre del evento es obligatorio.\n");
        } else if (nombre.length() > 100) {
            errores.append("• El nombre no puede exceder los 100 caracteres.\n");
        }

        if (fechaPicker.getValue() == null) {
            errores.append("• La fecha del evento es obligatoria.\n");
        } else {
            LocalDate hoy = LocalDate.now();
            if (fechaPicker.getValue().isBefore(hoy)) {
                errores.append("• La fecha del evento no puede ser anterior a hoy.\n");
            }
        }

        String lugar = txtLugar.getText() == null ? "" : txtLugar.getText().trim();
        if (lugar.isEmpty()) {
            errores.append("• El lugar del evento es obligatorio.\n");
        } else if (lugar.length() > 100) {
            errores.append("• El lugar no puede exceder los 100 caracteres.\n");
        }

        if (cbModalidad.getValue() == null || cbModalidad.getValue().isEmpty()) {
            errores.append("• Debe seleccionar una modalidad.\n");
        }
        if (cbEstado.getValue() == null || cbEstado.getValue().isEmpty()) {
            errores.append("• Debe seleccionar un estado.\n");
        }

        String descripcion = txtDescripcion.getText() == null ? "" : txtDescripcion.getText();
        if (descripcion.length() > 1000) {
            errores.append("• La descripción no puede exceder los 1000 caracteres.\n");
        }

        if (errores.length() > 0) {
            mostrarAdvertencia("Campos obligatorios / formato inválido",
                    "Corrija los siguientes errores antes de continuar:\n\n" + errores);
            return false;
        }
        return true;
    }

    private Evento construirEventoDesdeFormulario(Integer id) {
        Evento e = new Evento();
        e.setIdEvento(id);
        e.setNombre(txtNombre.getText().trim());
        e.setDescripcion(txtDescripcion.getText());
        e.setFecha(fechaPicker.getValue());
        e.setLugar(txtLugar.getText().trim());
        e.setModalidad(cbModalidad.getValue());
        e.setEstado(cbEstado.getValue());
        return e;
    }

    private void limpiarFormulario(boolean incluirSeleccion) {
        txtNombre.clear();
        txtDescripcion.clear();
        fechaPicker.setValue(null);
        txtLugar.clear();
        cbModalidad.setValue(null);
        cbEstado.setValue(null);
        if (incluirSeleccion) {
            eventoSeleccionado = null;
            tablaEventos.getSelectionModel().clearSelection();
        }
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        txtNombre.requestFocus();
    }

    // ======================== Diálogos de feedback ========================

    private void mostrarInformación(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.initOwner(getStage());
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.initOwner(getStage());
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.initOwner(getStage());
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) txtNombre.getScene().getWindow();
    }

    /** Cierra los recursos de la DAO al cerrar la aplicación. */
    public void cerrar() {
        try {
            eventoDAO.cerrar();
        } catch (Exception ignored) {
        }
    }
}
