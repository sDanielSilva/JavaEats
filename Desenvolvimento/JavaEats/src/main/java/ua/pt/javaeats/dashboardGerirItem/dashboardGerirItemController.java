package ua.pt.javaeats.dashboardGerirItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.pt.javaeats.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class dashboardGerirItemController {
    /**
     * Elementos da interface gráfica e conexão com a base de dados.
     */
    @FXML
    private TableView<Item> tableViewItens;
    @FXML
    private TextField idField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField precoField;
    @FXML
    private ComboBox<String> areaPreparoBox;
    @FXML
    private ComboBox<Categoria> categoriaBox;
    private Connection conexao;

    private ObservableList<Categoria> categoriasList = FXCollections.observableArrayList();

    private Item itemParaEditar;
    private GerirBD gerirBD;

    //Método de inicialização do controlador
    public void initialize() {
        conectarBD(); //Estabelece conexão com a base de dados
        if (conexao != null) {
            carregarItens();

            List<Categoria> categorias = gerirBD.importar_categoria();
            categoriasList.addAll(categorias);
            categoriaBox.setItems(categoriasList);
        } else {
            System.out.println("Erro ao conectar à Base de Dados!");
        }
    }
    

    private void conectarBD() {
        //Cria uma nova instância da classe de conexão ConectarBD
        ConectarBD conectarBD = new ConectarBD();
        //Estabelece a conexão com a base de dados e atribui à variável 'conexao'
        conexao = conectarBD.conectar();
        //Cria uma nova instância de GerirBD.
        gerirBD = new GerirBD();
        //Estabelece a conexão com a base de dados no gerirBD.
        gerirBD.ConectarBD(conexao);
    }


    private void carregarItens() {
        //Obtém a lista de itens da base de dados usando o GerirBD
        List<Item> itens = gerirBD.importar_item();

        //Adiciona todos os itens obtidos à TableView tableViewItens
        tableViewItens.getItems().addAll(itens);
    }


    @FXML
    public void adicionar_item() {
        //Obtém o nome, preço, área de preparo e nome da categoria dos campos de entrada
        String nome = nomeField.getText();
        double preco = Double.parseDouble(precoField.getText());
        Categoria categoriaSelecionada = categoriaBox.getValue();
        int idCategoria = categoriaSelecionada.getId();

        Item novoItem = new Item(nome, preco, idCategoria, conexao);

        //Guarda o novo item na base de dados usando o gerirBD
        gerirBD.guardar_item(novoItem);
        //Adiciona o novo item à TableView
        tableViewItens.getItems().add(novoItem);
        //Atualiza a TableView
        tableViewItens.refresh();
        mostrarAlerta("SUCESSO", "Item adicionado!");
    }

    @FXML
    public void remover_item() {
        try {//Obtém o item selecionado na TableView
            Item itemSelecionado = tableViewItens.getSelectionModel().getSelectedItem();

            //Verifica se um item foi selecionado antes de removê-lo
            if (itemSelecionado != null) {
                boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente este item?");
                if (confirmacao) {
                    //Remove o item da base de dados usando o GerirBD
                    gerirBD.remover_item(itemSelecionado);
                    //Remove o item da TableView
                    tableViewItens.getItems().remove(itemSelecionado);
                    mostrarAlerta("SUCESSO", "Item eliminado!");;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void atualizar_item(ActionEvent actionEvent) {
        if (itemParaEditar != null) {
            String novoNome = nomeField.getText();
            String novoPreco = precoField.getText();

            // Verificar se o novoPreco é um número válido
            try {
                Double.parseDouble(novoPreco);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Preço inválido. Por favor, insira um número.");
                alert.showAndWait();
                return;
            }

            Categoria novaCategoria = categoriaBox.getValue();
            int novoIdCategoria = novaCategoria.getId();

            itemParaEditar.setNome(novoNome);
            itemParaEditar.setPreco(Double.parseDouble(novoPreco));
            itemParaEditar.setIdCategoria(novoIdCategoria);

            gerirBD.editar_item(itemParaEditar);

            int selectedIndex = tableViewItens.getSelectionModel().getSelectedIndex();
            tableViewItens.getItems().set(selectedIndex, itemParaEditar);

            limparCamposTexto();
            itemParaEditar = null;
            mostrarAlerta("SUCESSO", "Item editado!");
        }
    }



    @FXML
    public void editar_item(ActionEvent actionEvent) {
        Item itemSelecionado = tableViewItens.getSelectionModel().getSelectedItem();

        if (itemSelecionado != null) {
            preencherCampos(itemSelecionado);
            itemParaEditar = itemSelecionado;
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


    private void preencherCampos(Item item) {
        nomeField.setText(item.getNome());
        precoField.setText(String.valueOf(item.getPreco()));
        categoriaBox.setValue(gerirBD.getCategoriaById(item.getIdCategoria()));
    }

    private void limparCamposTexto() {
        nomeField.clear();
        precoField.clear();
        categoriaBox.setValue(null);
    }
}