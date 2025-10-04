package br.ufal.ic.p2.wepayu.Exception;

public class AgendaPagamentoIndisponivelException extends Exception {
    public AgendaPagamentoIndisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}
