package ua.pt.javaeats;
public class Pedido {

    private static int id;

    public static void setId(int novoId) {
        id = novoId;
    }
    private static String descricao;

    public void Pedido() {
        this.id = id;
        this.descricao = descricao;
    }

    public static void setDescricao(String novaDescricao) {
        descricao = novaDescricao;
    }
    public static String getDescricao() {
        return descricao;
    }

    public static int getId(){
        return id;
    }
}
