package br.ufal.ic.p2.wepayu.Exception;

public class AgendaPagamentoNulaException extends Exception {
    public AgendaPagamentoNulaException() {
        super("Agenda de pagamento nao pode ser nula");
    }
}
