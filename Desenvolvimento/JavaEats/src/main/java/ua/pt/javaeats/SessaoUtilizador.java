package ua.pt.javaeats;

public final class SessaoUtilizador {

    private static SessaoUtilizador instance;

    private int idUtilizador;
    private int cargo;
    private final String nomeFuncionario;

    SessaoUtilizador(int idUtilizador, int cargo, String nomeFuncionario) {
        this.idUtilizador = idUtilizador;
        this.cargo = cargo;
        this.nomeFuncionario = nomeFuncionario;
    }

    public static SessaoUtilizador getInstance(int idUtilizador, int cargo, String nomeFuncionario) {
        if (instance == null) {
            instance = new SessaoUtilizador(idUtilizador, cargo, nomeFuncionario);
        }
        return instance;
    }

    public static SessaoUtilizador getInstance() {
        return instance;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public int getCargo() {
        return cargo;
    }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public static void setInstance(SessaoUtilizador instance) {
        SessaoUtilizador.instance = instance;
    }

    public void terminarSessaoUtilizador(){
        idUtilizador = 0;
        cargo = 0;
        SessaoUtilizador.instance = null;
    }

    @Override
    public String toString() {
        return "SessaoUtilizador{" +
                "idUtilizador=" + idUtilizador +
                ", cargo=" + cargo +
                '}';
    }
}
