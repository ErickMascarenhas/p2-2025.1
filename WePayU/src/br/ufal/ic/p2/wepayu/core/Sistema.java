package br.ufal.ic.p2.wepayu.core;

import br.ufal.ic.p2.wepayu.Exception.DesfazerNadaException;
import br.ufal.ic.p2.wepayu.Exception.RefazerNadaException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Sindicato;
import br.ufal.ic.p2.wepayu.services.EmpregadoServices;
import br.ufal.ic.p2.wepayu.services.FolhaDePagamentoServices;
import br.ufal.ic.p2.wepayu.utils.Comando;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Sistema {
    private boolean sistemaEncerrado = false;
    private final EmpregadoServices empregadoServices = new EmpregadoServices();
    private final FolhaDePagamentoServices folhaDePagamentoServices = new FolhaDePagamentoServices(empregadoServices);
    private final String dados = "bancoDeDados.xml";
    public static Stack<Comando> undo = new Stack<>();
    public static Stack<Comando> redo = new Stack<>();
    public static List<String> agendasPagamento = new ArrayList<>() {{add("semanal 5"); add("semanal 2 5"); add("mensal $");}};

    public Sistema() throws Exception {
        carregar();
    }

    @SuppressWarnings("unchecked")
    private void carregar() throws Exception {
        File arquivo = new File(dados);
        if (!arquivo.exists()) {
            empregadoServices.setEmpregados(new HashMap<>());
            return;
        }
        FileInputStream input = new FileInputStream(arquivo);
        XMLDecoder decodificador = new XMLDecoder(input);
        empregadoServices.setEmpregados((HashMap<String, Empregado>) decodificador.readObject());
        decodificador.close();
        input.close();
    }

    private void salvar() throws Exception {
        FileOutputStream output = new FileOutputStream(dados);
        XMLEncoder codificador = new XMLEncoder(output);
        codificador.writeObject(empregadoServices.getEmpregados());
        codificador.close();
        output.close();
    }

    public void encerrarSistema() throws Exception {
        salvar();
        this.sistemaEncerrado = true;
    }

    public boolean isSistemaEncerrado() {
        return this.sistemaEncerrado;
    }

    public void zerarSistema() {
        HashMap<String, Empregado> backup = new HashMap<>(empregadoServices.getEmpregados());
        List<String> backupSindicato = new ArrayList<>(Sindicato.getIdsSindicato()), backupAgendas = new ArrayList<>(agendasPagamento);

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregadoServices.zerarEmpregados();
                agendasPagamento.clear();
                agendasPagamento.add("semanal 5");
                agendasPagamento.add("semanal 2 5");
                agendasPagamento.add("mensal $");
                Sindicato.zerarSindicato();
            }
            @Override
            public void desfazer() {
                empregadoServices.setEmpregados(backup);
                setAgendasPagamento(backupAgendas);
                Sindicato.setIdsSindicato(backupSindicato);
            }
        };

        c.executar();
        undo.push(c);
        redo.clear();
        this.sistemaEncerrado = false;

        File arquivo = new File(dados);
        if (arquivo.exists()) arquivo.delete();
    }

    public EmpregadoServices getEmpregadoServices() {
        return empregadoServices;
    }

    public FolhaDePagamentoServices getFolhaDePagamentoServices() {
        return folhaDePagamentoServices;
    }

    public List<String> getAgendasPagamento() {
        return agendasPagamento;
    }

    public void setAgendasPagamento(List<String> agendasPagamento) {
        this.agendasPagamento = agendasPagamento;
    }

    public void undo() throws Exception {
        if(undo.isEmpty()){
            throw new DesfazerNadaException();
        }

        Comando c = undo.pop();
        c.desfazer();
        redo.push(c);
    }
    public void redo() throws Exception{
        if(redo.isEmpty()){
            throw new RefazerNadaException();
        }

        Comando c = redo.pop();
        c.executar();
        undo.push(c);
    }
}
