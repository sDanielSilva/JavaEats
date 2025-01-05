package ua.pt.javaeats;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    @Test
    void getProduto() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setProduto("Produto");
        assertEquals("Produto", itemPedido.getProduto());
    }

    @Test
    void setProduto() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setProduto("Produto");
        assertEquals("Produto", itemPedido.produtoProperty().get());
    }

    @Test
    void produtoProperty() {
        ItemPedido itemPedido = new ItemPedido();
        SimpleStringProperty property = itemPedido.produtoProperty();
        assertNotNull(property);
    }

    @Test
    void getQuantidade() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(5);
        assertEquals(5, itemPedido.getQuantidade());
    }

    @Test
    void setQuantidade() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(5);
        assertEquals(5, itemPedido.quantidadeProperty().get());
    }

    @Test
    void quantidadeProperty() {
        ItemPedido itemPedido = new ItemPedido();
        SimpleIntegerProperty property = itemPedido.quantidadeProperty();
        assertNotNull(property);
    }

    @Test
    void getQuantidadeOriginal() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidadeOriginal(3);
        assertEquals(3, itemPedido.getQuantidadeOriginal());
    }

    @Test
    void setQuantidadeOriginal() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidadeOriginal(3);
        assertEquals(3, itemPedido.getQuantidadeOriginal());
    }

    @Test
    void getPreco() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setPreco(10.5);
        assertEquals(10.5, itemPedido.getPreco());
    }

    @Test
    void setPreco() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setPreco(10.5);
        assertEquals(10.5, itemPedido.precoProperty().get());
    }

    @Test
    void precoProperty() {
        ItemPedido itemPedido = new ItemPedido();
        SimpleDoubleProperty property = itemPedido.precoProperty();
        assertNotNull(property);
    }

    @Test
    void getIdItem() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setIdItem(1);
        assertEquals(1, itemPedido.getIdItem());
    }

    @Test
    void setIdItem() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setIdItem(1);
        assertEquals(1, itemPedido.idItemProperty().get());
    }
}
