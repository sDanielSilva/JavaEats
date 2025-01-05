package ua.pt.javaeats;

public class Zona {

    private String nome;
    private int id;

    public Zona(int idZona, String nome) {
        this.id = idZona;
        this.nome = nome;
    }


    public Zona(String novoNome) {
        this.nome = novoNome;
    }


    @Override
    public String toString() {
        return nome;
    }

    public Zona(){

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
