package ua.pt.javaeats.dashboardPrincipal;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ua.pt.javaeats.*;
import ua.pt.javaeats.dashboardBalcao.dashboardBalcaoController;
import ua.pt.javaeats.dashboardMesa.DashboardMesaController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
public class DashboardController {
    @FXML
    private FlowPane funcionarioPane;

    private Connection conexao;

    public void initialize() {
        ConectarBD bd = new ConectarBD();
        conexao = bd.conectar(); // Atribui ao campo de classe 'conexao'
        if (conexao != null) {
            carregarFuncionarios(conexao);
            adicionarAnimacaoBotoes();
            iniciarComunicacaoSerial();
        } else {
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }


    private void carregarFuncionarios(Connection conexao) {
        GerirBD gerirBD = new GerirBD();
        gerirBD.ConectarBD(conexao);

        // Importa a lista de funcionários da base de dados
        List<Funcionario> funcionarios = gerirBD.importar_funcionario();

        // Ciclo da lista de funcionários
        for (Funcionario funcionario : funcionarios) {
            // Obtém o nome do funcionário
            String nomeFuncionario = funcionario.getNome();

            // Cria um botão com o nome do funcionário como rótulo
            Button btn = new Button(nomeFuncionario);
            btn.setPrefSize(225, 112.5);
            btn.setStyle("-fx-background-color: #67c2ff; -fx-text-fill: white; -fx-font-size: 16");
            btn.setOnAction(e -> {
                // Verifica se há uma sessão de utilizador ativa e a termina, se existir
                if (SessaoUtilizador.getInstance() != null) {
                    SessaoUtilizador.getInstance().terminarSessaoUtilizador();
                }
                // Verifica a palavra-passe e continua com a sessão
                verificarPasswordESeguir(nomeFuncionario, conexao);
            });

            // Adiciona o botão ao container (funcionarioPane) na interface gráfica
            funcionarioPane.getChildren().add(btn);
        }
    }

    private void adicionarAnimacaoBotoes() {
        // Iterar sobre todos os filhos do container de botões
        for (int i = 0; i < funcionarioPane.getChildren().size(); i++) {
            // Verificar se o filho é um botão
            if (funcionarioPane.getChildren().get(i) instanceof Button) {
                // Se for um botão, adicionar animação
                Button button = (Button) funcionarioPane.getChildren().get(i);
                adicionarAnimacaoButton(button);
            }
        }
    }

    private SerialPort portaSerial;

    private void iniciarComunicacaoSerial() {
        // Obtém a lista de portas seriais disponíveis no sistema
        SerialPort[] portNames = SerialPort.getCommPorts();

        // ciclo das portas seriais disponíveis
        for (SerialPort port : portNames) {
            // Verifica se a descrição da porta contém "COM5"
            if (port.getDescriptivePortName().contains("COM5")) {
                // Abre a porta serial
                port.openPort();

                // Configura os timeouts da porta serial
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

                // Atribui a porta serial à variável global 'portaSerial'
                portaSerial = port;

                // Cria um leitor para a entrada da porta serial
                BufferedReader reader = new BufferedReader(new InputStreamReader(port.getInputStream()));

                // Inicia uma nova thread para ler continuamente da porta serial
                new Thread(() -> {
                    try {
                        // Loop para ler linhas da porta serial
                        while (true) {
                            String line = reader.readLine();
                            // Verifica se a linha não é nula
                            if (line != null) {
                                // Remove espaços em branco da linha
                                line = line.replace(" ", "");
                                System.out.println(line);
                                // Chama o método para verificar o funcionário com base no UID lido
                                verificarFuncionarioPorUID(line);
                            }
                        }
                    } catch (SerialPortIOException e) {
                        System.out.println("Conexão fechada da porta serial!");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
    }

    private void verificarFuncionarioPorUID(String uid) {
        try {
            System.out.println("UID recebido: " + uid);

            String query = "SELECT * FROM Funcionario WHERE id_cartao = ?";
            PreparedStatement statement = conexao.prepareStatement(query);
            statement.setString(1, uid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Funcionário encontrado na base de dados.");
                int idFuncionario = resultSet.getInt("id_funcionario");
                int cargo = resultSet.getInt("id_cargo");
                String nome = resultSet.getString("nome");

                System.out.println("Funcionário Encontrado:");
                System.out.println("Nome: " + nome);
                System.out.println("ID do Funcionário: " + idFuncionario);

                //Inicia sessão para este funcionário
                SessaoUtilizador sessao = SessaoUtilizador.getInstance(idFuncionario, cargo, nome);
                SessaoUtilizador.setInstance(sessao);
                System.out.println(sessao);

                //Abre uma nova janela
                abrirNovaJanela();
            } else {
                mostrarAlerta("Erro", "Funcionário não encontrado!");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            fecharPortaSerial();
        }
    }

    private void fecharPortaSerial() {
        if (portaSerial != null && portaSerial.isOpen()) {
            portaSerial.closePort();
        }
    }

    private void adicionarAnimacaoButton(Button button) {
        //Cria uma nova transição de escala
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setByX(0.1);
        scaleTransition.setByY(0.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);

        //Adiciona um ouvinte ao evento de pressionar o botão
        button.setOnMousePressed(e -> scaleTransition.play());
    }


    private void verificarPasswordESeguir(String nomeFuncionario, Connection conexao) {
        // Cria um diálogo personalizado
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Autenticação");
        dialog.setHeaderText("Insira a password para " + nomeFuncionario + ": ");

        dialog.getDialogPane().setMinWidth(400);

        // Configura o botão OK
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Cria o campo de texto da password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Foca no campo de password quando o diálogo é exibido
        Platform.runLater(() -> passwordField.requestFocus());

        // Desativa o botão OK se o campo de password estiver vazio
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        // Adicion um listener ao campo de password para ativar/desativar o botão OK
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Cria uma label para a mensagem de erro
        Label errorMessage = new Label();
        errorMessage.setTextFill(Color.RED);

        // Adiciona o campo de password e a mensagem de erro ao diálogo
        VBox vbox = new VBox(passwordField, errorMessage);
        dialog.getDialogPane().setContent(vbox);

        // Adicionar um EventHandler ao botão OK para verificar a password
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!verificarPasswordBD(nomeFuncionario, passwordField.getText(), conexao)) {
                // Se a password estiver incorreta, mostrara mensagem de erro e limpa o campo de password
                errorMessage.setText("Password incorreta. Tente novamente.");
                passwordField.clear();
                event.consume();  // Consume o evento para evitar que o diálogo seja fechado
            }
        });

        // Adiciona um EventHandler ao botão "Cancelar" para fechar o diálogo
        Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> dialog.close());

        // Mostra o diálogo
        dialog.showAndWait();
    }


    private boolean verificarPasswordBD(String nomeFuncionario, String password, Connection conexao) {
        try {
            // Preparar a consulta SQL
            String query = "SELECT * FROM Funcionario WHERE nome = ? AND password = ?";
            PreparedStatement statement = conexao.prepareStatement(query);
            statement.setString(1, nomeFuncionario);
            statement.setString(2, password);
            // Executar a consulta e obter os resultados
            ResultSet resultSet = statement.executeQuery();

            // Se o resultado estiver presente, criar uma nova sessão de utilizador
            if (resultSet.next()) {
                int cargo = resultSet.getInt("id_cargo");
                int id_utilizador = resultSet.getInt("id_funcionario");
                String nome = resultSet.getString("nome");

                SessaoUtilizador sessao = SessaoUtilizador.getInstance(id_utilizador, cargo, nome);
                SessaoUtilizador.setInstance(sessao);
                System.out.println(sessao);

                // Abrir uma nova janela
                abrirNovaJanela();

                // Fechar o resultSet e o statement
                resultSet.close();
                statement.close();

                return true;
            } else {
                // Se a password estiver incorreta, retornar false
                return false;
            }
        } catch (SQLException ex) {
            // Imprimir o stack trace em caso de exceção
            ex.printStackTrace();
            return false;
        }
    }

    private void abrirNovaJanela() {
        try {
            // Obter a sessão do usuário
            SessaoUtilizador sessao = SessaoUtilizador.getInstance();
            int cargo = sessao.getCargo();

            // Se o cargo for 1, carregar a interface do gerente
            if (cargo == 1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/GerenteBarraLateral.fxml"));
                Parent root = loader.load();

                Scene scene = funcionarioPane.getScene();
                scene.setRoot(root);
            } else if (cargo == 3) {
                // Caso contrário, carregar a interface das mesas
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Balcao.fxml"));
                Parent root = loader.load();

                dashboardBalcaoController cozinha = loader.getController();
                cozinha.configurarBotaoTerminarSessao();

                Scene scene = funcionarioPane.getScene();
                scene.setRoot(root);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Mesas.fxml"));
                Parent root = loader.load();

                DashboardMesaController mesas = loader.getController();
                mesas.configurarBotaoTerminarSessao();
                mesas.configurarTitulo();

                Scene scene = funcionarioPane.getScene();
                scene.setRoot(root);
            }

        } catch (IOException e) {
            // Imprimir o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    /**
     * Exibe uma caixa de diálogo de alerta.
     * Cria e exibe um alerta com o tipo ERROR, exibindo o título e cabeçalho especificados.
     */
    private void mostrarAlerta(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }
}