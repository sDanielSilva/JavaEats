module ua.pt.javaeats {
    requires javafx.swing;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires com.fazecast.jSerialComm;
    requires itextpdf;
    requires jfxtras.controls;

    opens ua.pt.javaeats to javafx.fxml;
    exports ua.pt.javaeats;
    exports ua.pt.javaeats.dashboardPrincipal;
    exports ua.pt.javaeats.dashboardMesa;
    exports ua.pt.javaeats.dashboardPedido;
    exports ua.pt.javaeats.dashboardGerirItem;
    exports ua.pt.javaeats.dashboardGerirCategoria;
    exports ua.pt.javaeats.dashboardGerirFuncionario;
    exports ua.pt.javaeats.dashboardGerirMesas;
    exports ua.pt.javaeats.DashboardFecharConta;
    exports ua.pt.javaeats.dashboardGerente;
    exports ua.pt.javaeats.dashboardRelatorios;
    exports ua.pt.javaeats.dashboardReservarMesas;
    exports ua.pt.javaeats.dashboardGerirCargo;
    opens ua.pt.javaeats.dashboardGerirCargo to javafx.fxml;
    opens ua.pt.javaeats.dashboardReservarMesas to javafx.fxml;
    opens ua.pt.javaeats.dashboardRelatorios to javafx.fxml;
    opens ua.pt.javaeats.dashboardGerente to javafx.fxml;
    opens ua.pt.javaeats.dashboardGerirMesas to javafx.fxml;
    opens ua.pt.javaeats.dashboardGerirFuncionario to javafx.fxml;
    opens ua.pt.javaeats.dashboardPrincipal to javafx.fxml;
    opens ua.pt.javaeats.dashboardMesa to javafx.fxml;
    opens ua.pt.javaeats.dashboardPedido to javafx.fxml;
    opens ua.pt.javaeats.dashboardGerirItem to javafx.fxml;
    opens ua.pt.javaeats.dashboardGerirCategoria to javafx.fxml;
    opens ua.pt.javaeats.DashboardFecharConta to javafx.fxml;
    exports ua.pt.javaeats.dashboardBalcao;
    opens ua.pt.javaeats.dashboardBalcao to javafx.fxml;
    exports ua.pt.javaeats.dashboardGerirZonas;
    opens ua.pt.javaeats.dashboardGerirZonas to javafx.fxml;
}
