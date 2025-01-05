package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZonaTest {

    @Test
    void testToString() {
        //Cria uma instância da classe Zona com id 1 e nome "Esplanada"
        Zona zona = new Zona(1, "Esplanada");

        //Verifica se o método toString retorna o nome esperado
        assertEquals("Esplanada", zona.toString());
    }

    @Test
    void getNome() {
        //Cria uma instância da classe Zona com id 1 e nome "Esplanada"
        Zona zona = new Zona(1, "Esplanada");

        //Verifica se o método getNome retorna o nome esperado
        assertEquals("Esplanada", zona.getNome());
    }

    @Test
    void setNome() {
        //Cria uma instância da classe Zona com id 1 e nome "Esplanada"
        Zona zona = new Zona(1, "Esplanada");

        //Chama o método setNome para alterar o nome
        zona.setNome("Salão");

        //Verifica se o método getNome retorna o novo nome esperado
        assertEquals("Salão", zona.getNome());
    }

    @Test
    void getId() {
        //Cria uma instância da classe Zona com id 1 e nome "Esplanada"
        Zona zona = new Zona(1, "Esplanada");

        //Verifica se o método getId retorna o id esperado
        assertEquals(1, zona.getId());
    }

    @Test
    void setId() {
        //Cria uma instância da classe Zona
        Zona zona = new Zona();

        //Chama o método setId para definir o id
        zona.setId(2);

        //Verifica se o método getId retorna o novo id esperado
        assertEquals(2, zona.getId());
    }
}