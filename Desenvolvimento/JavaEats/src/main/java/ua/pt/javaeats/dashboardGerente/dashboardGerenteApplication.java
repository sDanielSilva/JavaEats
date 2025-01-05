package ua.pt.javaeats.dashboardGerente;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class dashboardGerenteApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/pt/javaeats/GerenteDashboard.fxml"));
        Parent root = loader.load();

        dashboardGerenteController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("JavaEats Dashboard Gerente");
        Scene scene = new Scene(root, 1920, 1080);
        Image image = new Image(getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm());
        primaryStage.getIcons().add(image);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        primaryStage.setMinWidth(1920);
        primaryStage.setMinHeight(1080);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
