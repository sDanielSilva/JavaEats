package ua.pt.javaeats.dashboardPedido;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ua.pt.javaeats.*;
import ua.pt.javaeats.DashboardFecharConta.DashboardFecharContaController;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;
import javafx.scene.layout.Pane;
import ua.pt.javaeats.dashboardMesa.DashboardMesaController;

public class DashboardPedidoController {

    @FXML
    private Pane itemsMostrar;
    @FXML
    private Pane categoriaPane;
    @FXML
    private Label numeroMesa;
    @FXML
    private Label mostrarReserva;
    @FXML
    private Label labelTotalPedido;
    @FXML
    private TableView<ItemPedido> tableView;
    @FXML
    private TableColumn<ItemPedido, String> produtoColumn;
    @FXML
    private TableColumn<ItemPedido, Integer> quantidadeColumn;
    @FXML
    private TableColumn<ItemPedido, Double> precoColumn;
    @FXML
    private TextField pesquisarItem;
    @FXML
    private Pane voltar_menu_mesas;
    private int mesaId;
    private int categoriaSelecionadaId = -1; // Inicializa com -1 para indicar que nenhuma categoria está selecionada
    private final ObservableList<ItemPedido> listaItens = FXCollections.observableArrayList();
    private final List<ItemPedido> listaItensOriginal = new ArrayList<>();
    private Timer timer; // Timer para atrasar a pesquisa
    private static final long DELAY = 500; // Atraso em milissegundos
    private String filtroAtual = ""; // Salva o filtro atual
    private boolean paginaCarregada = false;
    // Crie uma variável para rastrear as quantidades dos itens
    private final IntegerProperty totalQuantidades = new SimpleIntegerProperty(0);
    private DashboardMesaController mesaController;

