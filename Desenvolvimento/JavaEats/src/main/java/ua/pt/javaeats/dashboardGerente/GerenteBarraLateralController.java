package ua.pt.javaeats.dashboardGerente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ua.pt.javaeats.SessaoUtilizador;
import ua.pt.javaeats.dashboardBalcao.dashboardBalcaoController;
import ua.pt.javaeats.dashboardGerirFuncionario.dashboardGerirFuncionarioController;

import java.io.IOException;

public class GerenteBarraLateralController {

    @FXML
    private Button btnZonas;
    @FXML
    private Button btnSair;
    @FXML
    private Button btnGerirCargo;
    @FXML
    private Button btnVerRestaurante;
    @FXML
    private Button btnGerirCategorias;
    @FXML
    private Button btnGerirMesas;
    @FXML
    private Button btnRelatorios;
    @FXML
    private Button btnGerirFuncionarios;
    @FXML
    private Button btnGerirItens;
    @FXML
    private Button btnVerCozinha;
    @FXML
    private BorderPane layoutPrincipal;


    @FXML
    public void initialize() {
        layoutPrincipal.getStylesheets().add(getClass().getResource("/ua/pt/javaeats/CSS/barraLateral.css").toExternalForm());


        Tooltip tooltipSair = new Tooltip("Terminar Sessão");
        tooltipSair.setShowDelay(Duration.seconds(0));
        btnSair.setTooltip(tooltipSair);

        Tooltip tooltipVerRestaurante = new Tooltip("Ver Restaurante");
        tooltipVerRestaurante.setShowDelay(Duration.seconds(0));
        btnVerRestaurante.setTooltip(tooltipVerRestaurante);

        Tooltip tooltipGerirFuncionarios = new Tooltip("Gerir Funcionários");
        tooltipGerirFuncionarios.setShowDelay(Duration.seconds(0));
        btnGerirFuncionarios.setTooltip(tooltipGerirFuncionarios);

        Tooltip tooltipGerirCategorias = new Tooltip("Gerir Categorias");
        tooltipGerirCategorias.setShowDelay(Duration.seconds(0));
        btnGerirCategorias.setTooltip(tooltipGerirCategorias);

        Tooltip tooltipGerirItens = new Tooltip("Gerir Itens");
        tooltipGerirItens.setShowDelay(Duration.seconds(0));
        btnGerirItens.setTooltip(tooltipGerirItens);

        Tooltip tooltipGerirMesas = new Tooltip("Gerir Mesas");
        tooltipGerirMesas.setShowDelay(Duration.seconds(0));
        btnGerirMesas.setTooltip(tooltipGerirMesas);

        Tooltip tooltipRelatorios = new Tooltip("Relatórios");
        tooltipRelatorios.setShowDelay(Duration.seconds(0));
        btnRelatorios.setTooltip(tooltipRelatorios);

        Tooltip tooltipVerCozinha = new Tooltip("Ver Cozinha");
        tooltipVerCozinha.setShowDelay(Duration.seconds(0));
        btnVerCozinha.setTooltip(tooltipVerCozinha);

        Tooltip tooltipGerirCargo = new Tooltip("Gerir Cargos");
        tooltipGerirCargo.setShowDelay(Duration.seconds(0));
        btnGerirCargo.setTooltip(tooltipGerirCargo);

        Tooltip tooltipGerirZonas = new Tooltip("Gerir Zonas");
        tooltipGerirZonas.setShowDelay(Duration.seconds(0));
        btnZonas.setTooltip(tooltipGerirZonas);

        btnSair.setOnAction(e -> sair());
        btnGerirCargo.setOnAction(e -> abrirConfigCargos());
        btnVerCozinha.setOnAction(e -> abrirCozinha());
        btnGerirFuncionarios.setOnAction(e -> abrirConfigFuncionarios());
        btnGerirItens.setOnAction(e -> abrirConfigItens());
        btnGerirMesas.setOnAction(e -> abrirConfigMesas());
        btnRelatorios.setOnAction(e -> abrirRelatorios());
        btnGerirCategorias.setOnAction(e -> abrirConfigCategorias());
        btnVerRestaurante.setOnAction(e -> abrirRestaurante());
        btnZonas.setOnAction(e -> abrirConfigZonas());

        abrirRestaurante();
    }


    private void sair() {
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



    private void abrirConfigCargos() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/cargo.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // Imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirCozinha() {
        try {
            //Carrega a interface da cozinha
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Balcao.fxml"));
            Parent root = loader.load();

            //Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);

            //Obtém o controlador da cozinha
            dashboardBalcaoController cozinhaController = loader.getController();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void abrirRestaurante() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Mesas.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // Imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirConfigCategorias() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Categorias.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirRelatorios() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Relatorios.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirConfigMesas() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/gerirMesas.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirConfigFuncionarios() {
        try {
            // Carrega a interface de configurações de funcionários
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Funcionario.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
            // Obter a instância do controller da dashboardGerirFuncionario
            dashboardGerirFuncionarioController controller = loader.getController();

            // Iniciar a comunicação serial no controller da dashboardGerirFuncionario
            if (controller != null) {
                controller.iniciarComunicacaoSerialExterno();
            }
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirConfigItens() {
        try {
            // Carrega a interface de configurações de itens
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/item.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }

    private void abrirConfigZonas() {
        try {
            // Carrega a interface de configurações de itens
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Zonas.fxml"));
            Parent root = loader.load();

            // Define como o conteúdo do centro do layoutPrincipal
            layoutPrincipal.setCenter(root);
        } catch (IOException e) {
            // imprime o stack trace em caso de exceção
            e.printStackTrace();
        }
    }
}
