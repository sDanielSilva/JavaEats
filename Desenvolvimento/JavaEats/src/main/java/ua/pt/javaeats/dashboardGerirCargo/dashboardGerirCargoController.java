package ua.pt.javaeats.dashboardGerirCargo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.pt.javaeats.Cargo;
import ua.pt.javaeats.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class dashboardGerirCargoController {

    @FXML
    private TableView<Cargo> tableViewCargos;
    @FXML
    private TableColumn<Cargo, Integer> idColumn;
    @FXML
    private TableColumn<Cargo, String> nomeColumn;
    @FXML
    private TextField nomeField;

    Connection conexao;
    GerirBD gerirBD;
    @FXML
    public void initialize() {
        conectarBaseDeDados();
        if(conexao != null) {
            configurarTableView();
            carregarCargos();
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void conectarBaseDeDados() {
        conexao = new ConectarBD().conectar();
        gerirBD = new GerirBD();
        gerirBD.ConectarBD(conexao);
    }

    private void configurarTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idCargo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
    }

    private void carregarCargos() {
        if (conexao != null) {
            List<Cargo> cargos = gerirBD.importar_cargo();
            tableViewCargos.getItems().addAll(cargos);
        }
    }

    @FXML
    public void apagarCargo(ActionEvent event) {
        if(conexao != null) {
            Cargo cargoSelecionado = tableViewCargos.getSelectionModel().getSelectedItem();

            if (cargoSelecionado != null) {
                boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente este cargo?");
                if (confirmacao) {
                    try {
                        gerirBD.remover_cargo(cargoSelecionado);
                        tableViewCargos.getItems().remove(cargoSelecionado);
                        mostrarAlerta("SUCESSO", "Cargo eliminado!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Ocorreu um erro não tratado ao apagar o cargo: " + e.getMessage());
                    }
                }
            } else {
                mostrarAlerta("Erro", "Nenhum cargo selecionado para apagar.");
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void adicionarCargo(ActionEvent actionEvent) {
        if (conexao != null) {
            String novoNome = nomeField.getText().trim();

            if (!novoNome.isEmpty()) {
                if (novoNome.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                    try {
                        Cargo novoCargo = new Cargo(novoNome);
                        gerirBD.guardar_cargo(novoCargo);
                        tableViewCargos.getItems().add(novoCargo);
                        limparCampoTexto();
                        mostrarAlerta("SUCESSO", "Cargo adicionado!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Ocorreu um erro não tratado ao adicionar o cargo: " + e.getMessage());
                    }
                } else {
                    mostrarAlerta("Erro", "Nome do cargo inválido. Use apenas letras, números e alguns caracteres especiais como espaços, traços, underscore, etc.");
                }
            } else {
                mostrarAlerta("Erro", "O nome do cargo é obrigatório.");
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    public void editarCargo(ActionEvent event) {
        if (conexao != null) {
            try {
                Cargo cargoSelecionado = tableViewCargos.getSelectionModel().getSelectedItem();

                if (cargoSelecionado != null) {
                    nomeField.setText(cargoSelecionado.getDescricao());
                } else {
                    mostrarAlerta("Erro", "Nenhum cargo selecionado para edição.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro ao editar o cargo: " + e.getMessage());
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    public void atualizarCargo(ActionEvent actionEvent) {
        if (conexao != null) {
            try {
                Cargo cargoSelecionado = tableViewCargos.getSelectionModel().getSelectedItem();

                if (cargoSelecionado != null) {
                    String novoNomeCargo = nomeField.getText();

                    if (novoNomeCargo.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                        cargoSelecionado.setDescricao(novoNomeCargo);
                        gerirBD.editar_cargo(cargoSelecionado);
                        tableViewCargos.refresh();
                        limparCampoTexto();
                        mostrarAlerta("SUCESSO", "Cargo atualizado!");
                    } else {
                        mostrarAlerta("Erro", "O nome do cargo contém caracteres inválidos.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro ao atualizar o cargo: " + e.getMessage());
            }
        }else {
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
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

    private void limparCampoTexto() {
        nomeField.clear();
    }
}