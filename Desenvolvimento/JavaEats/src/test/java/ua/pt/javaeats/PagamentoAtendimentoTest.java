package ua.pt.javaeats;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoAtendimentoTest {

    @Test
    void getId() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setId(1L);
        assertEquals(1L, pagamento.getId());
    }

    @Test
    void setId() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setId(1L);
        assertEquals(1L, pagamento.getId());
    }

    @Test
    void getDataHoraInicio() {
        Timestamp dataHoraInicio = new Timestamp(System.currentTimeMillis());
        PagamentoAtendimento pagamento = new PagamentoAtendimento(dataHoraInicio, null, 50.0, "Cartão", 1);
        assertEquals(dataHoraInicio, pagamento.getDataHoraInicio());
    }

    @Test
    void setDataHoraInicio() {
        Timestamp dataHoraInicio = new Timestamp(System.currentTimeMillis());
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setDataHoraInicio(dataHoraInicio);
        assertEquals(dataHoraInicio, pagamento.getDataHoraInicio());
    }

    @Test
    void getDataHoraFim() {
        Timestamp dataHoraFim = new Timestamp(System.currentTimeMillis());
        PagamentoAtendimento pagamento = new PagamentoAtendimento(null, dataHoraFim, 50.0, "Cartão", 1);
        assertEquals(dataHoraFim, pagamento.getDataHoraFim());
    }

    @Test
    void setDataHoraFim() {
        Timestamp dataHoraFim = new Timestamp(System.currentTimeMillis());
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setDataHoraFim(dataHoraFim);
        assertEquals(dataHoraFim, pagamento.getDataHoraFim());
    }

    @Test
    void getPrecoTotal() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento(null, null, 50.0, "Cartão", 1);
        assertEquals(50.0, pagamento.getPrecoTotal());
    }

    @Test
    void setPrecoTotal() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setPrecoTotal(50.0);
        assertEquals(50.0, pagamento.getPrecoTotal());
    }

    @Test
    void getTipoPagamento() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento(null, null, 50.0, "Cartão", 1);
        assertEquals("Cartão", pagamento.getTipoPagamento());
    }

    @Test
    void setTipoPagamento() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setTipoPagamento("Dinheiro");
        assertEquals("Dinheiro", pagamento.getTipoPagamento());
    }

    @Test
    void getIdMesa() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento(null, null, 50.0, "Cartão", 1);
        assertEquals(1, pagamento.getIdMesa());
    }

    @Test
    void setIdMesa() {
        PagamentoAtendimento pagamento = new PagamentoAtendimento();
        pagamento.setIdMesa(2);
        assertEquals(2, pagamento.getIdMesa());
    }
}