    @FXML
    private void voltar_menu_mesas() {
        try {
            // Obter a sessão do usuário
            SessaoUtilizador sessao = SessaoUtilizador.getInstance();
            int cargo = sessao.getCargo();
            // Se o cargo for 1, carregar a interface do gerente
            if (cargo == 1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/GerenteBarraLateral.fxml"));
                Parent root = loader.load();

                Scene scene = voltar_menu_mesas.getScene();
                scene.setRoot(root);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Mesas.fxml"));
                Parent root = loader.load();

                DashboardMesaController mesas = loader.getController();
                mesas.configurarBotaoTerminarSessao();
                mesas.configurarTitulo();

                Scene scene = voltar_menu_mesas.getScene();
                scene.setRoot(root);
            }

        } catch (IOException e) {
            // Imprimir o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    @FXML
    private void consultaDeMesa() {

        // Verificar se há itens na tabela da mesa
        if (tableView.getItems().isEmpty()) {
            exibirAlerta("Consulta de Mesa", "Não há itens na mesa para gerar a respetiva consulta.");
        } else {
            exibirAlerta("Consulta de Mesa", "Consulta da mesa " + mesaId + " (atendimento " + obterIdPagamento(mesaId) + ") gerada com sucesso.");
            gerarConsultaDeMesaFormatoPDF();
        }
    }

    //Método para gerar a consulta de mesa em formato PDF
    private void gerarConsultaDeMesaFormatoPDF() {
        Document document = new Document();
        String imageSrc = getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm();
        String nomeFuncionario = SessaoUtilizador.getInstance().getNomeFuncionario();
        String nomePasta = "RecibosConsultasDeMesa";
        String nomeArquivoPDF = nomePasta + "/consulta_mesa" + mesaId + "_atendimento" + obterIdPagamento(mesaId) + ".pdf";

        try {
            File pasta = new File(nomePasta);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivoPDF));
            document.open();

            // Adicionar imagem ao documento
            Image imagem = Image.getInstance(imageSrc);
            document.add(imagem);

            // Adicionar conteúdo do recibo ao documento
            document.add(new Paragraph("JavaEats - Restaurante"));
            document.add(new Paragraph("Endereço: R. Cmte. Pinho e Freitas 28, 3750-127 Águeda"));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Número da Mesa: " + mesaId));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Data do Recibo: " + obterDataAtual()));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Funcionário: " + nomeFuncionario));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Itens pedidos:"));
            document.add(new Paragraph(" "));

            // Tabela para listar os itens
            PdfPTable tabelaItens = new PdfPTable(4);
            tabelaItens.setWidthPercentage(85.5F);
            tabelaItens.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabelaItens.addCell("Produto");
            tabelaItens.addCell("Quantidade");
            tabelaItens.addCell("Preço Unitário");
            tabelaItens.addCell("Preço Total Item");

            // Obtém os itens da primeira TableView (tableViewFecharConta)
            ObservableList<ItemPedido> itensConta = tableView.getItems();
            double total = 0.0;
            for (ItemPedido item : itensConta) {
                double precoTotal = item.getPreco() * item.getQuantidade();
                total += precoTotal;

                tabelaItens.addCell(item.getProduto());
                tabelaItens.addCell(String.valueOf(item.getQuantidade()));
                tabelaItens.addCell(String.format("%.2f €", item.getPreco()));
                tabelaItens.addCell(String.format("%.2f €", precoTotal));
            }

            document.add(tabelaItens);

            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Total: " + formatarTotal(total)));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Obrigado pela preferência!"));

        } catch (Exception e) {
            exibirAlerta("Erro", "Ocorreu um erro ao gerar a consulta de mesa: " + e.getMessage());
        } finally {
            document.close();
        }
    }

    @FXML
    private void transferirMesas() {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        //Se a mesa for livre ou reservada não deixar transferir
        if (!verificarStatusMesa(mesaId)) {
            exibirAlerta("Transferência de Mesa", "Não é possível transferir a partir de uma mesa livre ou reservada");
            return;
        }

        // Crie um diálogo personalizado
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Transferência de Mesa");
        dialog.setHeaderText("Para qual mesa deseja transferir?");
        dialog.getDialogPane().setMinWidth(510);

        // Configurar o botão OK
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Criar um label para a mensagem de erro
        Label errorMessage = new Label();
        errorMessage.setTextFill(Color.RED);

        // Configurar o conteúdo do diálogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Adicionar a ComboBox ao GridPane
        ComboBox<String> comboBoxMesas = new ComboBox<>();
        comboBoxMesas.setPromptText("Selecione uma mesa");
        comboBoxMesas.setStyle("-fx-background-color: #85f3ff;");
        grid.add(new Label("Mesas livres:"), 0, 0);
        grid.add(comboBoxMesas, 1, 0);
        grid.add(errorMessage, 1, 1);  // Adicionar o label de erro

        if (conexao != null) {
            try {
                String query = "SELECT id_mesa FROM Mesa WHERE status = 'livre' AND id_mesa != ?";
                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                preparedStatement.setInt(1, mesaId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String idMesa = resultSet.getString("id_mesa");
                    comboBoxMesas.getItems().add(idMesa);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }

        // Adicionar um EventHandler ao botão OK para verificar a password
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String mesaSelecionada = comboBoxMesas.getValue();

            // Verifique se uma mesa foi selecionada
            if (mesaSelecionada == null || mesaSelecionada.isEmpty()) {
                // Exibir a mensagem de erro
                errorMessage.setText("Nenhuma mesa selecionada. Por favor, escolha uma mesa.");
                event.consume();
            } else {
                // Mesa selecionada não é nula ou vazia, então converta para um inteiro
                transferenciaEntreMesas(mesaId, Integer.parseInt(mesaSelecionada));
                tableView.getItems().clear();
                double totalPedido = calcularTotal(tableView.getItems());
                formatarTotal(totalPedido);
                System.out.println("Transferência executada com sucesso da mesa " + mesaId + " para a mesa " + mesaSelecionada);
            }
        });

        // Adicionar um EventHandler ao botão "Cancelar" para fechar o diálogo
        Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> dialog.close());

        // Adicionar o GridPane ao conteúdo do DialogPane
        dialog.getDialogPane().setContent(grid);

        // Mostrar o diálogo
        dialog.showAndWait();
    }

    private void transferenciaEntreMesas(int mesaOrigemId, int mesaDestinoId) {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                // Iniciar a transação
                conexao.setAutoCommit(false);

                // Parte 1: Mover dados para a nova mesa
                // Obter o ID do pagamento existente da mesa de origem
                String queryObterPagamentoOrigem = "SELECT PR1.id_pagamento " +
                        "FROM Pedido_Restaurante PR1 " +
                        "INNER JOIN PagamentoAtendimento PA1 ON PA1.id_pagatendimento = PR1.id_pagamento " +
                        "INNER JOIN Mesa M1 ON M1.id_mesa = PA1.id_mesa " +
                        "WHERE M1.id_mesa = ? AND PA1.preco_total IS NULL LIMIT 1";
                PreparedStatement preparedStatementObterPagamentoOrigem = conexao.prepareStatement(queryObterPagamentoOrigem);
                preparedStatementObterPagamentoOrigem.setInt(1, mesaOrigemId);
                ResultSet resultSetPagamentoOrigem = preparedStatementObterPagamentoOrigem.executeQuery();

                // Mover os itens do pedido para a nova mesa e atualizar o status da mesa
                if (resultSetPagamentoOrigem.next()) {

                    String queryMoverItens = "UPDATE PedidoDetalhe PD " +
                            "JOIN Pedido_Restaurante PR1 ON PR1.id_pedido = PD.id_pedido " +
                            "JOIN PagamentoAtendimento PA1 ON PA1.id_pagatendimento = PR1.id_pagamento " +
                            "JOIN Mesa M1 ON M1.id_mesa = PA1.id_mesa " +
                            "SET PA1.id_mesa = ? " +
                            "WHERE PA1.id_mesa = ? AND PA1.preco_total IS NULL";
                    PreparedStatement preparedStatementMoverItens = conexao.prepareStatement(queryMoverItens);
                    preparedStatementMoverItens.setInt(1, mesaDestinoId);
                    preparedStatementMoverItens.setInt(2, mesaOrigemId);
                    preparedStatementMoverItens.executeUpdate();

                    // Parte 2: Remover dados da mesa de origem e atualizar o status das mesas
                    // Remover os itens do pedido da mesa de origem
                    String queryRemoverItens = "DELETE PD " +
                            "FROM PedidoDetalhe PD " +
                            "INNER JOIN Pedido_Restaurante PR1 ON PR1.id_pedido = PD.id_pedido " +
                            "INNER JOIN PagamentoAtendimento PA1 ON PA1.id_pagatendimento = PR1.id_pagamento " +
                            "INNER JOIN Mesa M1 ON M1.id_mesa = PA1.id_mesa " +
                            "WHERE PA1.id_mesa = ? AND PA1.preco_total IS NULL";
                    PreparedStatement preparedStatementRemoverItens = conexao.prepareStatement(queryRemoverItens);
                    preparedStatementRemoverItens.setInt(1, mesaOrigemId);
                    preparedStatementRemoverItens.executeUpdate();

                    // Atualizar o status das mesas
                    String queryAtualizarStatus = "UPDATE Mesa SET status = 'Livre' WHERE id_mesa = ?";
                    PreparedStatement preparedStatementAtualizarStatus = conexao.prepareStatement(queryAtualizarStatus);
                    preparedStatementAtualizarStatus.setInt(1, mesaOrigemId);
                    preparedStatementAtualizarStatus.executeUpdate();

                    String queryAtualizarStatusDestino = "UPDATE Mesa SET status = 'Ocupada' WHERE id_mesa = ?";
                    PreparedStatement preparedStatementAtualizarStatusDestino = conexao.prepareStatement(queryAtualizarStatusDestino);
                    preparedStatementAtualizarStatusDestino.setInt(1, mesaDestinoId);
                    preparedStatementAtualizarStatusDestino.executeUpdate();

                    // Confirmar a transação
                    conexao.commit();

                    // Fechar recursos
                    preparedStatementMoverItens.close();
                    preparedStatementRemoverItens.close();
                    preparedStatementAtualizarStatus.close();
                    preparedStatementAtualizarStatusDestino.close();
                }

                resultSetPagamentoOrigem.close();
                preparedStatementObterPagamentoOrigem.close();

            } catch (SQLException e) {
                // Lidar com erros (por exemplo, reverter a transação)
                if (conexao != null) {
                    try {
                        conexao.rollback();
                    } catch (SQLException rollbackException) {
                        rollbackException.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                // Restaurar o modo de autocommit
                try {
                    conexao.setAutoCommit(true);
                } catch (SQLException autoCommitException) {
                    autoCommitException.printStackTrace();
                }
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }

    @FXML
    private void imprimir2avia() {

        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                // Procurar o mais recente atendimento com preço total definido para essa mesa
                String query = "SELECT PagamentoAtendimento.*,Item.id_item, Item.nome AS nome_item, PedidoDetalhe.quantidade, Item.preco " +
                        "FROM PagamentoAtendimento " +
                        "INNER JOIN Pedido_Restaurante ON PagamentoAtendimento.id_pagatendimento = Pedido_Restaurante.id_pagamento " +
                        "INNER JOIN PedidoDetalhe ON Pedido_Restaurante.id_pedido = PedidoDetalhe.id_pedido " +
                        "INNER JOIN Item ON Item.id_item = PedidoDetalhe.id_item " +
                        "WHERE PagamentoAtendimento.id_mesa = ? AND PagamentoAtendimento.preco_total IS NOT NULL AND PagamentoAtendimento.data_hora_fim = ( " +
                        "    SELECT MAX(data_hora_fim) " +
                        "    FROM PagamentoAtendimento " +
                        "    WHERE id_mesa = ? AND preco_total IS NOT NULL ) ";

                System.out.println("Query: " + query);

                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                preparedStatement.setInt(1, mesaId);
                preparedStatement.setInt(2, mesaId); // Adicionando novamente para o segundo placeholder
                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    exibirAlerta("Atendimento não encontrado", "Não foi possível imprimir a 2ª via do recibo, não foi encontrado um atendimento finalizado para a mesa " + mesaId);
                } else {
                    String metodoPagamento = resultSet.getString("tipo_pagamento");
                    exibirAlerta("2ª Via Recibo", "2ª via do recibo do atendimento " + obterIdAtendimentoFinalizadoMaisRecente(mesaId) + " da mesa " + mesaId + " gerada com sucesso.");
                    gerar2aViaReciboFormatoPDF(metodoPagamento, resultSet);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                bd.desconectar();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }

    //Método para gerar a 2º via do recibo
    private void gerar2aViaReciboFormatoPDF(String metodoPagamento, ResultSet resultSet) throws SQLException {
        Document document = new Document();
        String imageSrc = getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm();
        String nomeFuncionario = SessaoUtilizador.getInstance().getNomeFuncionario();
        String nomePasta = "Recibos2aVia";
        String nomeArquivoPDF = nomePasta + "/recibo2aVia_atendimento" + obterIdAtendimentoFinalizadoMaisRecente(mesaId) + ".pdf";

        int idPagamentoAtendimento = resultSet.getInt("id_pagatendimento");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String data_hora_inicio = dateFormat.format(resultSet.getTimestamp("data_hora_inicio"));
        String data_hora_fim = dateFormat.format(resultSet.getTimestamp("data_hora_fim"));
        double precoTotal = resultSet.getDouble("preco_total");

        try {
            File pasta = new File(nomePasta);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivoPDF));
            document.open();

            // Adicionar imagem ao documento
            Image imagem = Image.getInstance(imageSrc);
            document.add(imagem);

            // Adicionar conteúdo do recibo ao documento
            document.add(new Paragraph("Duplicado/2ª via"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("JavaEats - Restaurante"));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Endereço: R. Cmte. Pinho e Freitas 28, 3750-127 Águeda"));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Data: " + obterDataAtual()));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Funcionário: " + nomeFuncionario));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Método de Pagamento: " + metodoPagamento));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Atendimento: " + idPagamentoAtendimento + " (mesa " + mesaId + ")"));
            document.add(new Paragraph("Data e hora de início: " + data_hora_inicio));
            document.add(new Paragraph("Data e hora de fim: " + data_hora_fim));
            document.add(new Paragraph("Itens comprados:"));
            document.add(new Paragraph(" "));

            // Tabela para listar os itens
            PdfPTable tabelaItens = new PdfPTable(4);
            tabelaItens.setWidthPercentage(85.5F);
            tabelaItens.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabelaItens.addCell("Produto");
            tabelaItens.addCell("Quantidade");
            tabelaItens.addCell("Preço Unitário");
            tabelaItens.addCell("Preço Total Item");

            // Criar um mapa para armazenar a quantidade total por nome do item
            Map<String, Integer> quantidadePorItem = new HashMap<>();
            Map<String, Double> precoTotalPorItem = new HashMap<>();

            // Processar os dados do ResultSet
            do {
                // Obter os valores específicos da linha atual
                String nomeItem = resultSet.getString("nome_item");
                int quantidade = resultSet.getInt("quantidade");
                double precoItem = resultSet.getDouble("preco");
                double precoTotalItem = quantidade * precoItem;

                // Atualizar a quantidade total do item no mapa
                quantidadePorItem.put(nomeItem, quantidadePorItem.getOrDefault(nomeItem, 0) + quantidade);

                // Atualizar o preço total do item no mapa
                precoTotalPorItem.put(nomeItem, precoTotalPorItem.getOrDefault(nomeItem, 0.0) + precoTotalItem);

                // Se o item já foi adicionado à tabela, atualizar apenas a quantidade e o preço total
                boolean itemExistente = false;
                for (int i = 0; i < tabelaItens.size(); i++) {
                    PdfPCell cell = tabelaItens.getRow(i).getCells()[0];
                    if (cell != null && cell.getPhrase().getContent().equals(nomeItem)) {
                        // Atualizar a quantidade na tabela
                        PdfPCell quantidadeCell = tabelaItens.getRow(i).getCells()[1];
                        quantidadeCell.setPhrase(new Phrase(String.valueOf(quantidadePorItem.get(nomeItem))));

                        // Atualizar o preço total na tabela
                        PdfPCell precoTotalCell = tabelaItens.getRow(i).getCells()[3];
                        precoTotalCell.setPhrase(new Phrase(String.format("%.2f €", precoTotalPorItem.get(nomeItem))));

                        itemExistente = true;
                        break;
                    }
                }

                // Se o item ainda não foi adicionado à tabela, adicioná-lo
                if (!itemExistente) {
                    tabelaItens.addCell(nomeItem);
                    tabelaItens.addCell(String.valueOf(quantidadePorItem.get(nomeItem)));
                    tabelaItens.addCell(String.format("%.2f €", precoItem));
                    tabelaItens.addCell(String.format("%.2f €", precoTotalPorItem.get(nomeItem)));
                }
            } while (resultSet.next());

            document.add(tabelaItens);
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Total: " + formatarTotal(precoTotal)));
            document.add(new Paragraph("___________________________________________________________________"));
            document.add(new Paragraph("Obrigado pela preferência!"));
        } catch (Exception e) {
            exibirAlerta("Erro", "Ocorreu um erro ao gerar a 2ª via do recibo: " + e.getMessage());
        } finally {
            document.close();
        }
    }

    //Método para obter a data atual do sistema
    private String obterDataAtual() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    private int obterIdAtendimentoFinalizadoMaisRecente(int mesaId) {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                String query = "SELECT PagamentoAtendimento.id_pagatendimento " +
                        "FROM PagamentoAtendimento " +
                        "WHERE PagamentoAtendimento.id_mesa = ? " +
                        "AND PagamentoAtendimento.preco_total IS NOT NULL " +
                        "AND PagamentoAtendimento.data_hora_fim = (" +
                        "   SELECT MAX(data_hora_fim) " +
                        "   FROM PagamentoAtendimento " +
                        "   WHERE id_mesa = ? " +
                        "   AND preco_total IS NOT NULL)";

                PreparedStatement preparedStatement = conexao.prepareStatement(query);
                preparedStatement.setInt(1, mesaId);
                preparedStatement.setInt(2, mesaId);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int idPagamentoAtendimento = resultSet.getInt("id_pagatendimento");
                    resultSet.close();
                    preparedStatement.close();
                    return idPagamentoAtendimento;
                } else {
                    // Se não houver resultados
                    exibirAlerta("Atendimento não encontrado", "Não foi possível encontrar um atendimento finalizado para a mesa " + mesaId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                bd.desconectar();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
        return -1; // Retornar um valor padrão ou lançar uma exceção, dependendo do seu caso
    }

    @FXML
    private void abrirInterfaceFecharConta() {
        abrirJanelaFecharConta(mesaId);
    }

    private void abrirJanelaFecharConta(int mesaId) {
        if (!verificarStatusMesa(mesaId)) { //Mesa não está ocupada (não tem pedidos confirmados)
            // Se nenhum pedido foi confirmado, exiba uma mensagem ou tome a ação apropriada.
            exibirAlerta("Nenhum Pedido Confirmado", "Por favor, confirme pelo menos um pedido antes de fechar a conta.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/FecharConta.fxml"));
            Parent root = loader.load();

            DashboardFecharContaController fecharContaController = loader.getController();
            fecharContaController.inicializarItens(listaItens);
            fecharContaController.setMesaId(mesaId);
            fecharContaController.setIdPagamentoAtendimento(obterIdPagamento(mesaId));

            // Obter a referência da Stage atual
            Stage stage = (Stage) tableView.getScene().getWindow();

            // Criar uma nova cena com o conteúdo do FecharConta.fxml
            Scene scene = new Scene(root);

            // Define a nova cena na Stage atual
            stage.setScene(scene);

            // Exibe a nova cena
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Adicione mais informações de diagnóstico aqui, se necessário
        }
    }

    //Método para exibir alerta de erros ou avisos
    private void exibirAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }




    @FXML
    private void confirmarItens() {

        // Cria um TextArea para permitir a inserção de uma descrição do pedido
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPromptText("Descrição do pedido...");

        // Cria um Alert do tipo CONFIRMATION para confirmar a ação de realizar o pedido
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Itens");
        alert.setHeaderText("Você tem certeza que deseja confirmar os itens?");
        alert.setContentText("Esta ação não pode ser desfeita.");
        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);

        // Cria botões personalizados para "Sim" e "Não"
        ButtonType buttonTypeSim = new ButtonType("Sim");
        ButtonType buttonTypeNao = new ButtonType("Não");

        // Configura o botão padrão para "Não"
        alert.getButtonTypes().setAll(buttonTypeSim, buttonTypeNao);
        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().stream()
                .filter(button -> button.getTypeSelector().equals(buttonTypeNao))
                .findFirst()
                .ifPresent(button -> Platform.runLater(button::requestFocus));

        // Obtém a referência à janela do Alert para configurar o comportamento de fecho
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setOnCloseRequest(event -> {
        });

        // Exibe o Alert e aguarda a resposta do utilizador
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeSim) {

                // Cria um objeto formatter para formatar a data e hora atual
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = LocalDateTime.now().format(formatter);

                // Cria um novo objeto PagamentoAtendimento para representar o pagamento
                PagamentoAtendimento novoPagamento = new PagamentoAtendimento();
                novoPagamento.setDataHoraInicio(Timestamp.valueOf(LocalDateTime.parse(formattedDateTime, formatter)));
                novoPagamento.setDataHoraFim(null);
                novoPagamento.setPrecoTotal(null);
                novoPagamento.setTipoPagamento(null);
                novoPagamento.setIdMesa(mesaId);

                // Inserir o novo pagamento na base de dados usando JDBC
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                try {
                    ConectarBD bd = new ConectarBD();
                    Connection conexao = bd.conectar();

                    // Verificar se já existe um pagamento com preço total para essa mesa
                    String verificaPagamentoSql = "SELECT COUNT(*) FROM PagamentoAtendimento WHERE id_mesa = ? AND preco_total IS NULL";
                    preparedStatement = conexao.prepareStatement(verificaPagamentoSql);
                    preparedStatement.setInt(1, novoPagamento.getIdMesa());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next() && resultSet.getInt(1) == 0) {
                        // Se existir um pagamento anterior com preço total, inserir o novo pagamento
                        String sql = "INSERT INTO PagamentoAtendimento (data_hora_inicio, data_hora_fim, preco_total, tipo_pagamento, id_mesa) VALUES (?, ?, ?, ?, ?)";
                        preparedStatement = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                        // Setar os valores
                        preparedStatement.setObject(1, novoPagamento.getDataHoraInicio());
                        preparedStatement.setObject(2, novoPagamento.getDataHoraFim());
                        preparedStatement.setObject(3, novoPagamento.getPrecoTotal());
                        preparedStatement.setObject(4, novoPagamento.getTipoPagamento());
                        preparedStatement.setInt(5, novoPagamento.getIdMesa());

                        // Executar a inserção
                        preparedStatement.executeUpdate();
                    } else {
                        System.out.println("Já existe um atendimento ativo");
                        // Trate conforme necessário (por exemplo, lançando uma exceção ou exibindo uma mensagem)
                    }

                } catch (SQLException e) {
                    e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                } finally {
                    // Fechar recursos
                    try {
                        if (resultSet != null) resultSet.close();
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) {
                            System.out.printf("Falha na conexão com a base de dados");
                            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
                            connection.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                    }
                }

                //Obtém a descricao do pedido
                String descricao = textArea.getText();

                // Obtém o ID do funcionário da classe SessaoUtilizador
                int idFuncionario = SessaoUtilizador.getInstance().getIdUtilizador();
                String nomeFuncionario = SessaoUtilizador.getInstance().getNomeFuncionario();

                // Aqui você pode usar 'descricao' e 'idFuncionario' conforme necessário
                System.out.println("Descrição do pedido: " + descricao);
                System.out.println("ID do Funcionário: " + idFuncionario + " | Nome: " + nomeFuncionario);

                // Obtém o ID do pagamento com base na consulta SQL fornecida
                int idPagamento = obterIdPagamento(mesaId);

                // Criar um novo pedido

                try {
                    ConectarBD bd = new ConectarBD();
                    Connection conexao = bd.conectar();
                    // Inserir o novo pedido na tabela
                    String sqlPedido = "INSERT INTO Pedido_Restaurante (descricao, id_pagamento, id_func, status) VALUES (?, ?, ?, ?)";
                    preparedStatement = conexao.prepareStatement(sqlPedido);

                    // Setar os valores
                    preparedStatement.setString(1, descricao);
                    preparedStatement.setInt(2, idPagamento);
                    preparedStatement.setInt(3, idFuncionario);
                    preparedStatement.setString(4, "Por Fazer");

                    // Executar a inserção do pedido
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                } finally {
                    // Fechar recursos
                    try {
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                    }
                }

                // Obtém o ID do pedido restaurante recém-criado
                int idPedidoRestaurante = obterIdPedidoRestaurante(mesaId);

                System.out.println("Pedido criado com o id: " + idPedidoRestaurante + " para o atendimento com o id: " + idPagamento);


                try {
                    ConectarBD bd = new ConectarBD();
                    Connection conexao = bd.conectar();

                    // Query para inserir os itens na tabela PedidoDetalhe
                    String sql = "INSERT INTO PedidoDetalhe (id_pedido, id_item, quantidade, data_hora) VALUES (?, ?, ?, ?)";
                    preparedStatement = conexao.prepareStatement(sql);

                    // Imprime a lista atualizada de itens
                    System.out.println("Lista atualizada");
                    for (ItemPedido item : listaItens) {
                        // Fazer algo com cada item, por exemplo:
                        System.out.println("Id item: " + item.getIdItem() + " | Nome: " + item.getProduto() + " | Quantidade: " + item.getQuantidade());
                    }

                    // Imprime a lista anterior de itens
                    System.out.println("Lista anterior");
                    for (ItemPedido item : listaItensOriginal) {
                        // Fazer algo com cada item, por exemplo:
                        System.out.println("Id item: " + item.getIdItem() + " | Nome: " + item.getProduto() + " | Quantidade: " + item.getQuantidadeOriginal());
                    }

                    // Clico de itens na listaItens para inseri-los na tabela PedidoDetalhe
                    for (ItemPedido item : listaItens) {
                        // Verificar se o item já existe na lista listaItensOriginal
                        boolean itemExiste = false;
                        int quantidadeAntiga = 0;

                        for (ItemPedido itemExistente : listaItensOriginal) {
                            if (itemExistente.getIdItem() == item.getIdItem()) {
                                itemExiste = true;
                                quantidadeAntiga = itemExistente.getQuantidadeOriginal();
                                break;
                            }
                        }

                        // Se o item não existir na listaItensOriginal, insere-o na tabela PedidoDetalhe
                        if (!itemExiste) {
                            preparedStatement.setLong(1, idPedidoRestaurante);
                            preparedStatement.setLong(2, item.getIdItem());
                            preparedStatement.setInt(3, item.getQuantidade());

                            // Utiliza a data e hora atual para o campo data_hora
                            LocalDateTime dataHoraAtual = LocalDateTime.now();
                            Timestamp timestamp = Timestamp.valueOf(dataHoraAtual);
                            preparedStatement.setTimestamp(4, timestamp);

                            preparedStatement.executeUpdate();

                            // Adiciona o item à lista listaItensOriginal e atualiza a quantidade original
                            listaItensOriginal.add(item);
                            item.setQuantidadeOriginal(item.getQuantidade());

                            // Imprimir os detalhes do pedido
                            System.out.println("Pedido: id_pedido = " + idPedidoRestaurante + ", id_item = " + item.getIdItem() + ", item adicionado = " + item.getProduto() + ", quantidade = " + item.getQuantidade() + ", data_hora = " + timestamp);
                        } else {
                            // Se a quantidade do item existente for alterada, faça um novo pedido com a diferença entre a quantidade nova e a antiga
                            int diferencaQuantidade = item.getQuantidade() - quantidadeAntiga;

                            if (diferencaQuantidade > 0) {
                                preparedStatement.setLong(1, idPedidoRestaurante);
                                preparedStatement.setLong(2, item.getIdItem());
                                preparedStatement.setInt(3, diferencaQuantidade);

                                // Utilize a data e hora atual para o campo data_hora
                                LocalDateTime dataHoraAtual = LocalDateTime.now();
                                Timestamp timestamp = Timestamp.valueOf(dataHoraAtual);
                                preparedStatement.setTimestamp(4, timestamp);

                                preparedStatement.executeUpdate();

                                item.setQuantidadeOriginal(item.getQuantidade());

                                // Imprimir os detalhes do pedido
                                System.out.println("Pedido: id_pedido = " + idPedidoRestaurante + ", id_item = " + item.getIdItem() + ", item modificado = " + item.getProduto() + ", quantidade adicionada = " + diferencaQuantidade + ", data_hora = " + timestamp);
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                } finally {
                    // Fechar recursos
                    try {
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace(); // Trate ou registre a exceção conforme necessário
                    }
                }
                // Altera o status da mesa para "Ocupada" e atualiza a lista de mesas
                alterarStatusMesa(mesaId, "Ocupada");
                mesaController.atualizarMesas();

                // Exibe um Alert informativo de sucesso caso o pedido seja realizado
                Alert pedidoFeitoAlert = new Alert(Alert.AlertType.INFORMATION);
                pedidoFeitoAlert.setTitle("Pedido");
                pedidoFeitoAlert.setHeaderText(null);
                pedidoFeitoAlert.setContentText("O pedido foi Realizado com sucesso!");
                pedidoFeitoAlert.showAndWait();
            } else {
                // Exibe um Alert informativo de insucesso caso o pedido não seja realizado
                Alert pedidoCanceladoAlert = new Alert(Alert.AlertType.INFORMATION);
                pedidoCanceladoAlert.setTitle("Pedido");
                pedidoCanceladoAlert.setHeaderText(null);
                pedidoCanceladoAlert.setContentText("O pedido foi cancelado.");
                pedidoCanceladoAlert.showAndWait();
            }
        });
    }

    public void CarregarListaAoAbrirPagina() {
    // Verifica se a página já foi carregada anteriormente
        if (paginaCarregada) {
            System.out.println("Lista Carregada.");

            // Adiciona todos os itens da lista atual à lista origina
            listaItensOriginal.addAll(listaItens);
        }
    }

    //Método para calcular o valor total dos itens nas duas tableviews
    private double calcularTotal(ObservableList<ItemPedido> items) {
        double total = 0.0;
        for (ItemPedido item : items) {
            total += item.getPreco() * item.getQuantidade();
        }
        return total;
    }

    //Método para atualizar o valor total nas duas tableviews
    private void atualizarTotalPedido() {
    }

    //Método para formatar o valor total com 2 casas decimais
    private String formatarTotal(double total) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(total) + " €";
    }

    private void alterarStatusMesa(int mesaId, String novoStatus) {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        // Verifica se a conexão com a base de dados foi bem-sucedida
        if (conexao != null) {
            try {
                //Query SQL para atualizar o status da mesa com base no ID da mesa
                String query = "UPDATE Mesa SET status = ? WHERE id_mesa = ?";
                try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                    preparedStatement.setString(1, novoStatus);
                    preparedStatement.setInt(2, mesaId);
                    // Executa a atualização na base de dados para alterar o status da mesa
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    bd.desconectar();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            // Se a conexão com a base de dados falhar, exibe mensagem de erro
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }

    private boolean verificarStatusMesa(int mesaId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            ConectarBD bd = new ConectarBD();
            connection = bd.conectar();

            if (connection != null) {
                // Consulta SQL para obter o status da mesa
                String query = "SELECT status FROM Mesa WHERE id_mesa = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, mesaId);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String status = resultSet.getString("status");

                    // Retorna true se a mesa estiver ocupada
                    return "Ocupada".equals(status);
                }
            }else {
                System.out.printf("Falha na conexão com a base de dados");
                mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
                bd.desconectar();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Trate ou registre a exceção conforme necessário
        } finally {
            // Fechar recursos
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Trate ou registre a exceção conforme necessário
            }
        }

        // Retorna false se houver algum problema ou se a mesa não for encontrada
        return false;
    }

    private int obterIdPagamento(int mesaId) {
        int idPagamento = 0;
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Consulta SQL para obter o ID do pagamento
            String sql = "SELECT PagamentoAtendimento.id_pagatendimento " +
                    "FROM PagamentoAtendimento " +
                    "WHERE PagamentoAtendimento.id_mesa = ? AND PagamentoAtendimento.preco_total IS NULL";

            preparedStatement = conexao.prepareStatement(sql);
            preparedStatement.setInt(1, mesaId);
            //Procura o id do atendimento criado
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idPagamento = resultSet.getInt("id_pagatendimento");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fechar recursos
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conexao != null) conexao.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Trate ou registre a exceção conforme necessário
            }
        }

        return idPagamento;
    }

    private int obterIdPedidoRestaurante(int mesaId) {
        int idPedidoRestaurante = 0;
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            if (conexao != null){
                // Consulta SQL para obter o ID do PedidoRestaurante
                String sql = "SELECT MAX(Pedido_Restaurante.id_pedido) AS ultimo_id_pedido " +
                        "FROM Pedido_Restaurante " +
                        "INNER JOIN PagamentoAtendimento ON PagamentoAtendimento.id_pagatendimento = Pedido_Restaurante.id_pagamento " +
                        "WHERE PagamentoAtendimento.id_mesa = ?";

                preparedStatement = conexao.prepareStatement(sql);
                preparedStatement.setInt(1, mesaId);

                //Procura o último id criado para o pedido
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    idPedidoRestaurante = resultSet.getInt("ultimo_id_pedido");
                }
            }else {
                System.out.printf("Falha na conexão com a base de dados");
                mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
                bd.desconectar();
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Trate ou registre a exceção conforme necessário
        } finally {
            // Fechar recursos
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conexao != null) conexao.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Trate ou registre a exceção conforme necessário
            }
        }

        return idPedidoRestaurante;
    }

    @FXML
    private void adicionarItem() {
        // Obtém o item selecionado na TableView
        ItemPedido selectedItem = tableView.getSelectionModel().getSelectedItem();

        // Verifica se algum item foi selecionado
        if (selectedItem == null) {
            mostrarErro("Nenhum item selecionado", "Por favor, selecione um item para adicionar.");
            return;
        }
        // Chama o método para adicionar o item ao pedido
        adicionarItemAoPedido(selectedItem);
    }

    private void adicionarItemAoPedido(ItemPedido item) {
        // Obtém o ID do pedido atual
        int idPedido = obterIdPedidoAtual();

        // Verifica se o ID do pedido é válido
        if (idPedido != -1) {
            // Obtém o ID do item
            int idItem = item.getIdItem();

            // Verifica se o ID do item é válido
            if (idItem != -1) {

                // Solicita o utilizador pela quantidade
                int novaQuantidade = promptQuantidade("Adicionar Quantidade", "Digite a quantidade a adicionar:");

                if (novaQuantidade > 0) {
                    // Atualiza a quantidade no objeto ItemPedido
                    item.setQuantidade(item.getQuantidade() + novaQuantidade);

                    // Atualiza a TableView
                    tableView.refresh();

                    // Atualiza a quantidade na base de dados
                    atualizarQuantidadePedidoDetalhe(idPedido, idItem, item.getQuantidade());
                } else {
                    mostrarErro("Quantidade Inválida", "Digite um número maior que 0.");
                }
            }
        }
    }

    @FXML
    private void removerItem() {

        // Obtém o item selecionado na TableView
        ItemPedido selectedItem = tableView.getSelectionModel().getSelectedItem();

        // Verifica se algum item foi selecionado
        if (selectedItem == null) {
            mostrarErro("Nenhum item selecionado", "Por favor, selecione um item para remover.");
            return;
        }

        // Obtém o ID do pedido atual
        int idPedido = obterIdPedidoAtual();

        // Verifica se o ID do pedido é válido
        if (idPedido != -1) {

            // Obtém o ID do item selecionado
            int idItem = selectedItem.getIdItem();

            // Verifica se o ID do item é válido
            if (idItem != -1) {
                int quantidadeAtual = selectedItem.getQuantidade();

                // Solicita ao utilizador a quantidade a ser removida, limitando-a à quantidade atual no pedido
                int quantidadeParaRemover = promptQuantidade("Remover Quantidade", "Digite a quantidade a remover (máximo " + quantidadeAtual + "):");

                // Verifica se a quantidade a ser removida é maior que 0
                if (quantidadeParaRemover > 0) {
                    // Verifica se a quantidade a ser removida é maior que a quantidade atual no pedido
                    if (quantidadeParaRemover > quantidadeAtual) {
                        mostrarErro("Erro de Remoção", "Não é possível remover mais itens do que os existentes no pedido.");
                        return; // Interrompe o processamento se ocorrer um erro
                    }

                    // Atualiza a quantidade no objeto ItemPedido
                    int quantidadeAtualizada = quantidadeAtual - quantidadeParaRemover;
                    selectedItem.setQuantidade(quantidadeAtualizada);

                    // Atualiza a TableView
                    tableView.refresh();

                    // Verifica se a quantidade é maior que 0
                    if (quantidadeAtualizada > 0) {
                        // Atualiza a quantidade na base de dados
                        atualizarQuantidadePedidoDetalhe(idPedido, idItem, quantidadeAtualizada);
                    } else {
                        // Se a quantidade for 0, remove o item
                        removerPedidoDetalhe(idPedido, idItem);

                        // Remove o item diretamente da lista de itens
                        listaItens.remove(selectedItem);

                        // Atualiza a TableView com a lista atualizada
                        tableView.setItems(listaItens);
                    }
                } else {
                    mostrarErro("Quantidade Inválida", "Digite um número maior que 0");
                }
            }
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private int promptQuantidade(String title, String headerText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText("Quantidade:");

        // Forma tradicional de obter o valor de resposta.
        java.util.Optional<String> result = dialog.showAndWait();

        // Verifica se o utilizador forneceu uma resposta
        if (result.isPresent()) {
            try {
                // Tenta converter a resposta para um valor inteiro
                return Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;    // Retorna 0 se o utilizador cancelar ou inserir uma entrada inválida
    }

    private int obterIdPedidoAtual() {
        // Substitua isso pelo método real que obtém o ID do pedido atual em sua aplicação
        int idPedido = Pedido.getId(); // Exemplo, substitua pelo método real em sua aplicação
        return idPedido;
    }

    private void atualizarQuantidadePedidoDetalhe(int idPedido, int idItem, int novaQuantidade) {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {

                // Query SQL para atualizar a quantidade do detalhe do pedido com base nos IDs fornecidos
                String query = "UPDATE PedidoDetalhe SET quantidade = ? WHERE id_pedido = ? AND id_item = ?";
                try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                    preparedStatement.setInt(1, novaQuantidade);
                    preparedStatement.setInt(2, idPedido);
                    preparedStatement.setInt(3, idItem);

                    // Executa a atualização na base de dados para alterar a quantidade do detalhe do pedido
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace(); // Lide com a exceção de uma maneira apropriada para sua aplicação
                } finally {
                    bd.desconectar();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }


    private void removerPedidoDetalhe(int idPedido, int idItem) {

        // Verifica se os IDs do pedido e do item são válidos
        if (idPedido > 0 && idItem > 0) {
            ConectarBD bd = new ConectarBD();
            Connection conexao = bd.conectar();

            if (conexao != null) {
                try {
                    // query SQL para excluir o detalhe do pedido com base nos IDs fornecidos
                    String query = "DELETE FROM PedidoDetalhe WHERE id_pedido = ? AND id_item = ?";
                    try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                        preparedStatement.setInt(1, idPedido);
                        preparedStatement.setInt(2, idItem);

                        // Executa a atualização na base de dados para remover o detalhe do pedido
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace(); // Lide com a exceção de uma maneira apropriada para sua aplicação
                    } finally {
                        bd.desconectar();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("ID do Pedido: " + idPedido + " ou ID do Item: " + idItem + " não foi obtido corretamente");
                mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
                bd.desconectar();
            }
        }
    }


    public void setMesaId(int mesaId) {
        this.mesaId = mesaId;
        numeroMesa.setText(String.valueOf(mesaId));
    }

    private void atualizarProximaReserva() {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        String query = "SELECT data_reserva, hora_reserva FROM Reservas WHERE id_mesa = ? AND data_reserva >= CURRENT_DATE AND ((data_reserva = CURRENT_DATE AND hora_reserva > CURRENT_TIME) OR data_reserva > CURRENT_DATE) ORDER BY data_reserva ASC, hora_reserva ASC LIMIT 1";

        if (conexao != null) {
            try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                // Substitua o valor do ID da mesa conforme necessário
                preparedStatement.setInt(1, mesaId);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Obtemos a data e hora da próxima reserva
                    LocalDateTime dataReserva = resultSet.getTimestamp("data_reserva").toLocalDateTime();
                    String horaReserva = resultSet.getString("hora_reserva");

                    // Formatamos a data e hora conforme necessário (exemplo: "dd/MM/yyyy HH:mm")
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataHoraFormatada = dataReserva.format(formatter);

                    LocalDateTime agora = LocalDateTime.now();
                    Duration diferencaTempo = Duration.between(agora, LocalDateTime.of(dataReserva.toLocalDate(), dataReserva.toLocalTime()));

                    if (diferencaTempo.toHours() < 1) {
                        mostrarReserva.setTextFill(Color.RED);
                    } else if (diferencaTempo.toHours() < 2) {
                        mostrarReserva.setTextFill(Color.YELLOW);
                    } else if (diferencaTempo.toHours() < 4) {
                        mostrarReserva.setTextFill(Color.GREEN);
                    }
                    // Definimos a cor da Label com base na diferença de tempo
                    if (dataReserva.toLocalDate().isEqual(LocalDate.now())) {
                        mostrarReserva.setText("Hoje " + horaReserva.substring(0, 5));
                    } else {
                        // Atualizamos a Label com a data e hora da próxima reserva
                        mostrarReserva.setText("Próxima Reserva: " + dataHoraFormatada + " " + horaReserva.substring(0, 5));
                    }
                } else {
                    mostrarReserva.setText("Não há reservas futuras para esta mesa.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }





    private void configurarColunas() {
        produtoColumn.setCellValueFactory(cellData -> cellData.getValue().produtoProperty());
        quantidadeColumn.setCellValueFactory(cellData -> cellData.getValue().quantidadeProperty().asObject());
        precoColumn.setCellValueFactory(cellData -> cellData.getValue().precoProperty().asObject());

        // Configurar alinhamento das colunas para o centro
        produtoColumn.setStyle("-fx-alignment: CENTER;");
        quantidadeColumn.setStyle("-fx-alignment: CENTER;");
        precoColumn.setStyle("-fx-alignment: CENTER;");
    }

    public DashboardPedidoController() {
    }

    // Método para adicionar listener a um item
    private void adicionarListener(ItemPedido item) {
        item.quantidadeProperty().addListener((observable, oldValue, newValue) -> {
            // Atualize a variável totalQuantidades quando a quantidade de um item for alterada
            int diferencaQuantidade = newValue.intValue() - oldValue.intValue();
            totalQuantidades.set(totalQuantidades.get() + diferencaQuantidade);

            // Atualize o total quando houver alterações na quantidade
            double totalPedido = calcularTotal(listaItens);
            formatarTotal(totalPedido);
        });
    }

    public void initialize() {

        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            configurarColunas();
            atualizarProximaReserva();
            carregarItensDoPedido();
            atualizarTotalPedido();
            carregarCategorias();
            CarregarListaAoAbrirPagina();

            paginaCarregada = true;

            // Adicione um ChangeListener à propriedade "quantidade" de cada item na lista
            for (ItemPedido item : listaItens) {
                adicionarListener(item);
            }

            // Adicione um listener para novos itens
            listaItens.addListener((ListChangeListener<ItemPedido>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (ItemPedido novoItem : change.getAddedSubList()) {
                            adicionarListener(novoItem);
                        }
                    }
                }
            });

            // Use o Bindings.createStringBinding com a variável totalQuantidades
            labelTotalPedido.textProperty().bind(Bindings.createStringBinding(() -> {
                double totalPedido = calcularTotal(listaItens);
                return formatarTotal(totalPedido);
            }, totalQuantidades, listaItens));

            pesquisarItem.textProperty().addListener((observable, oldValue, newValue) -> {
                pesquisarItens(newValue);
            });

            // Adicione um listener de clique à cena
            Scene cena = pesquisarItem.getScene();
            if (cena != null) {
                cena.setOnMouseClicked(event -> {
                    if (!pesquisarItem.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())) {
                        // Se o clique foi fora do pesquisarItem, solicita o foco ao pai
                        pesquisarItem.getParent().requestFocus();
                    }
                });
            }

            // Atualize o listener onMouseExited para remover o foco
            pesquisarItem.setOnMouseClicked(event -> {
                pesquisarItem.requestFocus(); // Garante que o foco é mantido quando o rato é clicado no TextField
            });
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }

    }

    private void pesquisarItens(String filtro) {
        filtroAtual = filtro; // Atualiza o filtro atual

        // Cancela o timer anterior se estiver em execução
        if (timer != null) {
            timer.cancel();
        }

        // Cria um novo timer para iniciar a pesquisa após o atraso
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ConectarBD bd = new ConectarBD();
                    Connection conexao = bd.conectar();

                    if (conexao != null) {
                        try {
                            String query = "SELECT * FROM Item";

                            // Adicione a condição de pesquisa se o filtro não estiver vazio
                            if (!filtroAtual.isEmpty()) {
                                query += " WHERE nome LIKE '%" + filtroAtual + "%'";
                                limparItensMostrados(); // Limpa os itens antes de mostrar os novos
                                System.out.println(query);
                            }

                            Statement statement = conexao.createStatement();
                            ResultSet resultSet = statement.executeQuery(query);

                            double buttonWidth = 180.0;
                            double buttonHeight = 50.0;
                            double xPosition = 10.0;
                            double yPosition = 10.0;
                            double paneWidth = 848.0;
                            double paneHeight = 460.0;
                            double spacing = 5.0;

                            // Cria um novo ScrollPane para exibir os botões das categorias
                            ScrollPane scrollPane = new ScrollPane();
                            scrollPane.setLayoutX(xPosition);
                            scrollPane.setLayoutY(yPosition);
                            scrollPane.setPrefWidth(paneWidth);
                            scrollPane.setPrefHeight(paneHeight);

                            // Configura as barras de scroll, neste caso a vertical não é necessária e a horizontal é só quando é preciso
                            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

                            // Cria um novo Pane para conter os botões das categorias
                            FlowPane paneItemsPesquisados = new FlowPane();
                            paneItemsPesquisados.setPrefSize(paneWidth, paneHeight);

                            while (resultSet.next()) {
                                int idItem = resultSet.getInt("id_item");
                                String nomeItem = resultSet.getString("nome");

                                Button btnItem = new Button(nomeItem);
                                btnItem.setPrefWidth(buttonWidth);
                                btnItem.setPrefHeight(buttonHeight);
                                btnItem.setMaxWidth(Double.MAX_VALUE);
                                btnItem.setOnAction(e -> itemSelecionado(idItem, nomeItem));

                                // Ajusta a posição do botão no Pane
                                btnItem.setLayoutX(xPosition);
                                btnItem.setLayoutY(yPosition);
                                paneItemsPesquisados.getChildren().add(btnItem);

                                // Atualiza as posições para o próximo botão
                                yPosition += buttonHeight + spacing;

                                // Verifica se o próximo botão caberá no espaço disponível, se não, ajusta as posições
                                if (yPosition + buttonHeight > paneHeight) {
                                    xPosition += buttonWidth + spacing;
                                    yPosition = 10.0;
                                }

                                // Verifica se há filtros aplicados; se não, limpa os itens mostrados e oculta o ScrollPane
                                if (filtro.isEmpty() || filtroAtual.isEmpty()) {
                                    limparItensMostrados();
                                    scrollPane.setVisible(false);
                                }
                            }

                            // Define o conteúdo do ScrollPane como o Pane criado com os botões
                            scrollPane.setContent(paneItemsPesquisados);
                            itemsMostrar.getChildren().add(scrollPane);

                            resultSet.close();
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            bd.desconectar();
                        }
                    }else {
                        // Se a conexão com o base de dados falhar, exibe mensagem de erro
                        System.out.printf("Falha na conexão com a base de dados");
                        mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
                        bd.desconectar();
                    }
                });
            }
        }, DELAY);
    }

    // Método para limpar a lista de itens mostrados na interface gráfica
    private void limparItensMostrados() {
        itemsMostrar.getChildren().clear();
    }


    private void categoriaSelecionada(int idCategoria, String nomeCategoria) {
        // Define a categoria selecionada e limpa a lista de itens mostrados
        categoriaSelecionadaId = idCategoria;
        limparItensMostrados();

        // Verifica se uma categoria válida foi selecionada antes de exibir os itens
        if (categoriaSelecionadaId != -1) {
            mostrarItens();
            System.out.println("Categoria selecionada - ID: " + idCategoria + ", Nome: " + nomeCategoria);
        }
    }

    private void carregarCategorias() {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                String query = "SELECT id_categoria, nome FROM Categoria";
                Statement statement = conexao.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                /* Parte estetica do Pane */
                double xPosition = 10.0; // Posição inicial em X para os botões
                double buttonWidth = 180.0; // Largura dos botões
                double buttonHeight = 50.0; // Altura dos botões
                double paneHeight = 100.0; // Altura do Pane
                double paneWidth = 846.0; // Largura do Pane

                // Cria um novo ScrollPane para exibir os botões das categorias
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setLayoutX(14.0);
                scrollPane.setLayoutY(10.0);
                scrollPane.setPrefWidth(paneWidth);
                scrollPane.setPrefHeight(paneHeight);

                // Configura as barras de scroll, neste caso a vertical não é necessario e a horizontal e só quando é preciso
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                // Cria um novo Pane para conter os botões das categorias
                Pane paneCategorias = new Pane();
                paneCategorias.setPrefSize(paneWidth, paneHeight);

                while (resultSet.next()) {
                    int idCategoria = resultSet.getInt("id_categoria");
                    String nomeCategoria = resultSet.getString("nome");

                    // Cria um novo botão para exibir o nome da categoria
                    Button btnCategoria = new Button(nomeCategoria);
                    btnCategoria.setPrefWidth(buttonWidth);
                    btnCategoria.setPrefHeight(buttonHeight);
                    btnCategoria.setMaxWidth(Double.MAX_VALUE);
                    // Lógica para tratar a ação quando uma categoria é selecionada
                    btnCategoria.setOnAction(e -> categoriaSelecionada(idCategoria, nomeCategoria));

                    btnCategoria.setLayoutX(xPosition);
                    btnCategoria.setLayoutY(10.0);

                    paneCategorias.getChildren().add(btnCategoria);

                    xPosition += buttonWidth + 5.0;
                }

                paneCategorias.setPrefWidth(xPosition);

                scrollPane.setContent(paneCategorias);
                categoriaPane.getChildren().add(scrollPane);

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                bd.desconectar();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }

    private void itemSelecionado(int idItem, String nomeItem) {

        // Lógica para lidar com a seleção de um item
        System.out.println("Item selecionado: ID: " + idItem + ", Nome: " + nomeItem);

        // Procura o preço do item na base de dados
        double precoItem = buscarPrecoNaBaseDeDados(idItem);

        // Verifica se o item já está na lista
        boolean itemJaNaLista = false;
        for (ItemPedido item : listaItens) {
            if (item.getIdItem() == idItem && item.getProduto().equals(nomeItem)) {
                itemJaNaLista = true;
                // Incrementa a quantidade do item na lista
                item.setQuantidade(item.getQuantidade() + 1);
                break;
            }
        }

        // Se o item já estiver na lista, a quantidade foi incrementada e não é necessário adicionar novamente
        if (!itemJaNaLista) {
            // Se o item não estiver na lista, adiciona-o com quantidade 1
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setIdItem(idItem);
            itemPedido.setProduto(nomeItem);
            itemPedido.setQuantidade(1); // Inicia a quantidade com 1
            itemPedido.setQuantidadeOriginal(1);
            itemPedido.setPreco(precoItem);

            listaItens.add(itemPedido);
        }

        // Atualiza a TableView com a lista de itens atualizada
        tableView.setItems(listaItens);
    }

    private double buscarPrecoNaBaseDeDados(int idItem) {
        double preco = 0.0;
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        try {
            // Preparação da declaração SQL
            String sql = "SELECT preco FROM Item WHERE id_item = ?";
            try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
                // Define o parâmetro na declaração SQL
                preparedStatement.setInt(1, idItem);

                // Executa a consulta
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Se houver resultados, obtém o preço
                    if (resultSet.next()) {
                        preco = resultSet.getDouble("preco");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Trate a exceção de forma adequada em sua aplicação
        } finally {
            bd.desconectar(); // Certifique-se de desconectar no final, mesmo se ocorrer uma exceção
        }

        return preco;
    }


    private void mostrarItens() {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                String query = null;

                if (categoriaSelecionadaId != -1) {
                    query = "SELECT id_item, nome FROM Item WHERE id_categoria = " + categoriaSelecionadaId;
                }

                Statement statement = conexao.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                limparItensMostrados(); // Limpa os itens antes de mostrar os novos

                /* Parte estetica do Pane */
                double buttonWidth = 180.0; // Largura dos botões
                double buttonHeight = 50.0; // Altura dos botões
                double xPosition = 10.0; // Posição inicial em X para os botões
                double yPosition = 10.0; // Posição inicial em Y para os botões
                double paneWidth = 850.0; // Largura do Pane
                double paneHeight = 450.0; // Altura do Pane
                double spacing = 5.0; // Espaçamento entre os botões

                // Cria um novo ScrollPane para exibir os botões das categorias
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setLayoutX(xPosition);
                scrollPane.setLayoutY(yPosition);
                scrollPane.setPrefWidth(paneWidth);
                scrollPane.setPrefHeight(paneHeight);

                // Configura as barras de scroll, neste caso a vertical é necessária e a horizontal não
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

                // Cria um novo Pane para conter os botões das categorias
                FlowPane flowPane = new FlowPane();
                flowPane.setPrefSize(paneWidth, paneHeight);

                while (resultSet.next()) {
                    int idItem = resultSet.getInt("id_item");
                    String nomeItem = resultSet.getString("nome");

                    // Cria um novo botão com o nome do Item
                    Button btnItem = new Button(nomeItem);
                    btnItem.setPrefWidth(buttonWidth);
                    btnItem.setPrefHeight(buttonHeight);
                    btnItem.setMaxWidth(Double.MAX_VALUE);

                    // Lógica para tratar a ação quando um item é selecionado
                    btnItem.setOnAction(e -> itemSelecionado(idItem, nomeItem));

                    // Adiciona o botão ao Pane itemsMostrar
                    flowPane.getChildren().add(btnItem);
                }

                scrollPane.setContent(flowPane);
                itemsMostrar.getChildren().add(scrollPane);

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                bd.desconectar();
            }
        }else {
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
    }

    private void carregarItensDoPedido() {
        listaItens.clear(); // Limpa a lista antes de carregar novamente os itens

        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                // Query SQL para obter informações sobre os itens associados ao pedido
                String query = "SELECT Item.id_item, " +
                        "Item.nome, " +
                        "SUM(PedidoDetalhe.quantidade) AS quantidade, " +
                        "Item.preco " +
                        "FROM Pedido_Restaurante " +
                        "INNER JOIN PedidoDetalhe ON Pedido_Restaurante.id_pedido = PedidoDetalhe.id_pedido " +
                        "INNER JOIN Item ON Item.id_item = PedidoDetalhe.id_item " +
                        "INNER JOIN PagamentoAtendimento ON PagamentoAtendimento.id_pagatendimento = Pedido_Restaurante.id_pagamento " +
                        "INNER JOIN Mesa ON Mesa.id_mesa = PagamentoAtendimento.id_mesa " +
                        "WHERE Mesa.id_mesa = " + mesaId +
                        "AND PagamentoAtendimento.preco_total IS NULL " +
                        "GROUP BY Item.id_item";

                System.out.println("Query: " + query);

                // Cria um Statement para executar a query na base de dados
                Statement statement = conexao.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                //ciclo com o resultado da query para obter informações sobre os itens
                while (resultSet.next()) {
                    int idItem = resultSet.getInt("id_item");
                    String nome = resultSet.getString("nome");
                    double preco = resultSet.getDouble("preco");
                    int quantidade = resultSet.getInt("quantidade");

                    System.out.println("Item: ID: " + idItem + " , Nome: " + nome + ", Quantidade: " + quantidade + ", Preço: " + preco + " €");

                    // Cria um objeto ItemPedido com as informações obtidas
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setIdItem(resultSet.getInt("id_item"));
                    itemPedido.setProduto(nome);
                    itemPedido.setQuantidade(quantidade);
                    itemPedido.setQuantidadeOriginal(quantidade);
                    itemPedido.setPreco(preco);

                    // Adiciona o item à lista de itens
                    listaItens.add(itemPedido);
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                bd.desconectar();
            }
        }else {
            // Se a conexão com a base de dados falhar, exibe mensagem de erro
            System.out.printf("Falha na conexão com a base de dados");
            mostrarErro("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            bd.desconectar();
        }
        // Atualiza a TableView na interface gráfica com a lista de itens carregada
        tableView.setItems(listaItens);
    }

    public void setMesaController(DashboardMesaController mesaController) {
        this.mesaController = mesaController;
    }
}