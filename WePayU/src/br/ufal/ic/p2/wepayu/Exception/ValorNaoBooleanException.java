package br.ufal.ic.p2.wepayu.Exception;

public class ValorNaoBooleanException extends Exception {
    public ValorNaoBooleanException(){
        super("Valor deve ser true ou false.");
    }
}
