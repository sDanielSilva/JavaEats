package ua.pt.javaeats;

import java.sql.Connection;

public class Funcionario {

    private String idCartao;
    private String nome;
    private String password;
    private int id;
    private int idCargo;
    private Connection conexao;

    public Funcionario() {

    }

    public Funcionario(int id, String nome, String password, int idCargo, String idCartao, Connection conexao) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.idCargo = idCargo;
        this.idCartao = idCartao;
        this.conexao = conexao;
    }

    public Funcionario(int id, String nome, String password, int idCargo) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.idCargo = idCargo;
    }

    public Funcionario(String nome, String password, int idCargo, String idCartao, Connection conexao) {
        this.nome = nome;
        this.password = password;
        this.idCargo = idCargo;
        this.idCartao = idCartao;
        this.conexao = conexao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getIdCartao() {return idCartao;}

    public void setIdCartao(String idCartao) {this.idCartao = idCartao;}
}
