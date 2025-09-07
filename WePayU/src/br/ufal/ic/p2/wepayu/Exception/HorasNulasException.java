package br.ufal.ic.p2.wepayu.Exception;

public class HorasNulasException extends Exception {
    public HorasNulasException(){
        super("Horas nao podem ser nulas.");
    }
}
