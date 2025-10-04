package br.ufal.ic.p2.wepayu.Exception;

public class AgendaPagamentoOcupadaException extends Exception {
    public AgendaPagamentoOcupadaException() {
        super("Agenda de pagamentos ja existe");
    }
}
