package br.ufal.ic.p2.wepayu.specifics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Venda {
    private String data;
    private double valor;

    public Venda(LocalDate data, double valor) {
        this.data = data.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.valor = valor;
    }

    public Venda() {
    }

    public String getData() {
        return data;
    }

    public LocalDate getDataNaoString() {
        if (this.data == null || this.data.isEmpty()) return null;
        return LocalDate.parse(this.data);
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
