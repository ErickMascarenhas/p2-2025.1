package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.specifics.Cartao;
import br.ufal.ic.p2.wepayu.specifics.Venda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Comissionado extends Empregado {
    private double comissao;
    private double vendasRealizadas;
    private List<Venda> vendas = new ArrayList<>();
    public Comissionado(String id, String nome, String endereco, String tipo, double salario, double comissao) throws EmpregadoNaoExisteException {
        super(id, nome, endereco, tipo, salario);
        this.comissao = comissao;
        this.vendasRealizadas = 0.00;
    }

    public Comissionado(){}

    public Comissionado(Comissionado comissionado){
        super(comissionado);
        this.comissao = comissionado.comissao;
        this.vendasRealizadas = comissionado.vendasRealizadas;
        this.vendas = comissionado.vendas;
    }

    public void lancaVenda(Venda venda){
        vendas.add(venda);
    }

    public void removerVenda(Venda venda){
        vendas.remove(venda);
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public double getVendasNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        double totalVendas = 0;
        for (Venda venda : this.getVendas()) {
            LocalDate dataVenda = venda.getDataNaoString();
            if (!dataVenda.isBefore(dataInicial) && !dataVenda.isAfter(dataFinal)) {
                totalVendas += venda.getValor();
            }
        }
        return totalVendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    public double getVendasRealizadas() {
        return vendasRealizadas;
    }

    public void setVendasRealizadas(double vendasRealizadas) {
        this.vendasRealizadas = vendasRealizadas;
    }
}
