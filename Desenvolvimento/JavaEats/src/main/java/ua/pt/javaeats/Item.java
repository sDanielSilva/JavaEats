package ua.pt.javaeats;

import java.sql.Connection;

public class Item {

    private double preco;
    private String nome;
    private String area_preparo;
    private int id;
    private int idCategoria;

    private Connection conexao;

    public Item(int id, String nome, double preco, int idCategoria, Connection conexao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.idCategoria = idCategoria;
        this.conexao = conexao;
    }


    public Item(int id, String nome, double preco, int idCategoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.idCategoria = idCategoria;
    }


    public Item(String nome, double preco, int idCategoria, Connection conexao) {
        this.nome = nome;
        this.preco = preco;
        this.idCategoria = idCategoria;
        this.conexao = conexao;
    }


    public double getPreco() {
        return Double.parseDouble(String.valueOf(preco));
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Connection getConexao() {
        return conexao;
    }

    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }
}