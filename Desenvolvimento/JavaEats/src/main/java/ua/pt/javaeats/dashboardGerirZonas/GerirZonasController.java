package ua.pt.javaeats.dashboardGerirZonas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.pt.javaeats.*;
import ua.pt.javaeats.Zona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GerirZonasController {
    @FXML
    private TableView tableViewZonas;
    @FXML
    private TableColumn idColuna;
    @FXML
    private TableColumn nomeColuna;
    @FXML
    private TextField nomeField;

    Connection conexao;
    GerirBD gerirBD;

    @FXML
    public void initialize() {

        conectarBaseDeDados();
        if (conexao != null) {
            configurarTableView();
            carregarZonas();
        } else {
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
        idColuna.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColuna.setCellValueFactory(new PropertyValueFactory<>("nome"));
    }

    private void carregarZonas() {
        if (conexao != null) {
            List<Zona> zonas = gerirBD.importar_zona();
            tableViewZonas.getItems().addAll(zonas);
        } else {
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
    public void criarZona(ActionEvent actionEvent) {
        if (conexao != null) {
            String novoNome = nomeField.getText().trim();

            if (!novoNome.isEmpty()) {
                if (novoNome.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                    try {
                        Zona novaZona = new Zona(novoNome);
                        gerirBD.guardar_zona(novaZona);
                        tableViewZonas.getItems().add(novaZona);
                        limparCampoTexto();
                        mostrarAlerta("Sucesso", "Zona adicionada com sucesso!");
                    } catch (Exception e) {
                        mostrarAlerta("Erro", "Ocorreu um erro ao adicionar a zona: " + e.getMessage());
                    }
                } else {
                    mostrarAlerta("Erro", "Nome da zona inválido. Use apenas letras, números e alguns caracteres especiais como espaços, traços, underscore, etc.");
                }
            } else {
                mostrarAlerta("Erro", "O nome da zona é obrigatório.");
            }
        } else {
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
    public void editarZona(ActionEvent event) {
        if (conexao != null) {
            try {
                Zona zonaSelecionado = (Zona) tableViewZonas.getSelectionModel().getSelectedItem();

                if (zonaSelecionado != null) {
                    nomeField.setText(zonaSelecionado.getNome());
                } else {
                    mostrarAlerta("Erro", "Nenhum zona selecionado para edição.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro ao editar a zona: " + e.getMessage());
            }
        } else {
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
    public void atualizarZona(ActionEvent actionEvent) {
        if (conexao != null) {
            try {
                Zona zonaSelecionado = (Zona) tableViewZonas.getSelectionModel().getSelectedItem();

                if (zonaSelecionado != null) {
                    String novoNomeZona = nomeField.getText();

                    if (novoNomeZona.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                        zonaSelecionado.setNome(novoNomeZona);
                        gerirBD.editar_zona(zonaSelecionado);
                        tableViewZonas.refresh();
                        limparCampoTexto();
                        mostrarAlerta("Sucesso", "Zona atualizada com sucesso!");
                    } else {
                        mostrarAlerta("Erro", "O nome do Zona contém caracteres inválidos.");
                    }
                }
            } catch (Exception e) {
                mostrarAlerta("Erro", "Ocorreu um erro ao atualizar o Zona: " + e.getMessage());
            }
        } else {
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
    public void removerZona(ActionEvent event) {
        if (conexao != null) {
            Zona zonaSelecionado = (Zona) tableViewZonas.getSelectionModel().getSelectedItem();

            if (zonaSelecionado != null) {

                try {
                    PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM Mesa WHERE id_zona = ?");
                    stmt.setInt(1, zonaSelecionado.getId());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        mostrarAlerta("Erro", "A zona contém mesas. Não pode ser removida.");
                    } else {
                        boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente esta zona?");
                        if (confirmacao) {
                            gerirBD.remover_zona(zonaSelecionado);
                            tableViewZonas.getItems().remove(zonaSelecionado);
                            mostrarAlerta("Sucesso", "Zona removida com sucesso!");
                        }
                    }
                } catch (Exception e) {
                    mostrarAlerta("Erro", "Ocorreu um erro ao apagar a zona: " + e.getMessage());
                }
            } else {
                mostrarAlerta("Erro", "Nenhuma zona selecionada para apagar.");
            }
        } else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }

    private void limparCampoTexto() {
        nomeField.clear();
    }

}
