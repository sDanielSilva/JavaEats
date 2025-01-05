package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void testToString() {
        Categoria categoria = new Categoria(1, "Comida");
        assertEquals("Comida", categoria.toString());
    }

    @Test
    void getNome() {
        Categoria categoria = new Categoria(1, "Comida");
        assertEquals("Comida", categoria.getNome());
    }

    @Test
    void setNome() {
        Categoria categoria = new Categoria(1, "Comida");
        categoria.setNome("Bebida");
        assertEquals("Bebida", categoria.getNome());
    }

    @Test
    void getId() {
        Categoria categoria = new Categoria(1, "Comida");
        assertEquals(1, categoria.getId());
    }

    @Test
    void setId() {
        Categoria categoria = new Categoria(1, "Comida");
        categoria.setId(2);
        assertEquals(2, categoria.getId());
    }
}