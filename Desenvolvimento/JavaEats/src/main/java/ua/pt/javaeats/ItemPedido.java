package ua.pt.javaeats;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ItemPedido {
    private final SimpleStringProperty produto = new SimpleStringProperty("");
    private final SimpleIntegerProperty quantidade = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty quantidadeOriginal = new SimpleIntegerProperty(0);
    private final SimpleDoubleProperty preco = new SimpleDoubleProperty(0.0);
    private final SimpleIntegerProperty idItem = new SimpleIntegerProperty(0);

    public String getProduto() {
        return produto.get();
    }

    public void setProduto(String produto) {
        this.produto.set(produto);
    }

    public SimpleStringProperty produtoProperty() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade.get();
    }

    public void setQuantidade(int quantidade) {
        this.quantidade.set(quantidade);
    }

    public SimpleIntegerProperty quantidadeProperty() {
        return quantidade;
    }

    // Adicionando propriedade e métodos para quantidade original
    public int getQuantidadeOriginal() {
        return quantidadeOriginal.get();
    }

    public void setQuantidadeOriginal(int quantidadeOriginal) {
        this.quantidadeOriginal.set(quantidadeOriginal);
    }

    public double getPreco() {
        return preco.get();
    }

    public void setPreco(double preco) {
        this.preco.set(preco);
    }

    public SimpleDoubleProperty precoProperty() {
        return preco;
    }

    public int getIdItem() {
        return idItem.get();
    }

    public void setIdItem(int idItem) {
        this.idItem.set(idItem);
    }

    public SimpleIntegerProperty idItemProperty() {
        return idItem;
    }
}

