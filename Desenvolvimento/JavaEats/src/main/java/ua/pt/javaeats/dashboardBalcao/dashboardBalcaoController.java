package ua.pt.javaeats.dashboardBalcao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import ua.pt.javaeats.ConectarBD;
import ua.pt.javaeats.GerirBD;
import ua.pt.javaeats.SessaoUtilizador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class dashboardBalcaoController {

    @FXML private AnchorPane layoutPrincipal;
    @FXML private Button botaoTerminarSessao;
    @FXML private GridPane tabelaPreparar;
    @FXML private GridPane tabelaPreparado;

    public void initialize(){
        exibirDetalhesPedidos();
        layoutPrincipal.getStylesheets().add(getClass().getResource("/ua/pt/javaeats/CSS/barraLateral.css").toExternalForm());

        Tooltip tooltipSair = new Tooltip("Terminar Sessão");
        tooltipSair.setShowDelay(Duration.seconds(0));
        botaoTerminarSessao.setTooltip(tooltipSair);

        configurarBotaoTerminarSessao();
    }

    public void configurarBotaoTerminarSessao() {
        SessaoUtilizador sessao = SessaoUtilizador.getInstance();
        int cargo = sessao.getCargo();
        if (cargo == 3) {
            botaoTerminarSessao.setVisible(true);
        } else {
            botaoTerminarSessao.setVisible(false);
        }
    }

    public void sair(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/dashboard.fxml"));
            Parent root = loader.load();

            SessaoUtilizador.getInstance().terminarSessaoUtilizador();

            // Cria uma nova cena com o root carregado
            Scene scene = new Scene(root, 1920, 1080);

            // Obtém o stage atual
            Stage stage = (Stage) layoutPrincipal.getScene().getWindow();

            // Define a nova cena no stage
            stage.setScene(scene);

            stage.setMaximized(true);

            stage.setMinWidth(1920);
            stage.setMinHeight(1080);

            // Mostra o stage
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exibirDetalhesPedidos() {
        ConectarBD conexaoBD = new ConectarBD();
        Connection conexao = conexaoBD.conectar();

        if (conexao != null) {
            try {
                String query = GerirBD.getConsultaDetalhesPedidos();
                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                int row = 0; // Controle de linha para posicionar os detalhes de cada pedido
                int col = 0; // Controle de coluna para posicionar os detalhes de cada pedido

                while (resultSet.next()) {

                    int idPedido = resultSet.getInt("id_pedido");
                    Timestamp horaPedidoCriado = resultSet.getTimestamp("hora_pedido_criado");
                    String[] nomesItens = resultSet.getString("nomes_itens").split(",");
                    String[] quantidadesItens = resultSet.getString("quantidades_itens").split(",");
                    String numerosMesa = resultSet.getString("numeros_mesa");
                    String descricao = resultSet.getString("descricao");

                    // Cálculo da cor de fundo
                    String corFundo = calcularCorFundo(horaPedidoCriado);

                    GridPane detalhesPedidoGridPane = new GridPane();
                    detalhesPedidoGridPane.setHgap(10);
                    detalhesPedidoGridPane.setVgap(5);
                    detalhesPedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px;");
                    detalhesPedidoGridPane.setMinWidth(192);


                    tabelaPreparar.setVgap(10); // espaçamento vertical
                    tabelaPreparar.setHgap(10); // espaçamento horizontal
                    tabelaPreparado.setVgap(10); // espaçamento vertical
                    tabelaPreparado.setHgap(10); // espaçamento horizontal

                    //Adicona o ID do Pedido
                    Label labelPedido = new Label("Pedido #" + idPedido);
                    GridPane.setColumnSpan(labelPedido, 4);
                    detalhesPedidoGridPane.add(labelPedido, 0, 0);


                    detalhesPedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px;" + corFundo);

                    //Adiciona os itens do pedido
                    int numItensPedido = nomesItens.length;
                    for (int i = 0; i < numItensPedido; i++) {
                        Label labelItem = new Label(nomesItens[i] + " - " + quantidadesItens[i]);
                        detalhesPedidoGridPane.add(labelItem, 0, i + 1);
                    }

                    //Adiciona o label dos números da mesa
                    Label labelNumerosMesa = new Label("Mesa(s): " + numerosMesa);
                    detalhesPedidoGridPane.add(labelNumerosMesa, 0, numItensPedido + 1);

                    //Adiciona o label dos detalhes
                    Text textDetalhesPedido = new Text("Descrição: " + descricao);
                    textDetalhesPedido.setWrappingWidth(192);
                    detalhesPedidoGridPane.add(textDetalhesPedido, 0, numItensPedido + 3);

                    detalhesPedidoGridPane.add(new Label(""), 0, numItensPedido + 4);

                    //Adiciona o botão "Pronto"
                    Button buttonPedido = new Button("Pronto");
                    buttonPedido.setMinWidth(100);
                    buttonPedido.setOnAction(event -> {
                        if (buttonPedido.getText().equals("Pronto")) {
                            moverPedidoParaPreparado(detalhesPedidoGridPane, buttonPedido, idPedido);
                        } else {
                            moverPedidoParaPreparar(detalhesPedidoGridPane, buttonPedido, idPedido);
                        }
                    });
                    detalhesPedidoGridPane.add(buttonPedido, 0, numItensPedido + 5); //Adiciona após os itens do pedido e do número da mesa (Botão 'Pronto')

                    //Alinha o botão ao centro da célula do GridPane
                    GridPane.setHalignment(buttonPedido, HPos.CENTER);
                    GridPane.setValignment(buttonPedido, VPos.BOTTOM);

                    //Adiciona o GridPane com os detalhes ao Pane principal (tabelaPreparar)
                    tabelaPreparar.add(detalhesPedidoGridPane, col, row);

                    //Calcula as posições para a próxima adição (Neste caso, fica com 3 colunas a tabelaPreparar)
                    col++;
                    if (col > 2) {
                        col = 0;
                        row++;
                    }

                    //Calcula a altura do Pane com base no número de itens adicionados e o espaçamento vertical
                    double alturaPane = (row + 1) * 200; //Espaçamento vertical
                    tabelaPreparar.setPrefHeight(alturaPane);
                }

                resultSet.close();
                preparedStatement.close();

                // Nova consulta para pedidos prontos com menos de 5 minutos
                String queryProntos = GerirBD.getConsultaPedidosProntos(30);
                PreparedStatement preparedStatementProntos = conexao.prepareStatement(queryProntos);
                ResultSet resultSetProntos = preparedStatementProntos.executeQuery();

                while (resultSetProntos.next()) {
                    int idPedido = resultSetProntos.getInt("id_pedido");
                    Timestamp horaPedidoCriado = resultSetProntos.getTimestamp("hora_pedido_criado");
                    String[] nomesItens = resultSetProntos.getString("nomes_itens").split(",");
                    String[] quantidadesItens = resultSetProntos.getString("quantidades_itens").split(",");
                    String numerosMesa = resultSetProntos.getString("numeros_mesa");
                    String descricao = resultSetProntos.getString("descricao");

                    GridPane detalhesPedidoGridPane = new GridPane();
                    detalhesPedidoGridPane.setHgap(10);
                    detalhesPedidoGridPane.setVgap(5);
                    detalhesPedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px;");
                    tabelaPreparar.setVgap(10); // espaçamento vertical
                    tabelaPreparar.setHgap(10); // espaçamento horizontal
                    tabelaPreparado.setVgap(10); // espaçamento vertical
                    tabelaPreparado.setHgap(10); // espaçamento horizontal

                    //Adicona o ID do Pedido
                    Label labelPedido = new Label("Pedido #" + idPedido);
                    GridPane.setColumnSpan(labelPedido, 4);
                    detalhesPedidoGridPane.add(labelPedido, 0, 0);


                    detalhesPedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: #1ce856;");

                    //Adiciona os itens do pedido
                    int numItensPedido = nomesItens.length;
                    for (int i = 0; i < numItensPedido; i++) {
                        Label labelItem = new Label(nomesItens[i] + " - " + quantidadesItens[i]);
                        detalhesPedidoGridPane.add(labelItem, 0, i + 1);
                    }

                    //Adiciona o label dos números da mesa
                    Label labelNumerosMesa = new Label("Mesa(s): " + numerosMesa);
                    detalhesPedidoGridPane.add(labelNumerosMesa, 0, numItensPedido + 1);

                    //Adiciona o label dos detalhes
                    Text textDetalhesPedido = new Text("Descrição: " + descricao);
                    textDetalhesPedido.setWrappingWidth(192);
                    detalhesPedidoGridPane.add(textDetalhesPedido, 0, numItensPedido + 3);

                    detalhesPedidoGridPane.add(new Label(""), 0, numItensPedido + 4);
                    //Adiciona o botão "Cancelar"
                    Button buttonPedido = new Button("Cancelar");
                    buttonPedido.setMinWidth(100);
                    buttonPedido.setOnAction(event -> {
                        if (buttonPedido.getText().equals("Cancelar")) {
                            moverPedidoParaPreparar(detalhesPedidoGridPane, buttonPedido, idPedido);
                        } else {
                            moverPedidoParaPreparado(detalhesPedidoGridPane, buttonPedido, idPedido);
                        }
                    });
                    detalhesPedidoGridPane.add(buttonPedido, 0, numItensPedido + 5); //Adiciona após os itens do pedido e do número da mesa (Botão 'Pronto')

                    //Alinha o botão ao centro da célula do GridPane
                    GridPane.setHalignment(buttonPedido, HPos.CENTER);
                    GridPane.setValignment(buttonPedido, VPos.BOTTOM);

                    //Adiciona o GridPane com os detalhes ao Pane principal (tabelaPreparar)
                    tabelaPreparado.add(detalhesPedidoGridPane, col, row);

                    //Calcula as posições para a próxima adição (Neste caso, fica com 3 colunas a tabelaPreparar)
                    col++;
                    if (col > 2) {
                        col = 0;
                        row++;
                    }


                    //Calcula a altura do Pane com base no número de itens adicionados e o espaçamento vertical
                    double alturaPane = (row + 1) * 200; //Espaçamento vertical
                    tabelaPreparado.setPrefHeight(alturaPane);
                }

                resultSetProntos.close();
                preparedStatementProntos.close();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro a carregar os pedidos da base de dados");

            } finally {
                conexaoBD.desconectar();
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

    private void removerPedidoDaTabelaPreparado(GridPane detalhesPedidoGridPane) {
        tabelaPreparado.getChildren().remove(detalhesPedidoGridPane);
    }

    private String calcularCorFundo(Timestamp horaPedidoCriado) {
        //Calcula a diferença em milissegundos entre o tempo atual e o momento de criação do pedido
        long diffEmMs = System.currentTimeMillis() - horaPedidoCriado.getTime();
        //Calcula a diferença em minutos
        long diffEmMin = diffEmMs / (60 * 1000);
        //Inicializa a string com o estilo da cor de fundo
        String corFundo = "-fx-background-color: ";

        if (diffEmMin >= 20) {
            corFundo += "#f2463a;"; //Se passaram mais de 20 minutos, cor vermelha
        } else if (diffEmMin >= 10) {
            corFundo += "#f8ff73;"; //Se passaram mais de 10 minutos, cor amarela
        } else {
            corFundo += "#85f3ff;"; //Caso contrário, cor azul
        }
        return corFundo;
    }

    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }

    private void moverPedidoParaPreparado(GridPane pedidoGridPane, Button button, int idPedido) {
        // Inicializa a variável para armazenar o Label "Mesa(s)"
        ConectarBD conexaoBD = new ConectarBD();
        Connection conexao = conexaoBD.conectar();

        if (conexao != null) {
            try {
                // Atualizar o status do pedido para "Pronto"
                String updateQuery = "UPDATE Pedido_Restaurante SET status = 'Pronto' WHERE id_pedido = ?";
                PreparedStatement updateStatement = conexao.prepareStatement(updateQuery);
                updateStatement.setInt(1, idPedido);
                updateStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro a mover o pedido");
            } finally {
                conexaoBD.desconectar();
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


        Label labelNumerosMesa = null;

        int numItensPedido = pedidoGridPane.getChildren().size() - 2; // Excluindo o botão e o label dos números da mesa

        // Encontra o Label "Mesa(s)" dentro do GridPane do pedido
        for (Node node : pedidoGridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == (numItensPedido + 1) && GridPane.getColumnIndex(node) == 0) {
                if (node instanceof Label) {
                    labelNumerosMesa = (Label) node;
                }
                break;
            }
        }

        // Remove o pedido da tabelaPreparar
        tabelaPreparar.getChildren().remove(pedidoGridPane);
        tabelaPreparar.setVgap(10);
        tabelaPreparar.setHgap(10);
        pedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: #1ce856;");
        pedidoGridPane.setMinWidth(192);

        // Remove o botão existente, se houver
        pedidoGridPane.getChildren().remove(button);

        // Cria o botão "Cancelar"
        Button buttonCancelar = new Button("Cancelar");
        buttonCancelar.setMinWidth(100);
        Label finalLabelNumerosMesa = labelNumerosMesa;
        buttonCancelar.setOnAction(event -> {
            // Adiciona de volta o Label "Mesa(s)" ao GridPane do pedido
            if (finalLabelNumerosMesa != null) {
                if (finalLabelNumerosMesa.getParent() != null) {
                    ((GridPane) finalLabelNumerosMesa.getParent()).getChildren().remove(finalLabelNumerosMesa);
                }
                pedidoGridPane.add(finalLabelNumerosMesa, 0, numItensPedido + 1);
            }
            moverPedidoParaPreparar(pedidoGridPane, buttonCancelar, idPedido);
        });


        // Adiciona o botão "Cancelar" ao final do pedido
        pedidoGridPane.add(buttonCancelar, 0, numItensPedido + 2);

        // Adiciona o pedido à tabelaPreparado
        tabelaPreparado.getChildren().add(pedidoGridPane);
    }


    private void moverPedidoParaPreparar(GridPane pedidoGridPane, Button button, int idPedido) {

        ConectarBD conexaoBD = new ConectarBD();
        Connection conexao = conexaoBD.conectar();

        if (conexao != null) {
            try {
                // Atualizar o status do pedido para "Pronto"
                String updateQuery = "UPDATE Pedido_Restaurante SET status = 'Cancelado' WHERE id_pedido = ?";
                PreparedStatement updateStatement = conexao.prepareStatement(updateQuery);
                updateStatement.setInt(1, idPedido);
                updateStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro a mover o pedido ");
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
        // Remove o pedido da tabelaPreparado
        tabelaPreparado.getChildren().remove(pedidoGridPane);
        tabelaPreparado.setVgap(10);
        tabelaPreparado.setHgap(10);
        pedidoGridPane.setStyle("-fx-border-color: black; -fx-padding: 5px; -fx-background-color: #61b1ba;");
        pedidoGridPane.setMinWidth(192);

        // Remove o botão existente, se houver
        pedidoGridPane.getChildren().remove(button);

        // Adiciona o botão "Pronto" ao final do pedido
        int numItensPedido = pedidoGridPane.getChildren().size() - 2; // Excluindo o botão e o label dos números da mesa
        Button buttonPronto = new Button("Pronto");
        buttonPronto.setMinWidth(100);
        buttonPronto.setOnAction(event -> {
            moverPedidoParaPreparado(pedidoGridPane, buttonPronto, idPedido);
        });
        pedidoGridPane.add(buttonPronto, 0, numItensPedido + 2);

        // Adiciona o botão "X" ao final do pedido
        Button buttonX = new Button("X");
        buttonX.setStyle("-fx-background-color: red;");
        buttonX.setMinWidth(100);
        buttonX.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação");
            alert.setHeaderText("Tem a certeja que deseja eliminar permanentemente este pedido do balcão?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                if (conexao != null) {
                    try {
                        // Atualizar o status do pedido para "Eliminado"
                        String updateQuery = "UPDATE Pedido_Restaurante SET status = 'Eliminado' WHERE id_pedido = ?";
                        PreparedStatement updateStatement = conexao.prepareStatement(updateQuery);
                        updateStatement.setInt(1, idPedido);
                        updateStatement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Ocorreu um erro a mover o pedido ");
                    } finally {
                        conexaoBD.desconectar();
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
                tabelaPreparar.getChildren().remove(pedidoGridPane);
                mostrarAlerta("Sucesso", "Pedido eliminado!");
            }
        });
        pedidoGridPane.add(buttonX, 1, numItensPedido + 2); // Adiciona ao lado do botão "Pronto"

        // Adiciona o pedido de volta à tabelaPreparar
        tabelaPreparar.getChildren().add(pedidoGridPane);
    }

}
