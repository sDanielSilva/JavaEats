package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessaoUtilizadorTest {

    @Test
    void getInstance() {
        //Testa se a instância retornada não é nula
        assertNotNull(SessaoUtilizador.getInstance());
    }

    @Test
    void testGetInstance() {
        //Cria uma instância da classe SessaoUtilizador
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Testa se a instância retornada é a mesma que foi criada
        assertEquals(instance, SessaoUtilizador.getInstance());
    }

    @Test
    void getIdUtilizador() {
        //Cria uma instância da classe SessaoUtilizador com idUtilizador igual a 1
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Testa se o método getIdUtilizador retorna o valor esperado
        assertEquals(1, instance.getIdUtilizador());
    }

    @Test
    void getCargo() {
        //Cria uma instância da classe SessaoUtilizador com cargo igual a 2
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Testa se o método getCargo retorna o valor esperado
        assertEquals(2, instance.getCargo());
    }

    @Test
    void getNomeFuncionario() {
        //Cria uma instância da classe SessaoUtilizador com nomeFuncionario igual a "Funcionário"
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Testa se o método getNomeFuncionario retorna o valor esperado
        assertEquals("Funcionário", instance.getNomeFuncionario());
    }

    @Test
    void setInstance() {
        //Cria uma instância da classe SessaoUtilizador
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Cria uma nova instância
        SessaoUtilizador newInstance = SessaoUtilizador.getInstance(3, 4, "Novo Funcionário");

        //Chama o método setInstance para definir uma nova instância
        SessaoUtilizador.setInstance(newInstance);

        //Testa se o método getInstance retorna a nova instância definida
        assertEquals(newInstance, SessaoUtilizador.getInstance());
    }

    @Test
    void terminarSessaoUtilizador() {
        //Cria uma instância da classe SessaoUtilizador
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Chama o método terminarSessaoUtilizador para limpar os dados da sessão
        instance.terminarSessaoUtilizador();

        //Testa se os valores foram resetados para os valores padrão
        assertEquals(0, instance.getIdUtilizador());
        assertEquals(0, instance.getCargo());
        assertNull(SessaoUtilizador.getInstance());
    }

    @Test
    void testToString() {
        //Cria uma instância da classe SessaoUtilizador
        SessaoUtilizador instance = SessaoUtilizador.getInstance(1, 2, "Funcionário");

        //Testa se o método toString retorna a representação esperada
        assertEquals("SessaoUtilizador{idUtilizador=1, cargo=2}", instance.toString());
    }
}