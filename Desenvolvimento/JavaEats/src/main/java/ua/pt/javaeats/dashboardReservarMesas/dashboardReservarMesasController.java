package ua.pt.javaeats.dashboardReservarMesas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.LocalTimeStringConverter;
import ua.pt.javaeats.ConectarBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class dashboardReservarMesasController {
    public Spinner timeSpinner;
    /**
     * Elementos da interface gráfica e conexão com a base de dados.
     */
    @FXML
    private ComboBox<String> comboBoxMesas;
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> comboBoxMesasReservadas;
    private ConectarBD conexaoBD;
    private Connection conexao;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public dashboardReservarMesasController() {
        conexaoBD = new ConectarBD();
        conexao = conexaoBD.conectar();
    }

    @FXML
    public void initialize() throws SQLException {
        conexaoBD = new ConectarBD();
        conexao = conexaoBD.conectar();
        if (conexao != null) {
            carregarMesasDisponiveis();
            carregarMesasReservadas();
            inicializarTimeSpinner();
        } else {
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
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

    private void inicializarTimeSpinner() {
        // Configurar o valor do TimeSpinner
        timeSpinner.setValueFactory(new SpinnerValueFactory<LocalTime>() {
            {
                // Converter o valor para uma string no formato "HH:mm"
                setConverter(new LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm"), null));
            }

            @Override
            public void decrement(int steps) {
                // Se o valor atual for nulo, definir para a hora atual arredondada para a hora mais próxima
                if (getValue() == null)
                    setValue(LocalTime.now().truncatedTo(ChronoUnit.HOURS));
                else {
                    // Caso contrário, diminuir o valor atual em 15 minutos multiplicado pelo número de passos
                    LocalTime time = getValue();
                    setValue(time.minusMinutes(15 * steps));
                }
            }

            @Override
            public void increment(int steps) {
                // Se o valor atual for nulo, definir para a hora atual arredondada para a hora mais próxima
                if (this.getValue() == null)
                    setValue(LocalTime.now().truncatedTo(ChronoUnit.HOURS));
                else {
                    // Caso contrário, aumentar o valor atual em 15 minutos multiplicado pelo número de passos
                    LocalTime time = getValue();
                    setValue(time.plusMinutes(15 * steps));
                }
            }
        });
        // Permitir que o TimeSpinner seja editável
        timeSpinner.setEditable(true);
    }


    private void carregarMesasDisponiveis() {
        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Consulta SQL para obter as mesas disponíveis
                String query = "SELECT id_mesa FROM Mesa WHERE status = 'livre'";
                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Adicionar as mesas disponíveis ao comboBox
                while (resultSet.next()) {
                    String idMesa = resultSet.getString("id_mesa");
                    comboBoxMesas.getItems().add("Mesa " + idMesa);
                }

                // Fechar o resultSet e o preparedStatement
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }

    @FXML
    public void selecionarDataHora(ActionEvent event) {
        // Verificar se a data, a hora e a mesa foram selecionadas
        if (datePicker.getValue() != null && timeSpinner.getValue() != null && comboBoxMesas.getValue() != null) {
            // Obter a data, a hora e a mesa selecionadas
            String data = datePicker.getValue().toString();
            String hora = timeSpinner.getValue().toString();
            String mesaSelecionada = comboBoxMesas.getValue();

            // Imprimir a data, a hora e a mesa selecionadas
            System.out.println("Data da reserva: " + data);
            System.out.println("Hora da reserva: " + hora);
            System.out.println("Mesa selecionada: " + mesaSelecionada);

        } else {
            // Imprimir uma mensagem se a data, a hora ou a mesa não forem selecionadas
            System.out.println("Por favor, selecione data, hora e mesa para a reserva.");
        }
    }

    @FXML
    public void efetuarReserva(ActionEvent event) {
        // Verificar se a data, a hora e a mesa foram selecionadas
        if (datePicker.getValue() != null && timeSpinner.getValue() != null && comboBoxMesas.getValue() != null) {
            try {
                // Obter a mesa selecionada, a data e a hora da reserva
                String mesaSelecionada = comboBoxMesas.getValue();
                LocalDate dataReserva = datePicker.getValue();
                String horaReserva = timeSpinner.getValue().toString();

                // Converter a hora da reserva para LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime timeReserva = LocalTime.parse(horaReserva, formatter);
                LocalDateTime dateTimeReserva = LocalDateTime.of(dataReserva, timeReserva);

                // Verificar se a data e a hora da reserva são anteriores à atual
                if (dateTimeReserva.isBefore(LocalDateTime.now())) {
                    // Mostrar um alerta se a data e/ou a hora da reserva forem anteriores à atual
                    mostrarAlerta("Erro", "A data e/ou hora da reserva são anteriores à atual.");
                    return;
                }

                // Verificar se a reserva já existe
                if (reservaExiste(mesaSelecionada, dateTimeReserva)) {
                    // Mostrar um alerta se já existir uma reserva para esta mesa na data e hora selecionadas
                    mostrarAlerta("Erro", "Já existe uma reserva para esta mesa na data e hora selecionadas.");
                    return;
                }

                // Criar a reserva instantaneamente
                atualizarStatusMesa(mesaSelecionada, dataReserva, horaReserva);

                // Calcular o atraso para reservar e libertar a mesa
                long delayParaReservar = ChronoUnit.MINUTES.between(LocalDateTime.now(), dateTimeReserva.minusMinutes(30));
                long delayParaLibertar = ChronoUnit.MINUTES.between(LocalDateTime.now(), dateTimeReserva.plusMinutes(20));

                // Agendar a tarefa de atualizar o status da mesa
                scheduler.schedule(() -> {
                    reservarMesa(mesaSelecionada);
                }, delayParaReservar, TimeUnit.MINUTES);

                // Agendar a tarefa de reverter o status da mesa
                scheduler.schedule(() -> {
                    reverterStatusMesa(mesaSelecionada);
                }, delayParaLibertar, TimeUnit.MINUTES);
                // Mostrar alerta de sucesso
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Mesa reservada!");
                alert.showAndWait();
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao efetuar a reserva: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Erro", "Por favor, selecione data, hora e mesa para a reserva.");
        }
    }


    private boolean reservaExiste(String mesaSelecionada, LocalDateTime dateTimeReserva) {
        // Inicializar a variável que indica se a reserva existe
        boolean reservaExiste = false;
        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Consulta SQL para verificar se a reserva existe
                String selectQuery = "SELECT * FROM Reservas WHERE data_reserva = ? AND hora_reserva = ? AND id_mesa = ?";
                PreparedStatement preparedStatement = conexao.prepareStatement(selectQuery);
                preparedStatement.setDate(1, java.sql.Date.valueOf(dateTimeReserva.toLocalDate()));
                preparedStatement.setTime(2, java.sql.Time.valueOf(dateTimeReserva.toLocalTime()));
                preparedStatement.setInt(3, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));

                ResultSet resultSet = preparedStatement.executeQuery();
                // Se houver resultados, a reserva existe
                if (resultSet.next()) {
                    reservaExiste = true;
                }

                // Fechar o resultSet e o preparedStatement
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados.");
        }
        return reservaExiste;
    }


    private void atualizarStatusMesa(String mesaSelecionada, LocalDate dataReserva, String horaReserva) {
        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Desativar o auto-commit para iniciar uma transação
                conexao.setAutoCommit(false);
                // Consulta SQL para inserir a reserva na base de dados
                String updateQuery = "INSERT INTO Reservas (id_mesa, data_reserva, hora_reserva) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = conexao.prepareStatement(updateQuery);
                preparedStatement.setInt(1, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));
                preparedStatement.setString(2, dataReserva.toString());
                preparedStatement.setString(3, horaReserva);
                int rowsAffected = preparedStatement.executeUpdate();

                // Se a consulta afetou alguma linha, a reserva foi efetuada com sucesso
                if (rowsAffected > 0) {
                    System.out.println("Reserva efetuada com sucesso!");
                    // Confirmar a transação
                    conexao.commit();
                    // Reativar o auto-commit
                    conexao.setAutoCommit(true);
                } else {
                    System.out.println("Nenhuma linha afetada. Verifique o ID da mesa e a query de atualização.");
                }

                // Fechar o preparedStatement
                preparedStatement.close();
            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            System.out.println("Conexão com o banco de dados não foi estabelecida.");
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }


    private void reservarMesa(String mesaSelecionada) {
        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Desativar o auto-commit para iniciar uma transação
                conexao.setAutoCommit(false);
                // Consulta SQL para atualizar o status da mesa para 'Reservada'
                String updateQuery = "UPDATE Mesa SET status = 'Reservada' WHERE id_mesa = ?";
                PreparedStatement preparedStatement = conexao.prepareStatement(updateQuery);
                preparedStatement.setInt(1, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));
                int rowsAffected = preparedStatement.executeUpdate();

                // Se a consulta afetou alguma linha, o status da mesa foi atualizado com sucesso
                if (rowsAffected > 0) {
                    System.out.println("Status da mesa " + mesaSelecionada + " atualizado para 'Reservado'");
                    // Confirmar a transação
                    conexao.commit();
                    // Reativar o auto-commit
                    conexao.setAutoCommit(true);
                } else {
                    System.out.println("Nenhuma linha afetada. Verifique o ID da mesa e a query de atualização.");
                }

                // Fechar o preparedStatement
                preparedStatement.close();
            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            System.out.println("Conexão com o banco de dados não foi estabelecida.");
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }

    private void reverterStatusMesa(String mesaSelecionada) {
        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Desativar o auto-commit para iniciar uma transação
                conexao.setAutoCommit(false);

                // Consulta SQL para verificar o status atual da mesa
                String checkStatusQuery = "SELECT status FROM Mesa WHERE id_mesa = ?";
                PreparedStatement checkStatusStatement = conexao.prepareStatement(checkStatusQuery);
                checkStatusStatement.setInt(1, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));
                ResultSet checkStatusResult = checkStatusStatement.executeQuery();

                // Se houver resultados, obter o status atual
                if (checkStatusResult.next()) {
                    String statusAtual = checkStatusResult.getString("status");

                    // Se o status atual for 'Reservada', reverter para 'Livre'
                    if (statusAtual.equals("Reservada")) {
                        String updateQuery = "UPDATE Mesa SET status = 'Livre' WHERE id_mesa = ?";
                        PreparedStatement preparedStatement = conexao.prepareStatement(updateQuery);
                        preparedStatement.setInt(1, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));
                        int rowsAffected = preparedStatement.executeUpdate();

                        // Se a consulta afetou alguma linha, o status da mesa foi revertido com sucesso
                        if (rowsAffected > 0) {
                            System.out.println("Status da mesa " + mesaSelecionada + " revertido para 'livre'");
                            // Confirmar a transação
                            conexao.commit();
                            // Reativar o auto-commit
                            conexao.setAutoCommit(true);
                        } else {
                            System.out.println("Nenhuma linha afetada. Verifique o ID da mesa e a query de atualização.");
                        }
                    }
                }

            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            System.out.println("Conexão com o base de dados não foi estabelecida.");
        }
    }


    @FXML
    public void reservarMesa(MouseEvent mouseEvent) {
        // Chamar o método efetuarReserva quando o botão de reservar mesa for clicado
        efetuarReserva(null);
    }


    @FXML
    public void cancelarReservarMesa(MouseEvent mouseEvent) {
        // Obter a mesa selecionada
        String mesaSelecionada = comboBoxMesasReservadas.getValue();
        // Verificar se a mesa foi selecionada e se a conexão com a base de dados está disponível
        if (mesaSelecionada != null && conexao != null) {
            try {
                // Desativar o auto-commit para iniciar uma transação
                conexao.setAutoCommit(false);
                // Consulta SQL para atualizar o status da mesa para 'Livre'
                String updateQuery = "UPDATE Mesa SET status = 'Livre' WHERE id_mesa = ?";
                PreparedStatement preparedStatement = conexao.prepareStatement(updateQuery);
                preparedStatement.setInt(1, Integer.parseInt(mesaSelecionada.replaceAll("\\D", "")));
                int rowsAffected = preparedStatement.executeUpdate();

                // Se a consulta afetou alguma linha, o status da mesa foi atualizado com sucesso
                if (rowsAffected > 0) {
                    System.out.println("Status da mesa " + mesaSelecionada + " atualizado para 'livre'");
                    // Mostrar um alerta de sucesso
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText(null);
                    alert.setContentText("Reserva cancelada!");
                    alert.showAndWait();
                    // Confirmar a transação
                    conexao.commit();
                    // Reativar o auto-commit
                    conexao.setAutoCommit(true);
                } else {
                    System.out.println("Nenhuma linha afetada. Verifique o ID da mesa e a query de atualização.");
                }

                // Fechar o preparedStatement
                preparedStatement.close();
            } catch (SQLException e) {
                // Mostrar um alerta se ocorrer um erro SQL
                mostrarAlerta("ERRO", "Falha na conexão com a base de dados.");
            }
        } else {
            // Mostrar um alerta se a mesa não for selecionada
            mostrarAlerta("Erro", "Por favor, selecione uma mesa reservada para cancelar a reserva.");

        }
    }

    private void carregarMesasReservadas() {
        // Limpar os itens do comboBox
        comboBoxMesasReservadas.getItems().clear();

        // Verificar se a conexão com a base de dados está disponível
        if (conexao != null) {
            try {
                // Consulta SQL para obter as mesas reservadas
                String query = "SELECT id_mesa FROM Mesa WHERE status = 'reservada'";
                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Adicionar as mesas reservadas ao comboBox
                while (resultSet.next()) {
                    String idMesa = resultSet.getString("id_mesa");
                    comboBoxMesasReservadas.getItems().add("Mesa " + idMesa);
                }

                // Fechar o resultSet e o preparedStatement
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                // Imprimir a pilha de exceções se ocorrer um erro SQL
                e.printStackTrace();
            }
        } else {
            // Mostrar um alerta se a conexão com a base de dados falhar
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
        }
    }
}