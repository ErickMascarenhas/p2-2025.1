package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.core.Sistema;
import br.ufal.ic.p2.wepayu.services.EmpregadoServices;
import br.ufal.ic.p2.wepayu.services.FolhaDePagamentoServices;

import java.time.LocalDate;

public class Facade {
    private final Sistema sistema;
    private final EmpregadoServices empregadoServices;
    private final FolhaDePagamentoServices folhaDePagamentoServices;

    public Facade() throws Exception {
        Sistema.undo.clear();
        Sistema.redo.clear();
        this.sistema = new Sistema();
        this.empregadoServices = sistema.getEmpregadoServices();
        this.folhaDePagamentoServices = sistema.getFolhaDePagamentoServices();
    }

    private void checarSistema() throws Exception {
        if (sistema.isSistemaEncerrado()) {
            throw new SistemaEncerradoException();
        }
    }

    public int getNumeroDeEmpregados() throws Exception {
        checarSistema();
        return empregadoServices.getNumeroDeEmpregados();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        checarSistema();
        return empregadoServices.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        checarSistema();
        return empregadoServices.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public void removerEmpregado(String id) throws Exception {
        checarSistema();
        empregadoServices.removerEmpregado(id);
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        checarSistema();
        return empregadoServices.getAtributoEmpregado(id, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        checarSistema();
        return empregadoServices.getEmpregadoPorNome(nome, indice);
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws  Exception {
        checarSistema();
        empregadoServices.alteraEmpregado(id, atributo, valor);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String comissao) throws  Exception {
        checarSistema();
        empregadoServices.alteraEmpregado(id, atributo, valor, comissao);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws  Exception {
        checarSistema();
        empregadoServices.alteraEmpregado(id, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws  Exception {
        checarSistema();
        empregadoServices.alteraEmpregado(id, atributo, valor, banco, agencia, contaCorrente);
    }

    public void lancaCartao(String id, String data, String horas) throws Exception{
        checarSistema();
        empregadoServices.lancaCartao(id, data, horas);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return empregadoServices.getHorasNormaisTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return empregadoServices.getHorasExtrasTrabalhadas(id, dataInicial, dataFinal);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception {
        checarSistema();
        empregadoServices.lancaVenda(id, data, valor);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return empregadoServices.getVendasRealizadas(id, dataInicial, dataFinal);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        checarSistema();
        empregadoServices.lancaTaxaServico(membro, data, valor);
    }

    public String getTaxasServico(String membro, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return empregadoServices.getTaxasServico(membro, dataInicial, dataFinal);
    }

    public String totalFolha(String data) throws Exception {
        checarSistema();
        return folhaDePagamentoServices.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        checarSistema();
        folhaDePagamentoServices.rodaFolha(data, saida);
    }

    public void undo() throws Exception {
        checarSistema();
        sistema.undo();
    }

    public void redo() throws Exception {
        checarSistema();
        sistema.redo();
    }

    public void zerarSistema() {
        sistema.zerarSistema();
    }

    public void encerrarSistema() throws Exception {
        sistema.encerrarSistema();
    }
}