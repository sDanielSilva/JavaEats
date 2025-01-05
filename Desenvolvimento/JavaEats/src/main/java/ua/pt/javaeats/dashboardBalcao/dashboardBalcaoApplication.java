package ua.pt.javaeats.dashboardBalcao;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class dashboardBalcaoApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/Balcao.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("JavaEats Dashboard Balcão");
        Image image = new Image(getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm());
        primaryStage.getIcons().add(image);
        primaryStage.setScene(new Scene(root, 1000, 1080));

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        primaryStage.show();

        // Após carregar o FXML, obtenha o controlador e chame o método exibirDetalhesPedidos()
        dashboardBalcaoController controller = loader.getController();
        controller.exibirDetalhesPedidos();
    }

    public static void main(String[] args) {
        launch(args);
    }
}