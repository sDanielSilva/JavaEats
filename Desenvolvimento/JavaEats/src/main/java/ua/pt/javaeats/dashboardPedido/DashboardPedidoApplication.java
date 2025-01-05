package ua.pt.javaeats.dashboardPedido;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DashboardPedidoApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ua/pt/javaeats/Pedido.fxml"));

        primaryStage.setTitle("JavaEats Dashboard");
        primaryStage.setScene(new Scene(root, 1920, 1080));

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        primaryStage.show();
    }
}
