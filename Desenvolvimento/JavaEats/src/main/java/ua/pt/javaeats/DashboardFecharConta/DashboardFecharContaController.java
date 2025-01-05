package ua.pt.javaeats.DashboardFecharConta;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.*;
import ua.pt.javaeats.*;
import ua.pt.javaeats.dashboardMesa.DashboardMesaController;
import ua.pt.javaeats.dashboardPedido.DashboardPedidoController;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardFecharContaController {
    @FXML
    private TableView<ItemPedido> tableViewFecharConta;
    @FXML
    private TableColumn<ItemPedido, String> produtoColumnFecharConta;
    @FXML
    private TableColumn<ItemPedido, Integer> quantidadeColumnFecharConta;
    @FXML
    private TableColumn<ItemPedido, Double> precoColumnFecharConta;
    @FXML
    private Label labelTotal;
    @FXML
    private TableView<ItemPedido> tableViewFecharConta2;
    @FXML
    private TableColumn<ItemPedido, String> produtoColumnFecharConta2;
    @FXML
    private TableColumn<ItemPedido, Integer> quantidadeColumnFecharConta2;
    @FXML
    private TableColumn<ItemPedido, Double> precoColumnFecharConta2;
    @FXML
    private Label labelTotal2;
    @FXML
    private Pane voltar_menu_pedido_mesa;
    private int tentativasPin = 0;
    private int mesaId;
    private int idPagamentoAtendimento;
    private double totalConta1;
    private double totalConta2;
    private double valorTotalAtendimento;
    @FXML
    private void voltar_menu_pedido_mesa() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Pedido.fxml"));
                Parent root = loader.load();

                // Obter uma referência para o controlador do Pedido e passar o ID da mesa
                DashboardPedidoController pedidoController = loader.getController();
                pedidoController.setMesaId(mesaId);
                pedidoController.initialize();

                Scene scene = voltar_menu_pedido_mesa.getScene();
                scene.setRoot(root);
            } catch (IOException e) {
                // Imprimir o stack trace em caso de exceção
                e.printStackTrace();
            }
    }

    // Método para obter o mesaId
    public void setMesaId(int mesaId) {
        this.mesaId = mesaId;
    }

    public void setIdPagamentoAtendimento(int idPagamentoAtendimento) {
        this.idPagamentoAtendimento = idPagamentoAtendimento;
    }

    //Método chamado ao inicializar o controlador
    public void initialize() {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if(conexao != null) {
            configurarColunas();
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

    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }

    //Método para inicializar os itens na tabela (começa na esquerda)
    public void inicializarItens(ObservableList<ItemPedido> listaItens) {
        tableViewFecharConta.setItems(listaItens);
        atualizarTotal();
        valorTotalAtendimento = totalConta1;
    }

    //Configurar as tableviews
    private void configurarColunas() {
        // Configuração para a primeira TableView
        produtoColumnFecharConta.setCellValueFactory(cellData -> cellData.getValue().produtoProperty());
        quantidadeColumnFecharConta.setCellValueFactory(cellData -> cellData.getValue().quantidadeProperty().asObject());
        precoColumnFecharConta.setCellValueFactory(cellData -> cellData.getValue().precoProperty().asObject());
        produtoColumnFecharConta.setStyle("-fx-alignment: CENTER;");
        quantidadeColumnFecharConta.setStyle("-fx-alignment: CENTER;");
        precoColumnFecharConta.setStyle("-fx-alignment: CENTER;");

        // Configuração para a segunda TableView
        produtoColumnFecharConta2.setCellValueFactory(cellData -> cellData.getValue().produtoProperty());
        quantidadeColumnFecharConta2.setCellValueFactory(cellData -> cellData.getValue().quantidadeProperty().asObject());
        precoColumnFecharConta2.setCellValueFactory(cellData -> cellData.getValue().precoProperty().asObject());
        produtoColumnFecharConta2.setStyle("-fx-alignment: CENTER;");
        quantidadeColumnFecharConta2.setStyle("-fx-alignment: CENTER;");
        precoColumnFecharConta2.setStyle("-fx-alignment: CENTER;");
    }

    //Método para atualizar o valor total nas duas tableviews
    private void atualizarTotal() {
        totalConta1 = calcularTotal(tableViewFecharConta.getItems());
        labelTotal.setText(formatarTotal(totalConta1));

        totalConta2 = calcularTotal(tableViewFecharConta2.getItems());
        labelTotal2.setText(formatarTotal(totalConta2));
    }

    //Método para calcular o valor total dos itens nas duas tableviews
    private double calcularTotal(ObservableList<ItemPedido> items) {
        double total = 0.0;
        for (ItemPedido item : items) {
            total += item.getPreco() * item.getQuantidade();
        }
        return total;
    }

    //Método para formatar o valor total com 2 casas decimais
    private String formatarTotal(double total) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(total) + " €";
    }

    //Método do botão fxml para mover itens da esquerda para a direita
    @FXML
    private void moverParaDireita() {
        moverItem(tableViewFecharConta, tableViewFecharConta2);
    }

    //Método do botão fxml para mover itens da direita para a esquerda
    @FXML
    private void moverParaEsquerda() {
        moverItem(tableViewFecharConta2, tableViewFecharConta);
    }

    //Método com a lógica para mover itens entre as tableviews
    private void moverItem(TableView<ItemPedido> sourceTableView, TableView<ItemPedido> targetTableView) {
        ItemPedido selectedItem = sourceTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            exibirAlerta("Nenhum item selecionado", "Por favor, selecione um item para mover.");
            return;
        }

        // Se a quantidade do item for 1, move o item imediatamente
        if (selectedItem.getQuantidade() == 1) {
            // Atribui 1 diretamente à variável quantidade
            int quantidade = 1;

            // Tenta encontrar um item com o mesmo nome na tabela de destino
            Optional<ItemPedido> existingItem = targetTableView.getItems()
                    .stream()
                    .filter(item -> item.getProduto().equals(selectedItem.getProduto()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Se o item já existe, apenas atualiza a quantidade
                existingItem.get().setQuantidade(existingItem.get().getQuantidade() + quantidade);
            } else {
                // Caso contrário, cria um novo ItemPedido
                ItemPedido newItem = new ItemPedido();
                newItem.setProduto(selectedItem.getProduto());
                newItem.setQuantidade(quantidade);
                newItem.setPreco(selectedItem.getPreco());

                // Adiciona o novo item à outra TableView
                targetTableView.getItems().add(newItem);
            }

            // Remove o item da tabela de origem
            sourceTableView.getItems().remove(selectedItem);

            // Atualiza os totais
            atualizarTotal();
            return;
        }

        boolean quantidadeValida = false;

        while (!quantidadeValida) {
            int quantidade = obterQuantidade();

            if (quantidade > selectedItem.getQuantidade()) {
                exibirAlerta("Quantidade inválida", "A quantidade inserida é maior que a quantidade disponível.");
            } else if (quantidade > 0) {
                // Tenta encontrar um item com o mesmo nome na tabela de destino
                Optional<ItemPedido> existingItem = targetTableView.getItems()
                        .stream()
                        .filter(item -> item.getProduto().equals(selectedItem.getProduto()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    // Se o item já existe, apenas atualiza a quantidade
                    existingItem.get().setQuantidade(existingItem.get().getQuantidade() + quantidade);
                } else {
                    // Caso contrário, cria um novo ItemPedido
                    ItemPedido newItem = new ItemPedido();
                    newItem.setProduto(selectedItem.getProduto());
                    newItem.setQuantidade(quantidade);
                    newItem.setPreco(selectedItem.getPreco());

                    // Adiciona o novo item à outra TableView
                    targetTableView.getItems().add(newItem);
                }

                // Reduz a quantidade do item selecionado na tabela de origem
                selectedItem.setQuantidade(selectedItem.getQuantidade() - quantidade);

                // Se a quantidade do item na tabela de origem for zero, remove o item
                if (selectedItem.getQuantidade() == 0) {
                    sourceTableView.getItems().remove(selectedItem);
                }

                // Atualiza os totais
                atualizarTotal();
                quantidadeValida = true;  // Sai do loop após um movimento bem-sucedido
            } else {
                // Se a quantidade for zero, apenas retorna sem exibir erro
                break;
            }
        }
    }

    @FXML
    private void moverTodosParaDireita() {
        moverTodos(tableViewFecharConta, tableViewFecharConta2);
    }

    @FXML
    private void moverTodosParaEsquerda() {
        moverTodos(tableViewFecharConta2, tableViewFecharConta);
    }

    private void moverTodos(TableView<ItemPedido> sourceTableView, TableView<ItemPedido> targetTableView) {
        // Obtém todos os itens da tabela de origem
        List<ItemPedido> todosItens = new ArrayList<>(sourceTableView.getItems());

        // Move cada item para a tabela de destino
        for (ItemPedido item : todosItens) {
            // Verifica se o item já existe na tabela de destino
            Optional<ItemPedido> existingItem = targetTableView.getItems()
                    .stream()
                    .filter(i -> i.getProduto().equals(item.getProduto()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Se o item já existe, apenas atualiza a quantidade
                existingItem.get().setQuantidade(existingItem.get().getQuantidade() + item.getQuantidade());
            } else {
                // Caso contrário, cria um novo ItemPedido
                ItemPedido newItem = new ItemPedido();
                newItem.setProduto(item.getProduto());
                newItem.setQuantidade(item.getQuantidade());
                newItem.setPreco(item.getPreco());

                // Adiciona o novo item à tabela de destino
                targetTableView.getItems().add(newItem);
            }
        }

        // Limpa todos os itens da tabela de origem
        sourceTableView.getItems().clear();

        // Atualiza os totais
        atualizarTotal();
    }

    //Método para obter a quantidade do item a ser movido
    private int obterQuantidade() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quantidade");
        dialog.setHeaderText(null);
        dialog.setContentText("Insira a quantidade:");

        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int quantidade = Integer.parseInt(result.get());
                    if (quantidade > 0) {
                        return quantidade;
                    } else {
                        exibirAlerta("Quantidade inválida", "A quantidade deve ser maior que zero.");
                    }
                } catch (NumberFormatException e) {
                    //o user carregou ok sem ter inserido nada
                    exibirAlerta("Quantidade inválida", "Por favor, insira um valor numérico válido.");
                }
            } else {
                // O user pressionou Cancelar
                return 0;
            }
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

    //Método para obter a data atual do sistema
    private String obterDataAtual() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    //Método para gerar o "recibo de compra" em formato PDF
    private void gerarReciboFormatoPDF(String metodoPagamento) {
        Document document = new Document();
        String imageSrc = getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm();
        String nomeFuncionario = SessaoUtilizador.getInstance().getNomeFuncionario();
        String nomePasta = "Recibos";
        String nomeArquivoPDF = nomePasta + "/recibo_atendimento" + idPagamentoAtendimento + "_precoPago" + totalConta2 + "€.pdf";

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
            document.add(new Paragraph("Método de Pagamento: " + metodoPagamento));
            document.add(new Paragraph("___________________________________________________________________"));
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

            // Obtém os itens da primeira TableView (tableViewFecharConta)
            ObservableList<ItemPedido> itensConta = tableViewFecharConta2.getItems();
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
            exibirAlerta("Erro", "Ocorreu um erro ao gerar o recibo: " + e.getMessage());
        } finally {
            document.close();
        }
    }

    //Método para obter o valor total da tableview
    private double extrairTotal(Label label) {
        String valorTotal = label.getText();
        // Substituir vírgulas por pontos para permitir a conversão correta
        valorTotal = valorTotal.replaceAll(",", ".");
        // Remove o símbolo € e espaços em branco
        valorTotal = valorTotal.replaceAll("€", "").trim();
        // Remove espaços extras e converte para double
        return Double.parseDouble(valorTotal);
    }

    //Método para exibir um alerta com o valor do troco a receber
    private void mostrarTroco(double troco) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Troco");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Seu troco é de %.2f €", troco));
        alert.showAndWait();
    }

    //Método associado ao botão da tableview para pagar em dinheiro
    @FXML
    private void pagarComDinheiro(MouseEvent mouseEvent) {

        if (tableViewFecharConta2.getItems().isEmpty()) {
            exibirAlerta("Pagamento em Dinheiro", "Não é possível fazer o pagamento, não existem itens para pagar.");
            return;
        }

        double total_a_pagar = totalConta2;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Pagamento com dinheiro");
        dialog.setHeaderText(null);
        dialog.setContentText("Insira a quantidade recebida:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double quantidadeRecebida = Double.parseDouble(result.get());
                if (quantidadeRecebida >= total_a_pagar) {
                    double troco = quantidadeRecebida - total_a_pagar;
                    // Mostrar o troco ao usuário
                    mostrarTroco(troco);
                    exibirAlerta("Pagamento", "O recibo_atendimento" + idPagamentoAtendimento + "_precoPago" + totalConta2 + "€.pdf foi gerado com sucesso.");
                    gerarReciboFormatoPDF("Dinheiro");
                    tableViewFecharConta2.getItems().clear();
                    labelTotal2.setText(formatarTotal(0));

                    if (labelTotal.getText().equals("0 €") && labelTotal2.getText().equals("0 €")) {
                        finalizarPagamento("Dinheiro");
                    } else if (labelTotal.getText().equals("0 €")) {
                        exibirAlerta("Pagamento", "Valor de pagamento em falta: " + labelTotal2.getText());
                    } else if (labelTotal2.getText().equals("0 €")) {
                        exibirAlerta("Pagamento", "Valor de pagamento em falta: " + labelTotal.getText());
                    }

                } else {
                    exibirAlerta("Quantia insuficiente", "A quantia recebida é menor que o total da compra.");
                }
            } catch (NumberFormatException e) {
                exibirAlerta("Quantidade inválida", "Por favor, insira um valor numérico válido.");
            }
        }
    }

    //Método associado ao botão da tableview da direita para pagar com cartão
    @FXML
    private void pagarComCartao(MouseEvent mouseEvent) {
        if (tableViewFecharConta2.getItems().isEmpty()) {
            exibirAlerta("Pagamento com Cartão", "Não é possível fazer o pagamento, não existem itens para pagar.");
            return;
        }

        double total = extrairTotal(labelTotal2);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pagamento com Cartão");
        alert.setHeaderText(null);
        alert.setContentText("Total a pagar: " + formatarTotal(total) + "\n\nInsira o cartão e pressione OK.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Simulando inserção do cartão
            boolean pinCorreto = false;

            while (!pinCorreto && tentativasPin < 3) {
                TextInputDialog pinDialog = new TextInputDialog();
                pinDialog.setTitle("PIN");
                pinDialog.setHeaderText(null);
                pinDialog.setContentText("Insira o PIN do cartão:");

                Optional<String> pinResult = pinDialog.showAndWait();
                if (pinResult.isPresent()) {
                    String pinInput = pinResult.get();

                    // Verifica se o PIN tem exatamente 4 dígitos
                    if (pinInput.length() == 4 && pinInput.matches("\\d+")) {
                        if (pinInput.equals("1234")) {
                            // Reseta a contagem de tentativas em caso de PIN correto
                            tentativasPin = 0;
                            pinCorreto = true;
                            // Exibir "Em autorização" por 2 segundos
                            exibirAguarde();
                        } else {
                            tentativasPin++;
                            exibirAlerta("PIN Incorreto", "PIN incorreto. Tentativa " + tentativasPin + " de 3.");
                        }
                    } else {
                        exibirAlerta("PIN Inválido", "O PIN deve conter exatamente 4 dígitos numéricos.");
                    }
                } else {
                    exibirAlerta("Operação Cancelada", "A operação foi cancelada.");
                    return;
                }
            }

            if (tentativasPin >= 3) {
                exibirAlerta("Tentativas Excedidas", "Você excedeu o número máximo de tentativas. A compra foi cancelada.");
                // Adicione aqui qualquer outra ação que você queira executar ao exceder as tentativas
            }
        } else {
            exibirAlerta("Pagamento Cancelado", "O pagamento com cartão foi cancelado.");
        }
    }

    //Método para simular o processo de pagamento em autorização (pagar com cartão) da tableview da direita
    private void exibirAguarde() {

        Alert aguardeAlert = new Alert(Alert.AlertType.INFORMATION);
        aguardeAlert.setTitle("Pagamento");
        aguardeAlert.setHeaderText(null);
        aguardeAlert.setContentText("Pagamento em autorização, por favor aguarde...");
        aguardeAlert.show();

        // Aguardar por 2 segundos antes de fechar o alerta
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            aguardeAlert.close();
            // Exibe a próxima mensagem após 2 segundos
            Platform.runLater(() -> {
                exibirPagamentoAutorizado();
                exibirAlerta("Pagamento", "O recibo_atendimento" + idPagamentoAtendimento + "_precoPago" + totalConta2 + "€.pdf foi gerado com sucesso.");
                gerarReciboFormatoPDF("Cartão de Crédito");
                tableViewFecharConta2.getItems().clear();
                labelTotal2.setText(formatarTotal(0));

                if (labelTotal.getText().equals("0 €") && labelTotal2.getText().equals("0 €")) {
                    finalizarPagamento("Cartão de Crédito");
                } else if (labelTotal.getText().equals("0 €")) {
                    exibirAlerta("Pagamento", "Valor de pagamento em falta: " + labelTotal2.getText());
                } else if (labelTotal2.getText().equals("0 €")) {
                    exibirAlerta("Pagamento", "Valor de pagamento em falta: " + labelTotal.getText());
                }
            });
        });
        pause.play();
    }

    //Método para exibir o sucesso do pagamento (pagar com cartão) da tableview da direita
    private void exibirPagamentoAutorizado() {
        Platform.runLater(() -> {
            Alert autorizadoAlert = new Alert(Alert.AlertType.INFORMATION);
            autorizadoAlert.setTitle("Pagamento");
            autorizadoAlert.setHeaderText(null);
            autorizadoAlert.setContentText("Pagamento Autorizado!");
            autorizadoAlert.showAndWait();
        });
    }

    private void alterarStatusMesa(int mesaId, String novoStatus) {
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                String query = "UPDATE Mesa SET status = ? WHERE id_mesa = ?";
                try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                    preparedStatement.setString(1, novoStatus);
                    preparedStatement.setInt(2, mesaId);
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
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void atualizarAtendimento(int idPagamentoAtendimento, double precoTotal, String tipoPagamento, Timestamp data_hora_fim) {
        System.out.println("Atualizando quantidade na base de dados: Atendimento = " + idPagamentoAtendimento + ", Preço total = " + precoTotal + ", Tipo Pagamento = " + tipoPagamento);
        ConectarBD bd = new ConectarBD();
        Connection conexao = bd.conectar();

        if (conexao != null) {
            try {
                String query = "UPDATE PagamentoAtendimento SET preco_total = ?, tipo_pagamento = ?, data_hora_fim = ? WHERE id_pagatendimento = ?";
                try (PreparedStatement preparedStatement = conexao.prepareStatement(query)) {
                    preparedStatement.setDouble(1, precoTotal);
                    preparedStatement.setString(2, tipoPagamento);
                    preparedStatement.setTimestamp(3, data_hora_fim);
                    preparedStatement.setInt(4, idPagamentoAtendimento);
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
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void finalizarPagamento(String tipoPagamento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        alterarStatusMesa(mesaId, "Livre");
        atualizarAtendimento(idPagamentoAtendimento, valorTotalAtendimento, tipoPagamento, Timestamp.valueOf(LocalDateTime.parse(formattedDateTime, formatter)));

        // Aguardar por 2 segundos antes de fechar o alerta
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> {
            tableViewFecharConta2.getItems().clear();
            labelTotal2.setText(formatarTotal(0));

            try {
                // Obter a sessão do usuário
                SessaoUtilizador sessao = SessaoUtilizador.getInstance();
                int cargo = sessao.getCargo();
                // Se o cargo for 1, carregar a interface do gerente
                if (cargo == 1) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/GerenteBarraLateral.fxml"));
                    Parent root = loader.load();

                    Scene scene = tableViewFecharConta.getScene();
                    scene.setRoot(root);
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Mesas.fxml"));
                    Parent root = loader.load();

                    DashboardMesaController mesas = loader.getController();
                    mesas.configurarBotaoTerminarSessao();
                    mesas.configurarTitulo();

                    Scene scene = tableViewFecharConta.getScene();
                    scene.setRoot(root);
                }

            } catch (IOException e) {
                // Imprimir o stack trace em caso de exceção
                e.printStackTrace();
            }
        });
        pause.play();
    }
}
