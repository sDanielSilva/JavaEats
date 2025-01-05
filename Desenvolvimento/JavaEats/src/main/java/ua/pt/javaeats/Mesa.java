package ua.pt.javaeats;

public class Mesa {

    private String status;
    private int id;

    private int id_zona;

    public Mesa(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public Mesa(String status) {
        this.status = status;
    }

    public Mesa() {

    }

    public Mesa(int id, String status, int idZona) {
        this.id = id;
        this.status = status;
        this.id_zona = idZona;
    }

    public void editarStatus(String novoStatus) {
        this.status = novoStatus;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_zona() {
        return id_zona;
    }

    public void setId_zona(int id_zona) {
        this.id_zona = id_zona;
    }
}
