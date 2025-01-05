package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void setId() {
        //Define um novo ID usando o método setId
        Pedido.setId(1);
        //Verifica se o ID foi definido corretamente
        assertEquals(1, Pedido.getId());
    }

    @Test
    void pedido() {
        //Ele apenas chama o método setId e setDescricao após a criação da instância.
        Pedido pedido = new Pedido();
        // Define um ID e uma descrição após a criação da instância
        Pedido.setId(2);
        Pedido.setDescricao("Teste");
        //Verifica se o ID e a descrição foram configurados corretamente
        assertEquals(2, Pedido.getId());
        assertEquals("Teste", Pedido.getDescricao());
    }

    @Test
    void setDescricao() {
        //Define uma nova descrição usando o método setDescricao
        Pedido.setDescricao("Sem picante");
        //Verifica se a descrição foi definida corretamente
        assertEquals("Sem picante", Pedido.getDescricao());
    }

    @Test
    void getDescricao() {
        //Define uma nova descrição usando o método setDescricao
        Pedido.setDescricao("Com picante");
        //Verifica se o método getDescricao retorna a descrição correta
        assertEquals("Com picante", Pedido.getDescricao());
    }

    @Test
    void getId() {
        //Define um novo ID usando o método setId
        Pedido.setId(3);
        //Verifica se o método getId retorna o ID corretamente
        assertEquals(3, Pedido.getId());
    }
}
