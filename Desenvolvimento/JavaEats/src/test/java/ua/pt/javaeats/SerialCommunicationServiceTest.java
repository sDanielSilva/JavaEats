package ua.pt.javaeats;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerialCommunicationServiceTest {

    @Test
    void getPortaSerial() {
        //Verifica se o método getPortaSerial retorna a porta serial correta
        SerialPort port = SerialCommunicationService.getPortaSerial();
        assertNull(port); //A princípio, a porta serial deve ser nula
    }

    @Test
    void setPortaSerial() {
        //Cria uma porta serial para teste
        SerialPort port = SerialPort.getCommPort("COM2");

        //Chama o método setPortaSerial para definir a porta serial
        SerialCommunicationService.setPortaSerial(port);

        //Verifica se o método getPortaSerial retorna a porta serial definida
        assertEquals(port, SerialCommunicationService.getPortaSerial());
    }

    @Test
    void verificarFuncionarioPorUID() {
        //Imprime para a consola o UID lido
        SerialCommunicationService.verificarFuncionarioPorUID("123456789");
    }

    @Test
    void getUltimoUID() {
        //Verifica se o método getUltimoUID retorna o último UID definido
        SerialCommunicationService.setUltimoUID("123456789");
        assertEquals("123456789", SerialCommunicationService.getUltimoUID());
    }

    @Test
    void setUltimoUID() {
        //Chama o método setUltimoUID para definir o último UID
        SerialCommunicationService.setUltimoUID("987654321");

        //Verifica se o método getUltimoUID retorna o último UID definido
        assertEquals("987654321", SerialCommunicationService.getUltimoUID());
    }
}
