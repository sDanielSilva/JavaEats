package ua.pt.javaeats.dashboardGerirMesas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.pt.javaeats.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GerirMesasController {

    @FXML
    private ComboBox zonaComboBox;

    private Mesa mesaParaEditar;

    private ObservableList<Zona> zonasList = FXCollections.observableArrayList();

    @FXML
    private TableColumn zonaColumn;
    @FXML
    private TableView<Mesa> table;
    @FXML
    private TableColumn<Mesa, Integer> idColumn;
    @FXML
    private TableColumn<Mesa, String> statusColumn;
    private Connection conexao;
    GerirBD gerirBD = new GerirBD();

    public void initialize() {
        ConectarBD conectarBD = new ConectarBD();
        conexao = conectarBD.conectar();

        if (conexao != null) {
            gerirBD.ConectarBD(conexao);
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            zonaColumn.setCellValueFactory(new PropertyValueFactory<>("id_zona"));

            try {
                List<Zona> zonas = gerirBD.importar_zona();
                zonasList.addAll(zonas);
                zonaComboBox.setItems(zonasList);
                atualizarTabela();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Um erro ocorreu durante a inicialização: " + e.getMessage());
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            showAlert(Alert.AlertType.ERROR,"ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            conectarBD.desconectar();
        }

    }

    @FXML
    public void adicionarMesa() {
        if (conexao != null) {
            Zona zonaSelecionada = (Zona) zonaComboBox.getValue();
            if (zonaSelecionada != null) {
                int idZona = zonaSelecionada.getId();
                Mesa novaMesa = new Mesa(0, "Livre", idZona);
                gerirBD.ConectarBD(conexao);
                try {
                    gerirBD.guardar_mesa(novaMesa);
                    atualizarTabela();
                    showAlert(Alert.AlertType.INFORMATION, "Successo", "Mesa adicionada com sucesso!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao adicionar a mesa: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Nenhuma zona selecionada.");
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            showAlert(Alert.AlertType.ERROR,"ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void editarMesa() {
        Mesa mesaSelecionada = table.getSelectionModel().getSelectedItem();

        if (mesaSelecionada != null) {

            preencherCampos(mesaSelecionada);

            mesaParaEditar = mesaSelecionada;
        }
    }

    private void preencherCampos(Mesa mesa) {
        zonaComboBox.setValue(gerirBD.getZonaById(mesa.getId_zona()));
    }

    @FXML
    public void atualizarMesa(ActionEvent actionEvent) {
        if (conexao != null) {
            if (mesaParaEditar != null) {
                Zona novaZona = (Zona) zonaComboBox.getValue();
                int novoIdZona = novaZona.getId();

                mesaParaEditar.setId_zona(novoIdZona);

                gerirBD.editar_mesa(mesaParaEditar);
                int selectedIndex = table.getSelectionModel().getSelectedIndex();
                table.getItems().set(selectedIndex, mesaParaEditar);

                zonaComboBox.getSelectionModel().clearSelection();
                mesaParaEditar = null;
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Mesa atualizada!");
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            showAlert(Alert.AlertType.ERROR,"ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    public void removerMesa() {

        if (conexao != null) {
            Mesa mesaSelecionada = table.getSelectionModel().getSelectedItem();
            gerirBD.ConectarBD(conexao);
            try {
                if (mesaSelecionada != null) {
                    boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente esta mesa?");
                    if (confirmacao) {
                        gerirBD.remover_mesa(mesaSelecionada);
                        atualizarTabela();
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Mesa removida com sucesso!");
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao remover a mesa: " + e.getMessage());
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            showAlert(Alert.AlertType.ERROR,"ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void atualizarTabela() throws Exception {
        gerirBD.ConectarBD(conexao);
        table.getItems().clear();
        table.getItems().addAll(gerirBD.importar_mesa());
        table.refresh();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
