package br.ufal.ic.p2.wepayu.specifics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cartao {
    private String data;
    private double horasTrabalhadas;

    public Cartao(LocalDate data, double horasTrabalhadas) {
        this.data = data.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.horasTrabalhadas = horasTrabalhadas;
    }

    public Cartao() {
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

    public double getHorasTrabalhadas() {
        return horasTrabalhadas;
    }

    public void setHorasTrabalhadas(double horasTrabalhadas) {
        this.horasTrabalhadas = horasTrabalhadas;
    }
}
