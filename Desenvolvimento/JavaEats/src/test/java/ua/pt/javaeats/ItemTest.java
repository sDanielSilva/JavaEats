package ua.pt.javaeats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void getPreco() {
        Item item = new Item(1, "lasanha", 10.99, 1, null);
        assertEquals(10.99, item.getPreco(), 0.01);
    }

    @Test
    void setPreco() {
        Item item = new Item(1, "lasanha", 10.99, 1, null);
        item.setPreco(15.99);
        assertEquals(15.99, item.getPreco(), 0.01);
    }

    @Test
    void getNome() {
        Item item = new Item(1, "lasanha", 10.99, 1, null);
        assertEquals("lasanha", item.getNome());
    }

    @Test
    void setNome() {
        Item item = new Item(1, "lasanha", 10.99, 1, null);
        item.setNome("Feijoada");
        assertEquals("Feijoada", item.getNome());
    }

    @Test
    void getId() {
        Item item = new Item(1, "Feijoada", 10.99, 1, null);
        assertEquals(1, item.getId());
    }

    @Test
    void setId() {
        Item item = new Item(1, "Feijoada", 10.99, 1, null);
        item.setId(2);
        assertEquals(2, item.getId());
    }

    @Test
    void getIdCategoria() {
        Item item = new Item(1, "Feijoada", 10.99, 1, null);
        assertEquals(1, item.getIdCategoria());
    }

    @Test
    void setIdCategoria() {
        Item item = new Item(1, "Feijoada", 10.99, 1, null);
        item.setIdCategoria(2);
        assertEquals(2, item.getIdCategoria());
    }
}