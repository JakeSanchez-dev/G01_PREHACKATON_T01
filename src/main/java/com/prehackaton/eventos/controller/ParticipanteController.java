
package com.prehackaton.eventos.controller;

import com.prehackaton.eventos.model.*;
import com.prehackaton.eventos.util.ParticipanteReportUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.regex.Pattern;

public class ParticipanteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtInstitucion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Participante> tablaParticipantes;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colNombres;
    @FXML private TableColumn<Participante, String> colApellidos;
    @FXML private TableColumn<Participante, String> colDni;
    @FXML private TableColumn<Participante, String> colCorreo;
    @FXML private TableColumn<Participante, String> colTelefono;
    @FXML private TableColumn<Participante, String> colInstitucion;

    private ParticipanteDAO participanteDAO;

    @FXML
    public void initialize() {
        try {
            participanteDAO = new ParticipanteDAOImpl();

            colId.setCellValueFactory(new PropertyValueFactory<>("idParticipante"));
            colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
            colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
            colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
            colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            colInstitucion.setCellValueFactory(new PropertyValueFactory<>("institucion"));

            tablaParticipantes.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, anterior, seleccionado) -> {
                        if (seleccionado != null) {
                            mostrarDatos(seleccionado);
                        }
                    });

            cargarTabla();

        } catch (Exception e) {
            mostrarError("No se pudo inicializar el módulo: " + e.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            List<Participante> lista = participanteDAO.listar();
            tablaParticipantes.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            mostrarError("Error al cargar participantes: " + e.getMessage());
        }
    }

    private void mostrarDatos(Participante p) {
        txtNombres.setText(p.getNombres());
        txtApellidos.setText(p.getApellidos());
        txtDni.setText(p.getDni());
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        txtInstitucion.setText(p.getInstitucion());
    }

    private Participante obtenerDatos() {
        Participante p = new Participante();
        p.setNombres(txtNombres.getText().trim());
        p.setApellidos(txtApellidos.getText().trim());
        p.setDni(txtDni.getText().trim());
        p.setCorreo(txtCorreo.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setInstitucion(txtInstitucion.getText().trim());
        return p;
    }

    private boolean validarDatos() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String correo = txtCorreo.getText().trim();
        String institucion = txtInstitucion.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()
                || correo.isEmpty() || institucion.isEmpty()) {
            mostrarAdvertencia("Complete todos los campos obligatorios.");
            return false;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarAdvertencia("El DNI debe contener exactamente 8 números.");
            return false;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", correo)) {
            mostrarAdvertencia("Ingrese un correo electrónico válido.");
            return false;
        }

        if (!txtTelefono.getText().trim().isEmpty()
                && !txtTelefono.getText().trim().matches("\\d{7,15}")) {
            mostrarAdvertencia("El teléfono debe contener entre 7 y 15 números.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleGuardar() {
        if (!validarDatos()) return;

        try {
            Participante p = obtenerDatos();

            if (participanteDAO.existeDni(p.getDni())) {
                mostrarAdvertencia("Ya existe un participante registrado con ese DNI.");
                return;
            }

            if (participanteDAO.insertar(p)) {
                mostrarInformacion("Participante registrado correctamente.");
                handleLimpiar();
                cargarTabla();
            }

        } catch (Exception e) {
            mostrarError("No se pudo guardar el participante: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        Participante seleccionado = tablaParticipantes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un participante de la tabla.");
            return;
        }

        if (!validarDatos()) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar actualización");
        confirmacion.setHeaderText("¿Desea actualizar este participante?");
        confirmacion.setContentText("Se modificarán los datos del participante seleccionado.");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            Participante p = obtenerDatos();
            p.setIdParticipante(seleccionado.getIdParticipante());

            if (participanteDAO.existeDniExcluyendo(
                    p.getDni(), p.getIdParticipante())) {
                mostrarAdvertencia("Otro participante ya tiene ese DNI.");
                return;
            }

            if (participanteDAO.actualizar(p)) {
                mostrarInformacion("Participante actualizado correctamente.");
                handleLimpiar();
                cargarTabla();
            }

        } catch (Exception e) {
            mostrarError("No se pudo actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        Participante seleccionado = tablaParticipantes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un participante para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar participante?");
        confirmacion.setContentText(
                seleccionado.getNombres() + " " + seleccionado.getApellidos());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            if (participanteDAO.eliminar(seleccionado.getIdParticipante())) {
                mostrarInformacion("Participante eliminado correctamente.");
                handleLimpiar();
                cargarTabla();
            }
        } catch (Exception e) {
            mostrarError("No se pudo eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuscar() {
        try {
            String criterio = txtBuscar.getText().trim();
            tablaParticipantes.setItems(
                    FXCollections.observableArrayList(participanteDAO.buscar(criterio)));
        } catch (Exception e) {
            mostrarError("Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void handleMostrarTodos() {
        cargarTabla();
    }

    /** Recarga el listado desde la base de datos (al volver a la pestaña). */
    public void refrescar() {
        cargarTabla();
    }

    @FXML
    private void handleLimpiar() {
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtInstitucion.clear();
        tablaParticipantes.getSelectionModel().clearSelection();
        txtNombres.requestFocus();
    }

    @FXML
    private void handleReporte() {
        try {
            ParticipanteReportUtil.mostrarReporte(participanteDAO.listar());
        } catch (Exception e) {
            mostrarError("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private void mostrarInformacion(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje).showAndWait();
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}

