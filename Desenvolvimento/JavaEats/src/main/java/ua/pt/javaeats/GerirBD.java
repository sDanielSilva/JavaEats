package ua.pt.javaeats;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GerirBD {

    private Connection connection;
    public void ConectarBD(Connection connection){
        this.connection = connection;
    }

    public List<Item> importar_item() {
        List<Item> itens = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Item");

            while (rs.next()) {
                int id_item = rs.getInt("id_item");
                String nome_item = rs.getString("nome");
                double preco_item = rs.getDouble("preco");
                int id_categoria = rs.getInt("id_categoria");

                Item item = new Item(id_item, nome_item, preco_item, id_categoria, connection);
                itens.add(item);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Ocorreu um erro a carregar os itens da base de dados");
            e.printStackTrace();
        }

        return itens;
    }

    public void removerCozinhaPorId(int id) {
        try {
            String sql = "DELETE FROM sua_tabela WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public List<Pedido> importar_pedido(){
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Pedido_Restaurante");

            while (rs.next()) {
                int id_pedido = rs.getInt(Pedido.getId());
                String descricao = rs.getString(Pedido.getDescricao());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    public List<Cargo> importar_cargo() {
        List<Cargo> cargos = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Cargo");

            while (rs.next()) {
                int id_cargo = rs.getInt("id_cargo");
                String descricao = rs.getString("descricao");

                Cargo cargo = new Cargo(descricao);
                // Atribuir o ID separadamente
                cargo.setIdCargo(id_cargo);
                cargos.add(cargo);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Ocorreu um erro a carregar os cargos da base de dados");
            e.printStackTrace();
        }

        return cargos;
    }


    public Zona getZonaById(int id) {
        Zona zona = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Zonas WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                zona = new Zona(nome);
                zona.setId(id);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return zona;
    }


    public Categoria getCategoriaById(int id) {
        Categoria categoria = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Categoria WHERE id_categoria = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                categoria = new Categoria(nome);
                categoria.setId(id);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoria;
    }

    public Cargo getCargoById(int id) {
        Cargo cargo = null;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Cargo WHERE id_cargo = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String descricao = rs.getString("descricao");
                cargo = new Cargo(descricao);
                cargo.setIdCargo(id);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cargo;
    }


    public int getIdCargoPorNome(String nomeCargo) {
        int idCargo = -1;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id_cargo FROM Cargo WHERE descricao = ?");
            stmt.setString(1, nomeCargo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idCargo = rs.getInt("id_cargo");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idCargo;
    }

    public void guardar_cargo(Cargo cargo) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Cargo (descricao) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cargo.getDescricao());
            stmt.executeUpdate();

            // Obtém o ID gerado automaticamente após a inserção
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id_cargo = generatedKeys.getInt(1);
                cargo.setIdCargo(id_cargo);
            }

            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro a guardar o cargo");
            e.printStackTrace();
        }
    }

    public void remover_zona(Zona zona) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Zonas WHERE id = ?");
            stmt.setInt(1, zona.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro a remover a zona");
            e.printStackTrace();
        }
    }

    public void remover_cargo(Cargo cargo) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Cargo WHERE id_cargo = ?");
            stmt.setInt(1, cargo.getIdCargo());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao remover o cargo");
        }
    }

    public void editar_zona(Zona zona) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE Zonas SET nome = ? WHERE id = ?");
            stmt.setString(1, zona.getNome());
            stmt.setInt(2, zona.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro a editar  a zona");
            e.printStackTrace();
        }
    }

    public void editar_cargo(Cargo cargo) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE Cargo SET descricao = ? WHERE id_cargo = ?");
            stmt.setString(1, cargo.getDescricao());
            stmt.setInt(2, cargo.getIdCargo());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar o cargo");
            e.printStackTrace();
        }
    }


    public List<Zona> importar_zona(){
        List<Zona> zonas = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Zonas");
            while (rs.next()) {
                int id_zona = rs.getInt("id");
                String nome = rs.getString("nome");

                Zona zona = new Zona(nome);
                zona.setId(id_zona);
                zonas.add(zona);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro a carregar as zonas da base de dados");
        }
        return zonas;
    }

    public List<Mesa> importar_mesa(){
        List<Mesa> mesas = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Mesa");
            while (rs.next()) {
                int id_mesa = rs.getInt("id_mesa");
                String status = rs.getString("status");
                int id_zona = rs.getInt("id_zona");
                mesas.add(new Mesa(id_mesa, status, id_zona));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao carregar as Mesas");
        }
        return mesas;
    }

    public List<Categoria> importar_categoria(){
        List<Categoria> categorias = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Categoria");

            while (rs.next()) {
                int id_categoria = rs.getInt("id_categoria");
                String nome_categoria = rs.getString("nome");

                Categoria categoria = new Categoria(nome_categoria);

                categoria.setId(id_categoria);
                categorias.add(categoria);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Ocorreu um erro a carregar as categorias da base de dados");
            e.printStackTrace();
        }

        return categorias;
    }

    public int getIdCategoriaPorNome(String nomeCategoria) {
        int idCategoria = -1;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id_categoria FROM Categoria WHERE nome = ?");
            stmt.setString(1, nomeCategoria);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idCategoria = rs.getInt("id_categoria");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idCategoria;
    }


    public List<Funcionario> importar_funcionario() {
        List<Funcionario> funcionarios = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Funcionario");

            while (rs.next()) {
                int id_funcionario = rs.getInt("id_funcionario");
                String nome_funcionario = rs.getString("nome");
                String password_funcionario = rs.getString("password");
                int idCargo = rs.getInt("id_cargo");
                String idCartao = rs.getString("id_cartao");

                Funcionario funcionario = new Funcionario(id_funcionario, nome_funcionario, password_funcionario, idCargo, idCartao, connection);
                funcionarios.add(funcionario);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Ocorreu um erro ao carregar os funcionario da base de dados");
            e.printStackTrace();
        }

        return funcionarios;
    }


    public Item guardar_item(Item item){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Item (nome, preco, id_categoria) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getNome());
            ps.setDouble(2, item.getPreco());
            ps.setInt(3, item.getIdCategoria());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("A criação do item falhou, nenhuma linha foi afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("A criação do item falhou, nenhum ID foi obtido.");
                }
            }
            ps.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao guardar o item");
            e.printStackTrace();
        }
        return item;
    }

    public void guardar_pedido(){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Pedido (descricao) VALUES (?)");

            // Definir os valores dos parametros
            ps.setString(1, Pedido.getDescricao());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void guardar_zona(Zona zona) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Zonas (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, zona.getNome());
            stmt.executeUpdate();

            // Obtém o ID gerado automaticamente após a inserção
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id_zona = generatedKeys.getInt(1);
                zona.setId(id_zona);
            }

            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao guardar a zona");
            e.printStackTrace();
        }
    }

    public void guardar_mesa(Mesa mesa){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Mesa (status, id_zona) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, mesa.getStatus());
            ps.setInt(2, mesa.getId_zona());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("A criação da mesa falhou, nenhuma linha foi afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mesa.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("A criação da mesa falhou, nenhum ID foi obtido.");
                }
            }
            ps.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro a guardar a mesa");
            System.out.println("Ocorreu um erro ao guardar a mesa: " + e.getMessage());
        }
    }


    /*public void guardar_pagamento(){
        try {
            String query = "INSERT INTO PagamentoAtendimento (data_hora, preco_total, tipo_pagamento, id_mesa) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, Pagamento.getDataHora());
            ps.setDouble(2, Pagamento.getPrecoTotal());
            ps.setString(3, Pagamento.getTipoPagamento());
            //ps.setInt(4, Mesa.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public Categoria guardar_categoria(Categoria categoria) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Categoria (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, categoria.getNome());
            int affectedRows = ps.executeUpdate();


            if (affectedRows == 0) {
                throw new SQLException("A criação da Categoria falhou, nenhuma linha foi afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("A criação da categoria falhou, nenhum ID foi obtido.");
                }
            }

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar a categoria");
        }
        return categoria;
    }



    public Funcionario guardar_funcionario(Funcionario funcionario){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Funcionario (nome, password, id_cargo) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getPassword());
            ps.setInt(3, funcionario.getIdCargo());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("A criação do funcionário falhou, nenhuma linha foi afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    funcionario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("A criação do funcionário falhou, nenhum ID foi obtido.");
                }
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu a guardar o funcionario");
        }
        return funcionario;
    }


    public void guardar_cargo(String descricaoCargo){
        try {
            String query = "INSERT INTO Cargo (descricao) VALUES (?)";
            PreparedStatement ps = connection.prepareStatement(query);

            //ps.setString(1, Cargo.getDescricao());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void editar_item(Item item){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Item SET nome = ?, preco = ?, id_categoria = ? WHERE id_item = ?");
            ps.setString(1, item.getNome());
            ps.setDouble(2, item.getPreco());
            ps.setInt(3, item.getIdCategoria());
            ps.setInt(4, item.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar o item");
        }
    }

    public void editar_pedido(){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Pedido SET descricao = ? WHERE id_pedido = ?");

            // Definir os valores dos parametros
            ps.setString(1, Pedido.getDescricao());
            ps.setInt(2, Pedido.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


   /* public void editar_pagamento(){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE PagamentoAtendimento SET data_hora = ?, preco_total = ?, tipo_pagamento = ?, id_mesa = ? WHERE id_pagatendimento = ?");
            ps.setString(1, Pagamento.getDataHora());
            ps.setDouble(2, Pagamento.getPrecoTotal());
            ps.setString(3, Pagamento.getTipoPagamento());
            //ps.setInt(4, Mesa.getId());
            ps.setInt(5, Pagamento.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public void editar_categoria(Categoria categoria) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Categoria SET nome = ? WHERE id_categoria = ?");

            ps.setString(1, categoria.getNome());
            ps.setInt(2, categoria.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar a categoria");
        }
    }

    public void editar_funcionario(Funcionario funcionario) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Funcionario SET nome = ?, password = ?, id_cargo = ?, id_cartao = ? WHERE id_funcionario = ?");
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getPassword());
            ps.setInt(3, funcionario.getIdCargo());
            ps.setString(4, funcionario.getIdCartao());
            ps.setInt(5, funcionario.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar o funcionario");
        }
    }




    public void editar_mesa(Mesa mesa){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Mesa SET id_zona = ? WHERE id_mesa = ?");
            ps.setInt(1, mesa.getId_zona());
            ps.setInt(2, mesa.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao editar mesa.");
        }
    }


    public void editar_cargo(){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Cargo SET descricao = ? WHERE id_cargo = ?");
            //ps.setString(1, Cargo.getDescricao());
            //ps.setInt(2, Cargo.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remover_item(Item item){
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Item WHERE id_item = ?");
            ps.setInt(1, item.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao Remover os item");
        }
    }

    public void remover_pedido() {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Pedido WHERE id_pedido = ?");

            // Definir o valor do parametro
            ps.setInt(1, Pedido.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao Remover pedido da base de dados.");
            e.printStackTrace();
        }
    }

    public void remover_mesa(Mesa mesa){
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Mesa WHERE id_mesa = ?");
            ps.setInt(1, mesa.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao remover a mesa da base de dados.");
        }
    }


    public void remover_categoria(Categoria categoria) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Categoria WHERE id_categoria = ?");
            ps.setInt(1, categoria.getId());
            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Categoria excluída com sucesso!");
            } else {
                System.out.println("Nenhuma categoria encontrada para exclusão com o ID fornecido.");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Erro ao excluir categoria: " + e.getMessage());
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao Remover os categoria da base de dados.");
            e.printStackTrace();
        }
    }



    public void remover_funcionario(Funcionario funcionario){
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Funcionario WHERE id_funcionario = ?");
            ps.setInt(1, funcionario.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro SQL", "Ocorreu um erro ao Remover os funcionarios da base de dados.");
        }
    }

    public static String getConsultaDetalhesPedidos() {
        return "SELECT pr.id_pedido, pr.descricao AS descricao," +
                "GROUP_CONCAT(i.nome) AS nomes_itens, " +
                "GROUP_CONCAT(pd.quantidade) AS quantidades_itens, " +
                "m.mesas_associadas AS numeros_mesa, " +
                "(SELECT data_hora " +
                " FROM PedidoDetalhe " +
                " WHERE id_pedido = pr.id_pedido " +
                " ORDER BY data_hora LIMIT 1) AS hora_pedido_criado " +
                "FROM PedidoDetalhe pd " +
                "INNER JOIN Pedido_Restaurante pr ON pd.id_pedido = pr.id_pedido " +
                "INNER JOIN Item i ON pd.id_item = i.id_item " +
                "INNER JOIN PagamentoAtendimento pa ON pr.id_pagamento = pa.id_pagatendimento " +
                "INNER JOIN ( " +
                "   SELECT pr.id_pedido, GROUP_CONCAT(pa.id_mesa) AS mesas_associadas " +
                "   FROM Pedido_Restaurante pr " +
                "   INNER JOIN PagamentoAtendimento pa ON pr.id_pagamento = pa.id_pagatendimento " +
                "   GROUP BY pr.id_pedido " +
                ") m ON pr.id_pedido = m.id_pedido " +
                "WHERE pr.status = 'Por Fazer' or pr.status = 'Cancelado'" +
                "GROUP BY pr.id_pedido";
    }

    public static String getConsultaPedidosProntos(int minutos) {
        return "SELECT pr.id_pedido, pr.descricao AS descricao, " +
                "GROUP_CONCAT(i.nome) AS nomes_itens, " +
                "GROUP_CONCAT(pd.quantidade) AS quantidades_itens, " +
                "m.mesas_associadas AS numeros_mesa, " +
                "(SELECT data_hora " +
                " FROM PedidoDetalhe " +
                " WHERE id_pedido = pr.id_pedido " +
                " ORDER BY data_hora LIMIT 1) AS hora_pedido_criado " +
                "FROM PedidoDetalhe pd " +
                "INNER JOIN Pedido_Restaurante pr ON pd.id_pedido = pr.id_pedido " +
                "INNER JOIN Item i ON pd.id_item = i.id_item " +
                "INNER JOIN PagamentoAtendimento pa ON pr.id_pagamento = pa.id_pagatendimento " +
                "INNER JOIN ( " +
                "   SELECT pr.id_pedido, GROUP_CONCAT(pa.id_mesa) AS mesas_associadas " +
                "   FROM Pedido_Restaurante pr " +
                "   INNER JOIN PagamentoAtendimento pa ON pr.id_pagamento = pa.id_pagatendimento " +
                "   GROUP BY pr.id_pedido " +
                ") m ON pr.id_pedido = m.id_pedido " +
                "WHERE pr.status = 'Pronto' AND " +
                "      pd.data_hora >= NOW() - INTERVAL "+minutos+" MINUTE " +
                "GROUP BY pr.id_pedido";
    }

    private void mostrarAlerta(String titulo, String cabecalho) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.showAndWait();
    }


}
