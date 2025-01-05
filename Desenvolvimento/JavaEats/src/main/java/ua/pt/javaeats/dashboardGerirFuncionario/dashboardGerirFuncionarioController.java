package ua.pt.javaeats.dashboardGerirFuncionario;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import ua.pt.javaeats.*;
import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.cell.PropertyValueFactory;


public class dashboardGerirFuncionarioController {
    @FXML
    private ComboBox<Cargo> cargoComboBox;
    private ObservableList<Cargo> cargosList = FXCollections.observableArrayList();
    /**
     * Elementos da interface gráfica e conexão com a base de dados.
     */
    @FXML private TableView<Funcionario> tableViewFuncionarios;
    @FXML private TextField nomeField;
    @FXML private TextField passwordField;
    @FXML private TextField idField;
    @FXML private TextField cartaoField;
    @FXML private TextField idCargoField;
    private Connection conexao;
    private GerirBD gerirBD;
    private Funcionario funcionarioParaEditar;

    public void initialize() throws SQLException {

        iniciarComunicacaoSerial();
        inicializarConexao(); //Estabelece a ligação à base de dados.
        if (conexao != null) {
            carregarFuncionarios();
            List<Cargo> cargos = gerirBD.importar_cargo();
            cargosList.addAll(cargos);
            cargoComboBox.setItems(cargosList);
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


    private void iniciarComunicacaoSerial() {
        //Obtém uma instância da porta serial com as configurações do arduino
        SerialPort portaSerial = configurarPortaSerial();

        if (portaSerial.openPort()) {
            //Cria um buffer para ler os dados da porta serial
            BufferedReader reader = new BufferedReader(new InputStreamReader(portaSerial.getInputStream()));
            //Inicia uma nova thread para realizar a leitura contínua dos dados da porta serial
            new Thread(() -> {
                try {
                    while (true) {
                        //Lê uma linha da porta serial (espera até encontrar um "enter")
                        String line = reader.readLine();
                        // Verifica se a linha lida não é nula
                        if (line != null) {
                            //Remove espaços em branco da linha
                            line = line.replace(" ", "");
                            //Atualiza o último UID na variável global
                            SerialCommunicationService.setUltimoUID(line);
                            //Verificar e processar o funcionário com base no UID lido
                            SerialCommunicationService.verificarFuncionarioPorUID(line);
                        }
                    }
                } catch (SerialPortIOException e) {
                    //Trata exceções relacionadas à entrada/saída na porta serial
                    System.out.println("Erro de E/S na porta serial: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Erro de I/O: " + e.getMessage());
                } catch (Exception e) {
                    //Trata exceções não identificadas
                    System.out.println("Erro não identificado: " + e.getMessage());
                }
            }).start();
        } else {
            System.out.println("Não foi possível abrir a porta serial.");
        }
    }


    private SerialPort configurarPortaSerial() {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Portas Seriais Disponíveis:");
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }
        //Configuração do Arduino
        SerialPort portaSerial = SerialPort.getCommPort("COM5");
        portaSerial.setBaudRate(9600);
        //Tempo limite de leitura
        portaSerial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 180000, 0);

        return portaSerial;
    }





    private void inicializarConexao() throws SQLException {

        ConectarBD conectarBD = new ConectarBD(); //Cria uma instância da classe ConectarBD.
        conexao = conectarBD.conectar(); //Estabelece a conexão e a armazena na variável 'conexao'.
       if (conexao != null) {
            inicializarGerirBD(); //Se a conexão for bem-sucedida, inicializa o gerir da base de dados.
     //   } else {
       //     throw new SQLException("Falha ao conectar-se à base de dados.");
        }
    }

    private void inicializarGerirBD() {
        gerirBD = new GerirBD(); //Cria uma nova instância de GerirBD.
        gerirBD.ConectarBD(conexao); //Estabelece a conexão com a base de dados no gerirBD.
    }

    private void carregarFuncionarios() {
        if (conexao != null) {
                // Obtém a lista de funcionários da base de dados através do GerirBD
                List<Funcionario> funcionarios = gerirBD.importar_funcionario();

                // Adiciona os funcionários à TableView para exibição na interface gráfica
                tableViewFuncionarios.getItems().addAll(funcionarios);

        }
    }



