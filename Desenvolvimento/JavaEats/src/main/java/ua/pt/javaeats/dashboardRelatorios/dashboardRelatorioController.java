package ua.pt.javaeats.dashboardRelatorios;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import ua.pt.javaeats.ConectarBD;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class dashboardRelatorioController {

    @FXML
    private  Label rotuloItemMaisVendido;
    @FXML
    private  Label rotuloVendasPorCategoria;
    @FXML
    private BarChart<String, Number> barChart1;
    @FXML
    private BarChart<String, Number> barChart2;
    @FXML
    private Label rotuloContagemPagamentos;

    private Connection conexao;
    @FXML
    private DatePicker dataInicioPicker;

    @FXML
    private DatePicker dataFimPicker;


    public void initialize() {
        ConectarBD conectarBD = new ConectarBD();
        conexao = conectarBD.conectar();
        if (conexao != null) {

        } else {
            mostrarAlerta("ERRO", "Falha na conexão com a base de dados, verifique a ligação.");
            conectarBD.desconectar();
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

    @ FXML
    void gerarRelatorio() {
        // Obter as datas de início e fim a partir dos DatePickers
        LocalDate dataInicio = dataInicioPicker.getValue();
        LocalDate dataFim = dataFimPicker.getValue();

        // Verificar se as datas de início e fim foram selecionadas
        if (dataInicio != null && dataFim != null) {
            // Verificar se a data de fim é anterior à data de início
            if (dataFim.isBefore(dataInicio)) {
                // Se a data de fim for anterior à data de início, mostrar um alerta de erro
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("A data final deve ser igual ou posterior à data inicial.");
                alert.showAndWait();
                return;
            }
            // Converter as datas de início e fim para strings
            String dataInicioString = dataInicio.toString();
            String dataFimString = dataFim.toString();

            // Calcular o total faturado por dia, contagem de tipo de pagamento, total faturado por mesa, vendas por categoria e item mais vendido
            calcularTotalFaturadoPorDia(dataInicioString, dataFimString);
            contagemTipoPagamento(dataInicioString, dataFimString);
            calcularTotalFaturadoPorMesa(dataInicioString, dataFimString);
            vendasPorCategoria(dataInicioString, dataFimString);
            itemMaisVendido(dataInicioString, dataFimString);
        } else {
            // Lógica para lidar com datas não selecionadas
        }
    }

    void calcularTotalFaturadoPorDia (String dataInicioString, String dataFimString){
        // SQL para obter o total faturado por dia
        String sql = "SELECT DATE(data_hora_inicio) as dia, SUM(preco_total) FROM PagamentoAtendimento WHERE data_hora_inicio BETWEEN ? AND ? GROUP BY dia";

        try {
            // Preparar a declaração SQL
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            ResultSet rs = stmt.executeQuery();

            // Criar uma nova série para o gráfico
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (rs.next()) {
                // Adicionar os dados ao gráfico
                XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString(1), rs.getDouble(2));
                series.getData().add(data);

                // Adicionar um tooltip aos nós do gráfico
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
                        if (newNode != null) {
                            Tooltip tooltip = new Tooltip(String.format("€%.2f", data.getYValue()));
                            tooltip.setShowDelay(Duration.ZERO);
                            tooltip.setStyle("-fx-font-size: 14px;");
                            Tooltip.install(newNode, tooltip);
                        }
                    }
                });
            }

            // Atualizar o gráfico na thread da interface gráfica
            Platform.runLater(() -> {
                barChart1.getData().clear();
                barChart1.getData().add(series);
                barChart1.layout();
            });

            // Adicionar legendas aos eixos e permitir que o eixo Y se adapte ao valor mais alto
            CategoryAxis xAxis = (CategoryAxis) barChart1.getXAxis();
            xAxis.setLabel("Dia");

            NumberAxis yAxis = (NumberAxis) barChart1.getYAxis();
            yAxis.setLabel("Valor (€)");
            yAxis.setAutoRanging(true);
            yAxis.setTickUnit(100);

        } catch (SQLException e) {
            // Imprimir a pilha de exceções se ocorrer um erro SQL
            e.printStackTrace();
        }
    }

    String contagemTipoPagamento (String dataInicioString, String dataFimString){
        StringBuilder result = new StringBuilder();
        try {
            // Consulta SQL para obter a contagem e o total de pagamentos em dinheiro
            String sql = "SELECT COUNT(*), SUM(preco_total) FROM PagamentoAtendimento WHERE tipo_pagamento = 'Dinheiro' AND data_hora_inicio BETWEEN ? AND ?";

            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            ResultSet rs = stmt.executeQuery();

            // Se houver resultados, adicione-os ao rótulo de contagem de pagamentos
            if (rs.next()) {
                String texto = "Pagamentos em dinheiro: " + rs.getInt(1) + ", Montante total: " + String.format("%.2f", rs.getDouble(2)) + " €";
                rotuloContagemPagamentos.setText(texto);
            }

            // Consulta SQL para obter a contagem e o total de pagamentos com cartão
            sql = "SELECT COUNT(*), SUM(preco_total) FROM PagamentoAtendimento WHERE tipo_pagamento = 'Cartão' AND data_hora_inicio BETWEEN ? AND ?";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            rs = stmt.executeQuery();

            // Se houver resultados, adicione-os ao rótulo de contagem de pagamentos
            if (rs.next()) {
                String texto = rotuloContagemPagamentos.getText() + "\nPagamentos com cartão: " + rs.getInt(1) + ", Montante total: " + String.format("%.2f", rs.getDouble(2)) + " €";
                rotuloContagemPagamentos.setText(texto);
                result.append(texto).append("\n");
            }

        } catch (SQLException e) {
            // Imprimir a pilha de exceções se ocorrer um erro SQL
            e.printStackTrace();
        }
        return result.toString();
    }

    void calcularTotalFaturadoPorMesa (String dataInicioString, String dataFimString){
        // Consulta SQL para obter o total faturado por mesa
        String sql = "SELECT id_mesa, SUM(preco_total) FROM PagamentoAtendimento WHERE data_hora_inicio BETWEEN ? AND ? GROUP BY id_mesa";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            ResultSet rs = stmt.executeQuery();

            // Criar uma nova série para o gráfico
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (rs.next()) {
                // Adicionar os dados ao gráfico
                XYChart.Data<String, Number> data = new XYChart.Data<>(rs.getString(1), rs.getDouble(2));
                series.getData().add(data);

                // Adicionar um tooltip aos nós do gráfico
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
                        if (newNode != null) {
                            Tooltip tooltip = new Tooltip(String.format("€%.2f", data.getYValue()));
                            tooltip.setShowDelay(Duration.ZERO);
                            tooltip.setStyle("-fx-font-size: 14px;");
                            Tooltip.install(newNode, tooltip);
                        }
                    }
                });
            }

            // Atualizar o gráfico na thread da interface gráfica
            Platform.runLater(() -> {
                barChart2.getData().clear();
                barChart2.getData().add(series);
                barChart2.layout();
            });

            // Adicionar legendas aos eixos e permitir que o eixo Y se adapte ao valor mais alto
            CategoryAxis xAxis = (CategoryAxis) barChart2.getXAxis();
            xAxis.setLabel("Mesa");

            NumberAxis yAxis = (NumberAxis) barChart2.getYAxis();
            yAxis.setLabel("Valor (€)");
            yAxis.setAutoRanging(true);
            yAxis.setTickUnit(100);

        } catch (SQLException e) {
            // Imprimir a pilha de exceções se ocorrer um erro SQL
            e.printStackTrace();
        }
    }


    String itemMaisVendido (String dataInicioString, String dataFimString){
        // Consulta SQL para obter o item mais vendido
        String sql = "SELECT Item.nome as nome_item, SUM(PedidoDetalhe.quantidade) as quantidade, SUM(PedidoDetalhe.quantidade * Item.preco) as total " +
                "FROM PedidoDetalhe INNER JOIN Item ON PedidoDetalhe.id_item = Item.id_item " +
                "WHERE PedidoDetalhe.data_hora BETWEEN ? AND ? " +
                "GROUP BY Item.nome " +
                "ORDER BY quantidade DESC LIMIT 1";

        StringBuilder result = new StringBuilder();
        try {
            // Preparar a declaração SQL
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            ResultSet rs = stmt.executeQuery();

            // Se houver resultados, adicione-os ao rótulo do item mais vendido
            if (rs.next()) {
                String texto = "Item mais vendido: " + rs.getString("nome_item") + ", Quantidade: " + rs.getInt("quantidade") + ", Valor total: " + rs.getInt("total") + " €";
                rotuloItemMaisVendido.setText(texto);
                result.append(texto).append("\n");
            }
        } catch (SQLException e) {
            // Imprimir a pilha de exceções se ocorrer um erro SQL
            e.printStackTrace();
        }
        return result.toString();
    }

    String vendasPorCategoria (String dataInicioString, String dataFimString){
        // Consulta SQL para obter a categoria mais popular
        String sql = "SELECT Categoria.nome as categoria, SUM(PedidoDetalhe.quantidade) as quantidade " +
                "FROM PedidoDetalhe INNER JOIN Item ON PedidoDetalhe.id_item = Item.id_item " +
                "INNER JOIN Categoria ON Item.id_categoria = Categoria.id_categoria " +
                "WHERE PedidoDetalhe.data_hora BETWEEN ? AND ? " +
                "GROUP BY Categoria.nome " +
                "ORDER BY quantidade DESC LIMIT 1";

        StringBuilder result = new StringBuilder();
        try {
            // Preparar a declaração SQL
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataInicioString);
            stmt.setString(2, dataFimString);
            ResultSet rs = stmt.executeQuery();

            // Se houver resultados, adicione-os ao rótulo de vendas por categoria
            while (rs.next()) {
                String texto = "Categoria mais popular: " + rs.getString("categoria") + ", Quantidade de itens vendida: " + rs.getInt("quantidade");
                rotuloVendasPorCategoria.setText(texto);
                result.append(texto).append("\n");
            }
        } catch (SQLException e) {
            // Imprimir a pilha de exceções se ocorrer um erro SQL
            e.printStackTrace();
        }
        return result.toString();
    }


    public void imprimirRelatorio(ActionEvent actionEvent) {
        // Verificar se o relatório foi gerado
        if (dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null) {
            // Se o relatório não foi gerado, mostrar um alerta de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Gere o relatório antes de tentar imprimi-lo.");
            alert.showAndWait();
            return;
        }

        // Criar um novo documento PDF
        Document document = new Document();
        try {
            int i = 1;
            // Criar um diretório para os relatórios, se ele não existir
            File directory = new File("Relatorios");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Criar um novo arquivo para o relatório
            File file;
            do {
                String filePath = "Relatorios/relatorio" + i + ".pdf";
                file = new File(filePath);
                i++;
            } while (file.exists());

            // Iniciar a escrita no documento PDF
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Adicionar o título e a data ao relatório
            LocalDate dataInicio = dataInicioPicker.getValue();
            LocalDate dataFim = dataFimPicker.getValue();
            String dataInicioString = dataInicio.toString();
            String dataFimString = dataFim.toString();
            Paragraph title = new Paragraph("Relatório " + dataInicioString + " : " + dataFimString, FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD));
            document.add(title);
            document.add(new Paragraph(" "));

            // Adicionar os gráficos ao relatório
            WritableImage snapshot = barChart1.snapshot(new SnapshotParameters(), null);
            WritableImage snapshot2 = barChart2.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            BufferedImage bufferedImage2 = SwingFXUtils.fromFXImage(snapshot2, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            ImageIO.write(bufferedImage2, "png", baos2);
            Image image = Image.getInstance(baos.toByteArray());
            Image image2 = Image.getInstance(baos2.toByteArray());

            // Redimensionar as imagens para caberem no documento
            float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float height = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            image.scaleToFit(width, height);
            image2.scaleToFit(width, height);

            // Adicionar os resultados das vendas por categoria, item mais vendido e contagem de pagamento ao relatório
            String vendasPorCategoriaResult = vendasPorCategoria(dataInicioString, dataFimString);
            Paragraph vendasPorCategoriaParagraph = new Paragraph(vendasPorCategoriaResult);

            String itemMaisVendidoResult = itemMaisVendido(dataInicioString, dataFimString);
            Paragraph itemMaisVendidoParagraph = new Paragraph(itemMaisVendidoResult);

            String contagemPagamentoResult = contagemTipoPagamento(dataInicioString, dataFimString);
            Paragraph contagemPagamentoParagraph = new Paragraph(contagemPagamentoResult);

            // Adicionar tudo ao documento
            document.add(image);
            document.add(image2);
            document.add(contagemPagamentoParagraph);
            document.add(itemMaisVendidoParagraph);
            document.add(vendasPorCategoriaParagraph);

        } catch (Exception e) {
            // Imprimir a pilha de exceções se ocorrer um erro
            e.printStackTrace();
        } finally {
            // Fechar o documento e mostrar um alerta de sucesso
            document.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Relatório guardado na pasta 'Relatorios'! ");
            alert.showAndWait();
        }
    }
}
