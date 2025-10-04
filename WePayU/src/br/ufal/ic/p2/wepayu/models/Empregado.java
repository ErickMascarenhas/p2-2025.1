package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.specifics.TaxaServico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private double salario;
    private boolean sindicalizado;
    private String metodoPagamento;
    private String banco;
    private String agencia;
    private String contaCorrente;
    private String idSindicato;
    private double taxaSindical;
    private double taxaServico;
    private double debitoSindicato = 0;
    private List<TaxaServico> taxasServicos = new ArrayList<>();
    private String agendaPagamento;

    public Empregado(String id, String nome, String endereco, String tipo, double salario) throws EmpregadoNaoExisteException {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicalizado = false;
        this.metodoPagamento = "emMaos";
        this.banco = null;
        this.agencia = null;
        this.contaCorrente = null;
        this.idSindicato = null;
        this.taxaSindical = 0.00;
        this.taxaServico = 0.00;
        this.agendaPagamento = null;
    }

    public Empregado(Empregado empregado){
        this.id = empregado.id;
        this.nome = empregado.nome;
        this.endereco = empregado.endereco;
        this.tipo = empregado.tipo;
        this.salario = empregado.salario;
        this.sindicalizado = empregado.sindicalizado;
        this.metodoPagamento = empregado.metodoPagamento;
        this.banco = empregado.banco;
        this.agencia = empregado.agencia;
        this.contaCorrente = empregado.contaCorrente;
        this.idSindicato = empregado.idSindicato;
        this.taxaSindical = empregado.taxaSindical;
        this.taxaServico = empregado.taxaServico;
        this.taxasServicos = empregado.taxasServicos;
        this.agendaPagamento = empregado.agendaPagamento;
    }

    public Empregado(){}

    public double getTaxaServico() {
        return taxaServico;
    }

    public void setTaxaServico(double taxaServico) {
        this.taxaServico = taxaServico;
    }

    public List<TaxaServico> getTaxasServicos() {
        return taxasServicos;
    }

    public double getTaxasServicoNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        double totalTaxas = 0;
        if (this.isSindicalizado()) {
            for (TaxaServico taxa : this.getTaxasServicos()) {
                LocalDate dataTaxa = taxa.getDataNaoString();
                if (!dataTaxa.isBefore(dataInicial) && !dataTaxa.isAfter(dataFinal)) totalTaxas += taxa.getValor();
            }
        }
        return totalTaxas;
    }

    public void setTaxasServicos(List<TaxaServico> taxasServicos) {
        this.taxasServicos = taxasServicos;
    }

    public void lancaTaxaServico(TaxaServico taxaServico){
        taxasServicos.add(taxaServico);
    }

    public void removerTaxaServico(TaxaServico taxaServico){
        taxasServicos.remove(taxaServico);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public double getSalario() {
        return salario;
    }

    public boolean isSindicalizado() {
        return sindicalizado;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public String getIdSindicato() {
        return idSindicato;
    }

    public void setIdSindicato(String idSindicato) {
        this.idSindicato = idSindicato;
    }

    public double getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    public double getDebitoSindicato() {
        return debitoSindicato;
    }

    public void setDebitoSindicato(double debitoSindicato) {
        this.debitoSindicato = debitoSindicato;
    }

    public String getAgendaPagamento() {
        return agendaPagamento;
    }

    public void setAgendaPagamento(String agendaPagamento) {
        this.agendaPagamento = agendaPagamento;
    }
}
