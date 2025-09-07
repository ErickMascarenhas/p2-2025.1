package br.ufal.ic.p2.wepayu.Exception;

public class DataDuracaoInvalidaException extends Exception {
    public DataDuracaoInvalidaException(){
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
