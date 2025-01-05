package ua.pt.javaeats.dashboardGerirCategoria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.pt.javaeats.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class dashboardGerirCategoriaController {
     /**
     * Elementos da interface gráfica e conexão com a base de dados.
     */
    @FXML TableView<Categoria> tableViewCategorias;
    @FXML private TableColumn<Categoria, Integer> idColumn;
    @FXML private TableColumn<Categoria, String> nomeColumn;
    @FXML private TextField nomeField;
    Connection conexao;
    GerirBD gerirBD;

    /**
     * Inicializa a aplicação ao carregar a interface.
     * Chama métodos para conectar-se à base de dados, configurar a TableView e carregar as categorias.
     */
    @FXML
    public void initialize() {
        conectarBaseDeDados();
        if (conexao != null) {
            configurarTableView();
            carregarCategorias();
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
        //Cria uma conexão com a base de dados usando a classe ConectarBD
        conexao = new ConectarBD().conectar();
        //Inicializa o objeto GerirBD, passando a conexão como parâmetro para operações na base de dados
        gerirBD = new GerirBD();
        gerirBD.ConectarBD(conexao);
    }

    private void configurarTableView() {
        //Associa a coluna 'idColumn' ao atributo 'id' da classe Categoria
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        //Associa a coluna 'nomeColumn' ao atributo 'nome' da classe Categoria
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    }

    private void carregarCategorias() {
        //Cria uma lista vazia para armazenar as categorias recuperadas da base de dados
        List<Categoria> categorias = new ArrayList<>();
        if (conexao != null) {

            try (PreparedStatement preparedStatement = conexao.prepareStatement("SELECT id_categoria, nome FROM Categoria");
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                //Percorre o ResultSet para recuperar as categorias e adicioná-las à lista 'categorias'
                while (resultSet.next()) {
                    int idCategoria = resultSet.getInt("id_categoria");
                    String nomeCategoria = resultSet.getString("nome");
                    Categoria categoria = new Categoria(idCategoria, nomeCategoria);
                    categorias.add(categoria);
                }

                //Adiciona todas as categorias à TableView para exibição na interface
                tableViewCategorias.getItems().addAll(categorias);
            } catch (SQLException e) {
                e.printStackTrace();
                //Em caso de erro, exibe uma caixa de diálogo de alerta com uma mensagem de erro
                mostrarAlerta("Erro SQL", "Ocorreu um erro ao carregar as categorias da base de dados.");
            } catch (Exception e) {
                //Em caso de erro, exibe uma caixa de diálogo de alerta com uma mensagem de erro
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro não tratado ao carregar as categorias: " + e.getMessage());
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
    public void apagarCategoria(ActionEvent event) {
        if (conexao != null) {
            Categoria categoriaSelecionada = tableViewCategorias.getSelectionModel().getSelectedItem();

            if (categoriaSelecionada != null) {
                boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente esta categoria?");
                if (confirmacao) {
                    try {
                        System.out.println("ID da categoria selecionada: " + categoriaSelecionada.getId());

                        // Remove a categoria da base de dados utilizando o método da classe GerirBD
                        gerirBD.remover_categoria(categoriaSelecionada);

                        // Remove a categoria da TableView
                        tableViewCategorias.getItems().remove(categoriaSelecionada);
                        mostrarAlerta("SUCESSO", "Categoria eliminada!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Ocorreu um erro não tratado ao apagar a categoria: " + e.getMessage());
                    }
                }
            } else {
                mostrarAlerta("Erro", "Nenhuma categoria selecionada para apagar.");
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

    public boolean mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    @FXML
    public void adicionarCategoria(ActionEvent actionEvent) {
        if (conexao != null) {
            String novoNome = nomeField.getText().trim();

            if (!novoNome.isEmpty()) {
                // Verifica se o nome da categoria contém apenas caracteres alfanuméricos e alguns caracteres especiais permitidos
                if (novoNome.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                    try {
                        Categoria novaCategoria = new Categoria(novoNome);
                        // Tentativa de salvar a nova categoria na base de dados
                        gerirBD.guardar_categoria(novaCategoria);
                        tableViewCategorias.getItems().add(novaCategoria);
                        limparCampoTexto();
                        mostrarAlerta("SUCESSO", "Categoria adicionada!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Ocorreu um erro não tratado ao adicionar a categoria: " + e.getMessage());
                    }
                } else {
                    mostrarAlerta("Erro", "Nome da categoria inválido. Use apenas letras, números e alguns caracteres especiais como espaços, traços, underscore, etc.");
                }
            } else {
                mostrarAlerta("Erro", "O nome da categoria é obrigatório.");
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
    public void editarCategoria(ActionEvent event) {
        if (conexao != null) {
            try {
                Categoria categoriaSelecionada = tableViewCategorias.getSelectionModel().getSelectedItem();

                if (categoriaSelecionada != null) {
                    nomeField.setText(categoriaSelecionada.getNome());
                } else {
                    mostrarAlerta("Erro", "Nenhuma categoria selecionada para edição.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro ao editar a categoria: " + e.getMessage());
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
    public void atualizarCategoria(ActionEvent actionEvent) {
        if (conexao != null) {
            try {
                Categoria categoriaSelecionada = tableViewCategorias.getSelectionModel().getSelectedItem();

                if (categoriaSelecionada != null) {
                    String novoNomeCategoria = nomeField.getText();

                    if (novoNomeCategoria.matches("[a-zA-Z0-9À-ÿ\\s_-]+")) {
                        categoriaSelecionada.setNome(novoNomeCategoria);
                        gerirBD.editar_categoria(categoriaSelecionada);
                        tableViewCategorias.refresh();
                        limparCampoTexto();
                        mostrarAlerta("SUCESSO", "Categoria atualizada!");
                    } else {
                        mostrarAlerta("Erro", "O nome da categoria contém caracteres inválidos.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro ao atualizar a categoria: " + e.getMessage());
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




    /**
     * Exibe uma caixa de diálogo de alerta.
     * Cria e exibe um alerta com o tipo ERROR, exibindo o título e cabeçalho especificados.
     */
    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }

    /**
     * Limpa o campo de texto (nomeField).
     * Este método é utilizado para limpar o campo de texto após realizar operações
     * como adicionar ou atualizar uma categoria na interface do usuário.
     */
    private void limparCampoTexto() {
        nomeField.clear();
    }
}