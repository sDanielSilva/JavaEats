package ua.pt.javaeats;

public class Categoria {
    private String nome;
    private int id;


    public Categoria(){

    }

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    @Override
    public String toString() {
        return nome;
    }

    public Categoria(String nome) {
        this.nome = nome;
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
}
