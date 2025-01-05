package ua.pt.javaeats.dashboardReservarMesas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class dashboardReservarMesasApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ua/pt/javaeats/reservas.fxml"));

        primaryStage.setTitle("JavaEats Dashboard Reservas");
        primaryStage.setScene(new Scene(root, 900, 700));
        Image image = new Image(getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm());
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}