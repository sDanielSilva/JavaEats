package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MesaTest {

    @Test
    void editarStatus() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa(1, "Disponível");

        //Chama o método editarStatus para alterar o status
        mesa.editarStatus("Ocupada");

        //Verifica se o status foi alterado corretamente
        assertEquals("Ocupada", mesa.getStatus());
    }

    @Test
    void getStatus() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa(2, "Reservada");

        //Verifica se o método getStatus retorna o status esperado
        assertEquals("Reservada", mesa.getStatus());
    }

    @Test
    void getId() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa(3, "Disponível");

        //Verifica se o método getId retorna o id esperado
        assertEquals(3, mesa.getId());
    }

    @Test
    void setStatus() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa(4, "Disponível");

        //Chama o método setStatus para alterar o status
        mesa.setStatus("Reservada");

        //Verifica se o status foi alterado corretamente
        assertEquals("Reservada", mesa.getStatus());
    }

    @Test
    void setId() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa();

        //Chama o método setId para definir o id
        mesa.setId(5);

        //Verifica se o método getId retorna o id definido
        assertEquals(5, mesa.getId());
    }

    @Test
    void getId_zona() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa(6, "Disponível", 1);

        //Verifica se o método getId_zona retorna o id_zona esperado
        assertEquals(1, mesa.getId_zona());
    }

    @Test
    void setId_zona() {
        //Cria uma instância de Mesa
        Mesa mesa = new Mesa();

        //Chama o método setId_zona para definir o id_zona
        mesa.setId_zona(2);

        //Verifica se o método getId_zona retorna o id_zona definido
        assertEquals(2, mesa.getId_zona());
    }
}