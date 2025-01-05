package ua.pt.javaeats.dashboardGerirItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class dashboardGerirItemApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ua/pt/javaeats/item.fxml"));

        primaryStage.setTitle("JavaEats Dashboard Item");
        Image image = new Image(getClass().getResource("/ua/pt/javaeats/Images/javaetas-removebg-preview.png").toExternalForm());
        primaryStage.getIcons().add(image);
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
