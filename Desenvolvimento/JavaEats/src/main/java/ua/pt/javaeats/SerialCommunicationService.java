package ua.pt.javaeats;

import com.fazecast.jSerialComm.SerialPort;

public class SerialCommunicationService {

    private static SerialPort portaSerial;
    private static String ultimoUID = null;

    public static SerialPort getPortaSerial() {
        return portaSerial;
    }

    public static void setPortaSerial(SerialPort port) {
        portaSerial = port;
    }

    public static void verificarFuncionarioPorUID(String uid) {
        System.out.println("UID: " + uid);
    }

    public static String getUltimoUID() {
        return ultimoUID;
    }

    public static void setUltimoUID(String uid) {
        ultimoUID = uid;
    }
}
