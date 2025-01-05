package ua.pt.javaeats;

public class Cargo {
    private String descricao;
    private int idCargo;

    public Cargo() {
    }

    public Cargo(String descricao, int idCargo) {
        this.descricao = descricao;
        this.idCargo = idCargo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Cargo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public void exibirInformacoesCargo() {
        System.out.println("ID do cargo: " + idCargo);
        System.out.println("Descrição do cargo: " + descricao);
    }

}