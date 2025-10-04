package br.ufal.ic.p2.wepayu.Exception;

public class NomeNaoEncontradoException extends Exception {
    public NomeNaoEncontradoException() {
        super("Nao ha empregado com esse nome.");
    }
}
