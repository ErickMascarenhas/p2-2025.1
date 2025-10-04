package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoEmUsoException extends Exception {
    public IdSindicatoEmUsoException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
