package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest {

    @Test
    void testToString() {
        Cargo cargo = new Cargo("Gerente");
        assertEquals("Gerente", cargo.toString());
    }

    @Test
    void getDescricao() {
        Cargo cargo = new Cargo("Gerente");
        assertEquals("Gerente", cargo.getDescricao());
    }

    @Test
    void getIdCargo() {
        Cargo cargo = new Cargo("Gerente", 1);
        assertEquals(1, cargo.getIdCargo());
    }

    @Test
    void setDescricao() {
        Cargo cargo = new Cargo("Gerente");
        cargo.setDescricao("Gerente do Restaurante");
        assertEquals("Gerente do Restaurante", cargo.getDescricao());
    }

    @Test
    void setIdCargo() {
        Cargo cargo = new Cargo("Gerente");
        cargo.setIdCargo(2);
        assertEquals(2, cargo.getIdCargo());
    }

    @Test
    void exibirInformacoesCargo() {
        Cargo cargo = new Cargo("Gerente", 1);
        //Este teste verifica se a exibição de informações não lança exceções
        assertDoesNotThrow(() -> cargo.exibirInformacoesCargo());
    }
}