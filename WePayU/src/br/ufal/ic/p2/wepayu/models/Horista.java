package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.specifics.Cartao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Horista extends Empregado {
    private double horasNormaisTrabalhadas;
    private double horasExtrasTrabalhadas;
    private List<Cartao> cartoesDePonto = new ArrayList<>();
    public Horista(String id, String nome, String endereco, String tipo, double salario) throws EmpregadoNaoExisteException {
        super(id, nome, endereco, tipo, salario);
        this.horasNormaisTrabalhadas = 0;
        this.horasExtrasTrabalhadas = 0;
    }

    public Horista(){}

    public Horista(Horista horista){
        super(horista);
        this.horasNormaisTrabalhadas = horista.horasNormaisTrabalhadas;
        this.horasExtrasTrabalhadas = horista.horasExtrasTrabalhadas;
        this.cartoesDePonto = horista.getCartoesDePonto();
    }

    public List<Cartao> getCartoesDePonto() {
        return cartoesDePonto;
    }

    public double[] getHorasNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        double horasNormais = 0;
        double horasExtras = 0;

        for (Cartao cartao : this.getCartoesDePonto()) {
            LocalDate dataCartao = cartao.getDataNaoString();
            if (!dataCartao.isBefore(dataInicial) && !dataCartao.isAfter(dataFinal)) {
                double horasTrabalhadas = cartao.getHorasTrabalhadas();
                if (horasTrabalhadas > 8) {
                    horasNormais += 8;
                    horasExtras += horasTrabalhadas - 8;
                } else {
                    horasNormais += horasTrabalhadas;
                }
            }
        }
        return new double[]{horasNormais, horasExtras};
    }

    public void setCartoesDePonto(List<Cartao> cartoesDePonto) {
        this.cartoesDePonto = cartoesDePonto;
    }

    public void lancaCartao(Cartao cartao) {
        cartoesDePonto.add(cartao);
    }

    public void removerCartao(Cartao cartao) {
        cartoesDePonto.remove(cartao);
    }

    public double getHorasNormaisTrabalhadas() {
        return horasNormaisTrabalhadas;
    }

    public void setHorasNormaisTrabalhadas(double horasNormais) {
        this.horasNormaisTrabalhadas = horasNormais;
    }

    public double getHorasExtrasTrabalhadas() {
        return horasExtrasTrabalhadas;
    }

    public void setHorasExtrasTrabalhadas(double horasExtras) {
        this.horasExtrasTrabalhadas = horasExtras;
    }
}
