package ua.pt.javaeats.dashboardGerente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class dashboardGerenteController {
    @FXML
    private Label infoLabel;
    @FXML
    private Label infoLabelRelatorio;
    @FXML
    private Label infoLabelMesas;
    @FXML
    private Label infoLabelCategorias;
    @FXML
    private Label infoLabelItems;
    @FXML
    private Label infoLabelVerRestaurante;
    private Stage primaryStage;
    @FXML
    private AnchorPane janelaPrincipal;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            janelaPrincipal.getChildren().clear(); // Limpa qualquer conteúdo existente na AnchorPane
            janelaPrincipal.getChildren().add(root); // Define o conteúdo carregado do FXML na AnchorPane

        } catch (IOException e) {
          //  e.printStackTrace();
        }
    }

    public void utilizadoresIcon() {
        loadFXML("/ua/pt/javaeats/Funcionario.fxml");
    }

    public void reportIcon() {
        loadFXML("/ua/pt/javaeats/Relatorios.fxml");
    }

    public void mesaIcon() {
        loadFXML("/ua/pt/javaeats/gerirMesas.fxml");
    }

    public void itemIcon() {
        loadFXML("/ua/pt/javaeats/item.fxml");
    }

    public void categoriaIcon() {
        loadFXML("/ua/pt/javaeats/Categorias.fxml");
    }

    public void homeIcon() {
        loadFXML("/ua/pt/javaeats/dashboard.fxml");
    }

    public void verRestauranteIcon() {
        loadFXML("/ua/pt/javaeats/Mesas.fxml");
    }

    public void relatoriosIcon(MouseEvent mouseEvent) {
        loadFXML("/ua/pt/javaeats/Relatorios.fxml");
    }

    public void mesasIcon(MouseEvent mouseEvent) {
        loadFXML("/ua/pt/javaeats/gerirMesas.fxml");
    }

    public void itemsIcon(MouseEvent mouseEvent) {
        loadFXML("/ua/pt/javaeats/item.fxml");
    }

    public void utilizadoresIconLegenda(MouseEvent mouseEvent) {
        infoLabel.setVisible(true);
        infoLabel.setText("Funcionários");
    }

    public void limparInfoLabel(MouseEvent mouseEvent) {
        infoLabel.setVisible(false);
        infoLabel.setText("");
    }

    public void limparInfoLabelRelatorio(MouseEvent mouseEvent) {
        infoLabelRelatorio.setVisible(false);
        infoLabelRelatorio.setText("");
    }

    public void limparInfoLabelCategorias(MouseEvent mouseEvent) {
        infoLabelCategorias.setVisible(false);
        infoLabelCategorias.setText("");
    }

    public void limparInfoLabelItems(MouseEvent mouseEvent) {
        infoLabelItems.setVisible(false);
        infoLabelItems.setText("");
    }

    public void limparInfoLabelMesas(MouseEvent mouseEvent) {
        infoLabelMesas.setVisible(false);
        infoLabelMesas.setText("");
    }
    public void utilizadoresIconNaoLegenda(MouseEvent mouseEvent) {
        infoLabel.setVisible(false);
        infoLabel.setText("");
    }

    public void relatorioLegenda(MouseEvent mouseEvent) {
        infoLabelRelatorio.setVisible(true);
        infoLabelRelatorio.setText("Relatórios");
    }

    public void relatorioNaoLegenda(MouseEvent mouseEvent) {
        infoLabelRelatorio.setVisible(false);
        infoLabelRelatorio.setText("");
    }

    public void mesaLegenda(MouseEvent mouseEvent) {
        infoLabelMesas.setVisible(true);
        infoLabelMesas.setText("Mesas");
    }

    public void mesaNaoLegenda(MouseEvent mouseEvent) {
        infoLabelMesas.setVisible(false);
        infoLabelMesas.setText("");
    }

    public void itemLegenda(MouseEvent mouseEvent) {
        infoLabelItems.setVisible(true);
        infoLabelItems.setText("Items");
    }

    public void itemNaoLegenda(MouseEvent mouseEvent) {
        infoLabelItems.setVisible(false);
        infoLabelItems.setText("");
    }

    public void categoriaLegenda(MouseEvent mouseEvent) {
        infoLabelCategorias.setVisible(true);
        infoLabelCategorias.setText("Categorias");
    }

    public void categoriaNaoLegenda(MouseEvent mouseEvent) {
        infoLabelCategorias.setVisible(false);
        infoLabelCategorias.setText("");
    }

    public void verRestauranteLegenda(MouseEvent mouseEvent) {
        infoLabelVerRestaurante.setVisible(true);
        infoLabelVerRestaurante.setText("Ver Restaurante");
    }

    public void verRestauranteNaoLegenda(MouseEvent mouseEvent) {
        infoLabelVerRestaurante.setVisible(false);
        infoLabelVerRestaurante.setText("");
    }

}