package ua.pt.javaeats;

import javafx.application.Platform;

import java.lang.constant.ConstantDesc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectarBD {
    private static String url = "your_url";
    private static String utilizador = "your_user";
    private static String password = "your_password";

    private static Connection conexao;

    public static Connection conectar() {
        try {
            conexao = DriverManager.getConnection(url, utilizador, password);
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar à Base de Dados!");
        }
        return conexao;
    }

    public void desconectar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexão encerrada com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao encerrar a conexão!" + e.getMessage());
            e.printStackTrace();
        }
    }
    public boolean isConexaoFechada() {
        try {
            return conexao == null || conexao.isClosed();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar o estado da conexão!" + e.getMessage());
            e.printStackTrace();
            return true;

        }
    }
}
