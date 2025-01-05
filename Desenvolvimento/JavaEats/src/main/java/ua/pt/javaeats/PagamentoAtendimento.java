package ua.pt.javaeats;

import java.sql.Timestamp;

public class PagamentoAtendimento {
    private Long id; // Seu identificador único, autoincrementável
    private Timestamp dataHoraInicio;
    private Timestamp dataHoraFim;
    private Double precoTotal;
    private String tipoPagamento;
    private int idMesa;

    // Construtores, getters e setters

    public PagamentoAtendimento() {
        // Construtor padrão
    }

    public PagamentoAtendimento(Timestamp dataHoraInicio, Timestamp dataHoraFim, Double precoTotal, String tipoPagamento, int idMesa) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.precoTotal = precoTotal;
        this.tipoPagamento = tipoPagamento;
        this.idMesa = idMesa;
    }

    // Getters e setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Timestamp dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Timestamp getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Timestamp dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public Double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(Double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }
}