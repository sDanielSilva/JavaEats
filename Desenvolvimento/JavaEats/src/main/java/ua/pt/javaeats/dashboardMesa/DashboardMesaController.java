package ua.pt.javaeats.dashboardMesa;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import ua.pt.javaeats.ConectarBD;
import ua.pt.javaeats.SessaoUtilizador;
import ua.pt.javaeats.dashboardPedido.DashboardPedidoController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardMesaController {
    public VBox vboxEsquerda;
    @FXML
    private VBox zonaContainer;
    @FXML
    private Label Titulo;
    @FXML
    private AnchorPane layoutPrincipal;
    @FXML
    private Button botaoTerminarSessao;
    @FXML
    private VBox mesaContainer;
    private Stage stage;

    private Connection conexao;

    public void initialize() {

        ConectarBD bd = new ConectarBD();
        conexao = bd.conectar();

        if (conexao != null) {

            layoutPrincipal.getStylesheets().add(getClass().getResource("/ua/pt/javaeats/CSS/barraLateral.css").toExternalForm());

            Tooltip tooltipSair = new Tooltip("Terminar Sessão");
            tooltipSair.setShowDelay(Duration.seconds(0));
            botaoTerminarSessao.setTooltip(tooltipSair);

            // Obter a sessão do usuário
            SessaoUtilizador sessao = SessaoUtilizador.getInstance();
            System.out.println(sessao);

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    atualizarMesas();
                });
            }, 0, 5, TimeUnit.SECONDS);

            carregarZonasDaBD(conexao);
        }else{
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }

    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }

    public void atualizarMesas() {
        if (conexao != null) {
            try {
                // Prepara a consulta SQL
                String query = "SELECT * FROM Eventos";
                Statement statement = conexao.createStatement();

                // Executa a consulta e obtem os resultados
                ResultSet resultSet = statement.executeQuery(query);

                // Itera sobre os resultados
                while (resultSet.next()) {
                    int mesa_id = resultSet.getInt("mesa_id");
                    String status_novo = resultSet.getString("status_novo");

                    // Atualiza o status da mesa
                    for (List<Button> mesas : mesasPorZona.values()) {
                        for (Button mesa : mesas) {
                            Integer idMesa = idsPorMesa.get(mesa);
                            if (idMesa != null && idMesa.equals(mesa_id)) {
                                mesa.setStyle("-fx-font-size: 14px; -fx-background-color: " +
                                        (status_novo.equalsIgnoreCase("livre") ? "green;" :
                                                (status_novo.equalsIgnoreCase("ocupada") ? "red;" :
                                                        (status_novo.equalsIgnoreCase("reservada") ? "yellow;" : ""))));
                                break;
                            }
                        }
                    }
                }

                // Fechar o resultSet
                resultSet.close();

                // Desativar SQL_SAFE_UPDATES
                String safeUpdatesOff = "SET SQL_SAFE_UPDATES = 0;";
                statement.executeUpdate(safeUpdatesOff);

                // Após a atualização das mesas, remover os registros antigos
                String deleteQuery = "DELETE FROM Eventos WHERE data_hora < DATE_SUB(NOW(), INTERVAL 2 MINUTE)";
                statement.executeUpdate(deleteQuery);

                // Reativar SQL_SAFE_UPDATES
                String safeUpdatesOn = "SET SQL_SAFE_UPDATES = 1;";
                statement.executeUpdate(safeUpdatesOn);

                // Fechar o statement
                statement.close();
            } catch (SQLException e) {
                // Imprimir o stack trace em caso de exceção
                e.printStackTrace();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }


    private void carregarZonasDaBD(Connection conexao) {
        if (conexao != null) {
            try {
                // Prepara a consulta SQL
                String query = "SELECT * FROM Zonas ORDER BY id";
                Statement statement = conexao.createStatement();

                // Executa a consulta e obter os resultados
                ResultSet resultSet = statement.executeQuery(query);

                VBox vbox = new VBox();
                vbox.setSpacing(10);

                Integer minId = null;

                ToggleGroup group = new ToggleGroup();

                // Itera sobre os resultados
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nome = resultSet.getString("nome");

                    if (minId == null) {
                        minId = id;
                    }

                    // Cria um novo texto para a zona
                    Text textoZona = new Text("Zona " + nome);
                    textoZona.setFont(Font.font("Arial", FontWeight.BOLD,  18));
                    ToggleButton btnZona = new ToggleButton();
                    btnZona.setToggleGroup(group);
                    btnZona.setOnAction(e -> {
                        mostrarMesasDaZona(id);
                    });
                    btnZona.setMinWidth(180);
                    btnZona.setMaxWidth(180);
                    btnZona.setMinHeight(40);
                    btnZona.setMaxHeight(40);
                    btnZona.setStyle("-fx-font-size: 18px; -fx-background-color: #67c2ff; -fx-text-fill: white;");
                    btnZona.setEffect(new DropShadow(5, Color.BLACK));
                    btnZona.setOnMouseEntered(e -> btnZona.setEffect(new Glow(0.5)));
                    btnZona.setOnMouseExited(e -> btnZona.setEffect(new DropShadow(5, Color.BLACK)));
                    btnZona.setOnMouseClicked(e -> btnZona.setEffect(new InnerShadow(10, Color.BLACK)));

                    // Adiciona um listener à propriedade selectedToggleProperty do ToggleGroup
                    group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                        public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                            // Quando a seleção muda, redefine todos os botões para o estado padrão
                            for (Toggle button : group.getToggles()) {
                                ((ToggleButton) button).setMinWidth(180);
                                ((ToggleButton) button).setStyle("-fx-font-size: 18px; -fx-background-color: #67c2ff; -fx-text-fill: white;");
                                ((ToggleButton) button).setEffect(new DropShadow(5, Color.BLACK));
                                ((ToggleButton) button).setTranslateX(0);
                            }

                            // Se um novo botão foi selecionado, aplica os efeitos
                            if (group.getSelectedToggle() != null) {
                                ((ToggleButton) group.getSelectedToggle()).setMinWidth(300);
                                ((ToggleButton) group.getSelectedToggle()).setStyle("-fx-font-size: 18px; -fx-background-color: #70acff; -fx-text-fill: white;");
                                ((ToggleButton) group.getSelectedToggle()).setEffect(new InnerShadow(15, Color.BLACK));
                                ((ToggleButton) group.getSelectedToggle()).setTranslateX(-120);
                            }
                        }
                    });

                    // Adiciona o texto ao botão
                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().add(textoZona);
                    btnZona.setGraphic(stackPane);

                    // Adiciona o botão ao vbox
                    vbox.getChildren().add(btnZona);

                    // Destaca o botão da zona com menor ID ao iniciar a aplicação
                    if (minId != null && id == minId) {
                        btnZona.setMinWidth(300);
                        btnZona.setStyle("-fx-font-size: 18px; -fx-background-color: #70acff; -fx-text-fill: white;");
                        btnZona.setEffect(new InnerShadow(15, Color.BLACK));
                        btnZona.setTranslateX(-120);
                    }
                }

                // Adiciona o vbox ao container
                zonaContainer.getChildren().add(vbox);

                if (minId != null) {
                    mostrarMesasDaZona(minId);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro a carregar as zonas da base de dados");
                e.printStackTrace();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }

    Map<Integer, List<Button>> mesasPorZona = new HashMap<>();
    Map<Button, Integer> idsPorMesa = new HashMap<>();

    private void carregarMesasDaBD(Connection conexao, int id_zona) {
        if (conexao != null) {
            try {
                // Prepara a consulta SQL
                String query = new StringBuilder().append("SELECT * FROM Mesa WHERE id_zona = ").append(id_zona).toString();
                Statement statement = conexao.createStatement();

                // Executa a consulta e obter os resultados
                ResultSet resultSet = statement.executeQuery(query);

                VBox vbox = new VBox();
                vbox.setSpacing(10);
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                vbox.getChildren().add(hbox);
                int buttonsInRow = 0;


                // Itera sobre os resultados
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_mesa");
                    String status = resultSet.getString("status");

                    // Calcula o valor total da mesa
                    String queryValorTotal = "SELECT PedidoDetalhe.quantidade, Item.preco FROM PedidoDetalhe " +
                            "INNER JOIN Item ON PedidoDetalhe.id_item = Item.id_item " +
                            "INNER JOIN Pedido_Restaurante ON PedidoDetalhe.id_pedido = Pedido_Restaurante.id_pedido " +
                            "INNER JOIN PagamentoAtendimento ON Pedido_Restaurante.id_pagamento = PagamentoAtendimento.id_pagatendimento " +
                            "WHERE PagamentoAtendimento.id_mesa = " + id + " AND PagamentoAtendimento.preco_total IS NULL";
                    Statement statementValorTotal = conexao.createStatement();
                    ResultSet resultSetValorTotal = statementValorTotal.executeQuery(queryValorTotal);
                    double valorTotal = 0;
                    while (resultSetValorTotal.next()) {
                        valorTotal += resultSetValorTotal.getInt("quantidade") * resultSetValorTotal.getDouble("preco");
                    }
                    resultSetValorTotal.close();
                    statementValorTotal.close();

                    // Cria um novo texto para a mesa
                    Text textoMesa = new Text("Mesa " + id);
                    if (!status.equalsIgnoreCase("livre") && valorTotal > 0) {
                        String valorFormatado = String.format("%.2f", valorTotal);
                        textoMesa.setText(textoMesa.getText() + "\nValor: " + valorFormatado);
                    }
                    textoMesa.setFont(Font.font("Arial", FontWeight.BOLD, 18));

                    // Cria um novo botão para a mesa
                    Button btnMesa = new Button();
                    idsPorMesa.put(btnMesa, id);
                    btnMesa.setVisible(true);
                    btnMesa.setMinWidth(180);
                    btnMesa.setMaxWidth(180);
                    btnMesa.setMinHeight(80);
                    btnMesa.setMaxHeight(80);
                    btnMesa.setOnAction(e -> abrirJanelaMesa(id));
                    btnMesa.setStyle("-fx-font-size: 18px; -fx-background-color: " +
                            (status.equalsIgnoreCase("livre") ? "green;" :
                                    (status.equalsIgnoreCase("ocupada") ? "red;" :
                                            (status.equalsIgnoreCase("reservada") ? "yellow;" : ""))) +
                            "-fx-background-radius: 10;");
                    btnMesa.setEffect(new DropShadow(2, Color.BLACK));
                    btnMesa.setOnMouseEntered(e -> btnMesa.setEffect(new Glow(0.5)));
                    btnMesa.setOnMouseExited(e -> btnMesa.setEffect(new DropShadow(2, Color.BLACK)));
                    btnMesa.setOnMouseClicked(e -> btnMesa.setEffect(new InnerShadow(10, Color.BLACK)));


                    // Adiciona o botão ao HBox
                    hbox.getChildren().add(btnMesa);
                    buttonsInRow++;

                    // Se houver 4 botões na linha, adiciona o HBox ao VBox e cria um novo HBox
                    if (buttonsInRow == 5) {
                        hbox = new HBox();
                        hbox.setSpacing(10);
                        vbox.getChildren().add(hbox);
                        buttonsInRow = 0;
                    }


                    // Adiciona o texto ao botão
                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().add(textoMesa);
                    btnMesa.setGraphic(stackPane);

                    // Adiciona o botão ao mapa de mesas por zona
                    if (!mesasPorZona.containsKey(id_zona)) {
                        mesasPorZona.put(id_zona, new ArrayList<>());
                    }
                    mesasPorZona.get(id_zona).add(btnMesa);
                }

                mesaContainer.getChildren().add(vbox);

            } catch (SQLException e) {
                // Imprimir o stack trace em caso de exceção
                e.printStackTrace();
                mostrarAlerta("Erro", "Ocorreu um erro a carregar as mesas da base de dados");
                e.printStackTrace();
            }
        }else{
            System.out.printf("Falha na conexão com a base de dados");
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }

    private void mostrarMesasDaZona(int id_zona) {
        // Remove as mesas antigas
        mesaContainer.getChildren().clear();

        // Carrega as novas mesas
        carregarMesasDaBD(conexao, id_zona);
    }



    private void abrirJanelaMesa(int mesaId) {
        try {
            // Carrega o fxml do Pedido
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Pedido.fxml"));
            Parent root = loader.load();

            // Obtem uma referência para o controlador do Pedido e passar o ID da mesa
            DashboardPedidoController pedidoController = loader.getController();
            pedidoController.setMesaId(mesaId);
            pedidoController.setMesaController(this);
            pedidoController.initialize();

            // Cria uma nova cena com o conteúdo da nova janela
            Scene scene = layoutPrincipal.getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            // Imprimir o stack trace em caso de exceção
            e.printStackTrace();

        }
    }

    public void fazerReserva(ActionEvent actionEvent) {
        if (stage == null || !stage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ua/pt/javaeats/reservas.fxml"));
                stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest(event -> stage = null);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Janela de reserva já está aberta!");
        }
    }

    public void configurarTitulo() {
        SessaoUtilizador sessao = SessaoUtilizador.getInstance();
        int cargo = sessao.getCargo();
        if (cargo == 2) {
            Titulo.setVisible(false);
        } else {
            vboxEsquerda.setManaged(true);
            vboxEsquerda.setVisible(true);
            botaoTerminarSessao.setVisible(true);
        }
    }

    public void configurarBotaoTerminarSessao() {
        SessaoUtilizador sessao = SessaoUtilizador.getInstance();
        int cargo = sessao.getCargo();
        if (cargo == 2) {
            vboxEsquerda.setManaged(true);
            vboxEsquerda.setVisible(true);
            botaoTerminarSessao.setVisible(true);
        } else {
            vboxEsquerda.setManaged(false);
            vboxEsquerda.setVisible(false);
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

            stage.setMinWidth(1920);
            stage.setMinHeight(1080);
            stage.setMaximized(true);

            // Mostra o stage
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}