    @FXML
    public void criarFuncionario() {
        if (conexao != null) {
            String nome = nomeField.getText();
            String password = passwordField.getText();
            Cargo cargoSelecionado = cargoComboBox.getValue();
            int idCargo = cargoSelecionado.getIdCargo();
            String idCartao = cartaoField.getText();
            //Cria um novo funcionário com os dados fornecidos
            Funcionario novoFuncionario = new Funcionario(nome, password, idCargo, idCartao, conexao);
            //Salva o novo funcionário na base de dados utilizando o GerirBD
            gerirBD.guardar_funcionario(novoFuncionario);
            //Adiciona o novo funcionário à TableView para exibição na interface gráfica
            tableViewFuncionarios.getItems().add(novoFuncionario);
            tableViewFuncionarios.refresh();
            mostrarAlerta("SUCESSO", "Funcionario criado com sucesso!");
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
    public void editarFuncionario() {
        if (conexao != null) {
            //Obtém o funcionário selecionado na TableView
            Funcionario funcionarioSelecionado = tableViewFuncionarios.getSelectionModel().getSelectedItem();

            //Verifica se há algum funcionário selecionado para edição
            if (funcionarioSelecionado != null) {
                //Preenche os campos de texto com os detalhes do funcionário selecionado

                preencherCampos(funcionarioSelecionado);
                //Define o funcionário selecionado como funcionário para edição
                funcionarioParaEditar = funcionarioSelecionado;
                cartaoField.setText(funcionarioSelecionado.getIdCartao());
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
    public void removerFuncionario() {
        //Obtém o funcionário selecionado na TableView para remoção
        Funcionario funcionarioSelecionado = tableViewFuncionarios.getSelectionModel().getSelectedItem();

        //Verifica se há algum funcionário selecionado para remoção
        if (funcionarioSelecionado != null) {
            boolean confirmacao = mostrarConfirmacao("CONFIRMAÇÃO", "Tem a certeza que pretende eliminar permanentemente este funcionario?");
            if (confirmacao) {
                //Remove o funcionário selecionado da base de dados
                gerirBD.remover_funcionario(funcionarioSelecionado);
                //Remove o funcionário selecionado da TableView para refletir a remoção na interface gráfica
                tableViewFuncionarios.getItems().remove(funcionarioSelecionado);
                mostrarAlerta("SUCESSO", "Funcionario eliminado!");
            }
        }
    }

    @FXML
    public void atualizarFuncionario(ActionEvent actionEvent) {
        if (conexao != null) {
            //Verifica se há um funcionário em edição
            if (funcionarioParaEditar != null) {
                //Obtém os novos valores dos campos de texto para atualização
                String novoNome = nomeField.getText();
                String novaSenha = passwordField.getText();
                Cargo novoCargo = cargoComboBox.getValue();
                int novoIdCargo = novoCargo.getIdCargo();
                String novoIdCartao = cartaoField.getText();

                //Define os novos valores para o funcionário em edição
                funcionarioParaEditar.setNome(novoNome);
                funcionarioParaEditar.setPassword(novaSenha);
                funcionarioParaEditar.setIdCargo(novoIdCargo);
                funcionarioParaEditar.setIdCartao(novoIdCartao);

                //Atualiza os dados do funcionário na base de dados
                gerirBD.editar_funcionario(funcionarioParaEditar);
                //Atualiza a TableView com os dados atualizados do funcionário
                int selectedIndex = tableViewFuncionarios.getSelectionModel().getSelectedIndex();
                tableViewFuncionarios.getItems().set(selectedIndex, funcionarioParaEditar);
                //Limpa os campos de texto após a atualização e indica que a edição foi concluída
                limparCamposTexto();
                funcionarioParaEditar = null;
                mostrarAlerta("SUCESSO", "Funcionario editado!");
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
     * Preenche os campos de texto com os detalhes do funcionário selecionado na TableView.
     * Define os valores dos campos de texto com o nome, password e identificação do cargo do funcionário.
     */
    private void preencherCampos(Funcionario funcionario) {
        nomeField.setText(funcionario.getNome());
        passwordField.setText(funcionario.getPassword());
        cargoComboBox.setValue(gerirBD.getCargoById(funcionario.getIdCargo()));
        cartaoField.setText(funcionario.getIdCartao());
    }

    /**
     * Limpa os campos de texto do nome, password e identificação do cargo.
     */
    private void limparCamposTexto() {
        nomeField.clear();
        passwordField.clear();
        cargoComboBox.setValue(null);
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

    public boolean mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void procurarCartao(ActionEvent actionEvent) {
        // Obter o último UID do cartão lido na consola
        String ultimoUID = obterUltimoUID();

        // Verificar se o UID foi encontrado
        if (ultimoUID != null) {
            // Definir o UID no cartaoField
            cartaoField.setText(ultimoUID);
        } else {
            // Se não houver UID, exibir uma mensagem de erro ou tomar a ação apropriada
            mostrarAlerta("Cartão", "Nenhum cartão (UID) encontrado.");
        }
    }


    private String obterUltimoUID() {
        // Retorna o último UID armazenado na variável global do SerialCommunicationService
        return SerialCommunicationService.getUltimoUID();
    }

    public void iniciarComunicacaoSerialExterno() {
        iniciarComunicacaoSerial();
    }
}
