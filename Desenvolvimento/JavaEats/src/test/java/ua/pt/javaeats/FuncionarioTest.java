package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    @Test
    void getNome() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        assertEquals("João", funcionario.getNome());
    }

    @Test
    void setNome() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        funcionario.setNome("Maria");
        assertEquals("Maria", funcionario.getNome());
    }

    @Test
    void getPassword() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        assertEquals("password", funcionario.getPassword());
    }

    @Test
    void setPassword() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        funcionario.setPassword("novapassword");
        assertEquals("novapassword", funcionario.getPassword());
    }

    @Test
    void getId() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        assertEquals(1, funcionario.getId());
    }

    @Test
    void setId() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        funcionario.setId(2);
        assertEquals(2, funcionario.getId());
    }

    @Test
    void getIdCargo() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        assertEquals(2, funcionario.getIdCargo());
    }

    @Test
    void setIdCargo() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        funcionario.setIdCargo(3);
        assertEquals(3, funcionario.getIdCargo());
    }

    @Test
    void getIdCartao() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        assertEquals("12345", funcionario.getIdCartao());
    }

    @Test
    void setIdCartao() {
        Funcionario funcionario = new Funcionario(1, "João", "password", 2, "12345", null);
        funcionario.setIdCartao("54321");
        assertEquals("54321", funcionario.getIdCartao());
    }
}