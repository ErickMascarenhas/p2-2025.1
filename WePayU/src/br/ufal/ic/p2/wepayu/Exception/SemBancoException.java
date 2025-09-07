package br.ufal.ic.p2.wepayu.Exception;

public class SemBancoException extends Exception {
    public SemBancoException(){
        super("Empregado nao recebe em banco.");
    }
}
