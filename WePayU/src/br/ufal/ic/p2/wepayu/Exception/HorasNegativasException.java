package br.ufal.ic.p2.wepayu.Exception;

public class HorasNegativasException extends Exception {
    public HorasNegativasException(){
        super("Horas devem ser positivas.");
    }
}
