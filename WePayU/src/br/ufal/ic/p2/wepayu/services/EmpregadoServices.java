package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.specifics.*;
import br.ufal.ic.p2.wepayu.utils.*;
import br.ufal.ic.p2.wepayu.core.Sistema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class EmpregadoServices {
    private HashMap<String, Empregado> empregados = new HashMap<>();

    public void zerarEmpregados(){
        empregados.clear();
    }

    public HashMap<String, Empregado> getEmpregados() {
        return empregados;
    }

    public void setEmpregados(HashMap<String, Empregado> empregados) {
        this.empregados = empregados;
    }

    public int getNumeroDeEmpregados() {
        return empregados.size();
    }

    int i = 0;
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        if (nome.isEmpty()) throw new NomeNuloException();
        else if (endereco.isEmpty()) throw new EnderecoNuloException();
        else if (!tipo.equals("assalariado") && !tipo.equals("comissionado") && !tipo.equals("horista")) throw new TipoInvalidoException();
        else if (tipo.equals("comissionado")) throw new TipoNaoAplicavelException();
        double salarioDouble = Conversao.converterSalario(salario);

        String id = Conversao.darId(i++);
        Empregado empregado;
        if (tipo.equals("assalariado")){
            empregado = new Assalariado(id, nome, endereco, tipo, salarioDouble);
            empregado.setAgendaPagamento("mensal $");
        }
        else{
            empregado = new Horista(id, nome, endereco, tipo, salarioDouble);
            empregado.setAgendaPagamento("semanal 5");
        }


        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.remove(id);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();

        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if (nome.isEmpty()) throw new NomeNuloException();
        else if (endereco.isEmpty()) throw new EnderecoNuloException();
        else if (!tipo.equals("assalariado") && !tipo.equals("comissionado") && !tipo.equals("horista")) throw new TipoInvalidoException();
        else if (!tipo.equals("comissionado")) throw new TipoNaoAplicavelException();
        double salarioDouble = Conversao.converterSalario(salario);
        double comissaoDouble = Conversao.converterComissao(comissao);

        String id = Conversao.darId(i++);
        Empregado empregado = new Comissionado(id, nome, endereco, tipo, salarioDouble, comissaoDouble);
        empregado.setAgendaPagamento("semanal 2 5");

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.remove(id);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();

        return id;
    }

    public void removerEmpregado(String id) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado backup = empregados.get(id);
        if (backup == null) throw new EmpregadoNaoExisteException();

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.remove(id);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        Comissionado comissionado = new Comissionado();
        if (emp.getTipo().equals("comissionado")) comissionado = converterComissionado(id, ((Comissionado) emp).getComissao(), emp);
        return switch (atributo) {
            case "id" -> emp.getId();
            case "nome" -> emp.getNome();
            case "endereco" -> emp.getEndereco();
            case "tipo" -> emp.getTipo();
            case "salario" -> String.format("%.2f", emp.getSalario());
            case "comissao" -> {
                if (emp.getTipo().equals("comissionado")) yield String.format("%.2f", comissionado.getComissao());
                else throw new EmpregadoNaoComissionadoException();
            }
            case "sindicalizado" -> Boolean.toString(emp.isSindicalizado());
            case "metodoPagamento" -> emp.getMetodoPagamento();
            case "banco" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new SemBancoException();
                yield emp.getBanco();
            }
            case "agencia" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new SemBancoException();
                yield emp.getAgencia();
            }
            case "contaCorrente" -> {
                if (!emp.getMetodoPagamento().equals("banco")) throw new SemBancoException();
                yield emp.getContaCorrente();
            }
            case "idSindicato" -> {
                if (emp.isSindicalizado()) yield emp.getIdSindicato();
                else throw new EmpregadoNaoSindicalizadoException();
            }
            case "taxaSindical" -> {
                if (emp.isSindicalizado()) yield String.format("%.2f", emp.getTaxaSindical());
                else throw new EmpregadoNaoSindicalizadoException();
            }
            case "agendaPagamento" -> emp.getAgendaPagamento();
            default -> throw new AtributoNaoExisteException();
        };
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        List<Empregado> nomesEmpregados = new ArrayList<>();
        for (Empregado empregado : empregados.values()) if (empregado.getNome().equals(nome)) nomesEmpregados.add(empregado);
        if (nomesEmpregados.isEmpty()) throw new NomeNaoEncontradoException();
        nomesEmpregados.sort(Comparator.comparing(Empregado::getId));
        return nomesEmpregados.get(indice - 1).getId();
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws  Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        switch (atributo) {
            case "nome" -> {
                if (valor == null || valor.isEmpty()) throw new NomeNuloException();
                else emp.setNome(valor);
            }
            case "endereco" -> {
                if (valor == null || valor.isEmpty()) throw new EnderecoNuloException();
                else emp.setEndereco(valor);
            }
            case "tipo" -> {
                if (emp.getTipo().equals(valor)) return;
                switch (valor) {
                    case "comissionado" -> throw new ComissaoNulaException();
                    case "horista" -> {
                        Horista horista = converterHorista(id, emp);
                        empregados.put(id, horista);
                    }
                    case "assalariado" -> {
                        Assalariado assalariado = converterAssalariado(id, emp);
                        empregados.put(id, assalariado);
                    }
                    default -> throw new TipoInvalidoException();
                }
            }
            case "salario" -> {
                double salario = Conversao.converterSalario(valor);
                emp.setSalario(salario);
            }
            case "comissao" -> {
                if (emp.getTipo().equals("comissionado")){
                    double comissao = Conversao.converterComissao(valor);
                    assert emp instanceof Comissionado;
                    ((Comissionado) emp).setComissao(comissao);
                }
                else throw new EmpregadoNaoComissionadoException();
            }
            case "metodoPagamento" -> {
                if (valor.equals("emMaos") || valor.equals("banco") || valor.equals("correios")) emp.setMetodoPagamento(valor);
                else throw new MetodoPagamentoInvalidoException();
            }
            case "banco" -> {
                if (valor != null) emp.setBanco(valor);
                else throw new BancoNuloException();
            }
            case "agencia" -> {
                if (valor != null) emp.setAgencia(valor);
                else throw new BancoNuloException();
            }
            case "contaCorrente" -> {
                if (valor != null) emp.setContaCorrente(valor);
                else throw new ContaCorrenteNulaException();
            }
            case "sindicalizado" -> {
                if (valor.equals("false") || valor.equals("true")) emp.setSindicalizado(Boolean.parseBoolean(valor));
                else throw new ValorNaoBooleanException();
            }
            case "idSindicato" -> {
                if (valor != null){
                    if (Sindicato.inSindicato(valor)) throw new IdSindicatoEmUsoException();
                    else{
                        Sindicato.removeIdSindicato(emp.getIdSindicato());
                        emp.setIdSindicato(valor);
                        Sindicato.addIdSindicato(valor);
                    }
                }
                else throw new IdSindicatoNuloException();
            }
            case "taxaSindical" -> {
                double taxaSindical = Conversao.converterTaxaSindical(valor);
                emp.setTaxaSindical(taxaSindical);
            }
            case "agendaPagamento" -> {
                if (valor != null) {
                    if (Sistema.agendasPagamento.contains(valor)) emp.setAgendaPagamento(valor);
                    else throw new AgendaPagamentoIndisponivelException();
                    }
                else throw new AgendaPagamentoNulaException();
            }
            default -> throw new AtributoNaoExisteException();
        }
        Empregado empregado = empregados.get(id);

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String comissao) throws  Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (!atributo.equals("tipo") || !(valor.equals("horista") || valor.equals("comissionado"))) throw new AtributoNaoExisteException();


        if (valor.equals("horista")){
            double salarioDobule = Conversao.converterSalario(comissao);
            Horista horista = converterHorista(id, emp);
            horista.setSalario(salarioDobule);
            empregados.put(id, horista);
        }
        else{
            double comissaoDouble = Conversao.converterComissao(comissao);
            Comissionado comissionado = converterComissionado(id, comissaoDouble, emp);
            empregados.put(id, comissionado);
        }

        Empregado empregado = empregados.get(id);
        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, empregado);
            }
            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws  Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (atributo.equals("sindicalizado") && valor.equals("true")){
            if (idSindicato.isEmpty()) throw new IdSindicatoNuloException();
            double taxaSindicalDouble = Conversao.converterTaxaSindical(taxaSindical);
            if (Sindicato.inSindicato(idSindicato)) throw new IdSindicatoEmUsoException();
            emp.setSindicalizado(true);
            emp.setIdSindicato(idSindicato);
            emp.setTaxaSindical(taxaSindicalDouble);
        }
        else throw new AtributoNaoExisteException();

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, emp);
                Sindicato.addIdSindicato(idSindicato);
            }

            @Override
            public void desfazer() {
                empregados.put(id, backup);
                Sindicato.removeIdSindicato(idSindicato);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws  Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        Empregado backup;
        if (emp instanceof Horista) backup = new Horista((Horista) emp);
        else if (emp instanceof Comissionado) backup = new Comissionado((Comissionado) emp);
        else backup = new Assalariado((Assalariado) emp);
        if (atributo.equals("metodoPagamento") && valor.equals("banco")){
            if (banco.isEmpty()) throw new BancoNuloException();
            if (agencia.isEmpty()) throw new AgenciaNulaException();
            if (contaCorrente.isEmpty()) throw new ContaCorrenteNulaException();
            emp.setMetodoPagamento("banco");
            emp.setBanco(banco);
            emp.setAgencia(agencia);
            emp.setContaCorrente(contaCorrente);
        }
        else throw new AtributoNaoExisteException();

        Comando c = new Comando() {
            @Override
            public void executar() {
                empregados.put(id, emp);
            }

            @Override
            public void desfazer() {
                empregados.put(id, backup);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public void lancaCartao(String id, String data, String horas) throws Exception{
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        if(!(emp instanceof Horista)) throw new EmpregadoNaoHoristaException();
        LocalDate dataT = Conversao.converterData(data);
        double horasT = Conversao.receberHora(horas);
        Cartao cartao = new Cartao(dataT, horasT);

        Comando c = new Comando() {
            @Override
            public void executar() {
                ((Horista) emp).lancaCartao(cartao);
            }
            @Override
            public void desfazer() {
                ((Horista) emp).removerCartao(cartao);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        if(!(emp instanceof Horista)) throw new EmpregadoNaoHoristaException();
        LocalDate dataI = Conversao.converterDataInicial(dataInicial);
        LocalDate dataF = Conversao.converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DataDuracaoInvalidaException();
        double horasNormais = 0;
        for(Cartao cartao : ((Horista) emp).getCartoesDePonto()) if(((cartao.getDataNaoString().isAfter(dataI) || cartao.getDataNaoString().isEqual(dataI)) && cartao.getDataNaoString().isBefore(dataF)) && cartao.getHorasTrabalhadas() >= 8) horasNormais += 8;
        return Conversao.converterHora(horasNormais);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        if(!(emp instanceof Horista)) throw new EmpregadoNaoHoristaException();
        LocalDate dataI = Conversao.converterDataInicial(dataInicial);
        LocalDate dataF = Conversao.converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DataDuracaoInvalidaException();
        double horasExtras = 0;
        for(Cartao cartao : ((Horista) emp).getCartoesDePonto()) if(((cartao.getDataNaoString().isAfter(dataI) || cartao.getDataNaoString().isEqual(dataI)) && cartao.getDataNaoString().isBefore(dataF)) && cartao.getHorasTrabalhadas() > 8) horasExtras += cartao.getHorasTrabalhadas() - 8;
        return Conversao.converterHora(horasExtras);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        if(!(emp instanceof Comissionado)) throw new EmpregadoNaoComissionadoException();
        LocalDate dataT = Conversao.converterData(data);
        double valorT = Conversao.receberValor(valor);
        Venda venda = new Venda(dataT, valorT);

        Comando c = new Comando() {
            @Override
            public void executar() {
                ((Comissionado) emp).lancaVenda(venda);
            }
            @Override
            public void desfazer() {
                ((Comissionado) emp).removerVenda(venda);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        if (id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado emp = empregados.get(id);
        if (emp == null) throw new EmpregadoNaoExisteException();
        if(!(emp instanceof Comissionado)) throw new EmpregadoNaoComissionadoException();
        LocalDate dataI = Conversao.converterDataInicial(dataInicial);
        LocalDate dataF = Conversao.converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DataDuracaoInvalidaException();
        double valorTotal = 0;
        for(Venda venda : ((Comissionado) emp).getVendas()) if(((venda.getDataNaoString().isAfter(dataI) || venda.getDataNaoString().isEqual(dataI)) && venda.getDataNaoString().isBefore(dataF))) valorTotal += venda.getValor();
        return String.format("%.2f", valorTotal);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        if (membro.isEmpty()) throw new IdentificacaoMembroNulaException();
        Empregado emp = null;
        for (Empregado empregado : empregados.values()) if (empregado.getIdSindicato() != null && empregado.getIdSindicato().equals(membro)) { emp = empregado; break; }
        if (emp == null) throw new MembroNaoExisteException();
        if(!emp.isSindicalizado()) throw new EmpregadoNaoSindicalizadoException();
        LocalDate dataT = Conversao.converterData(data);
        double valorT = Conversao.receberValor(valor);
        TaxaServico taxaServico = new TaxaServico(dataT, valorT);

        Empregado finalEmp = emp;
        Comando c = new Comando() {
            @Override
            public void executar() {
                finalEmp.lancaTaxaServico(taxaServico);
            }
            @Override
            public void desfazer() {
                finalEmp.removerTaxaServico(taxaServico);
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }

    public String getTaxasServico(String membro, String dataInicial, String dataFinal) throws Exception {
        if (membro.isEmpty()) throw new IdentificacaoMembroNulaException();
        Empregado emp = empregados.get(membro);
        if (emp == null) throw new MembroNaoExisteException();
        if(!emp.isSindicalizado()) throw new EmpregadoNaoSindicalizadoException();
        LocalDate dataI = Conversao.converterDataInicial(dataInicial);
        LocalDate dataF = Conversao.converterDataFinal(dataFinal);
        if(dataI.isAfter(dataF)) throw new DataDuracaoInvalidaException();
        double valorTotal = 0;
        for(TaxaServico taxaServico : emp.getTaxasServicos()) if(((taxaServico.getDataNaoString().isAfter(dataI) || taxaServico.getDataNaoString().isEqual(dataI)) && taxaServico.getDataNaoString().isBefore(dataF))) valorTotal += taxaServico.getValor();
        return String.format("%.2f", valorTotal);
    }

    public void criarAgendaDePagamentos(String descricao) throws Exception {
        if (Sistema.agendasPagamento.contains(descricao)) throw new AgendaPagamentoOcupadaException();
        else if (descricao.contains("semanal") || descricao.contains("mensal")){
            String[] agenda = descricao.split(" ");
            if (agenda[0].equals("semanal")) {
                if (agenda.length == 2 && Integer.parseInt(agenda[1]) >= 1 && Integer.parseInt(agenda[1]) <= 7) Sistema.agendasPagamento.add(descricao);
                else if (agenda.length == 3 && Integer.parseInt(agenda[1]) >= 1 && Integer.parseInt(agenda[1]) <= 52 && Integer.parseInt(agenda[2]) >= 1 && Integer.parseInt(agenda[2]) <= 7) Sistema.agendasPagamento.add(descricao);
                else throw new AgendaPagamentoInvalidaException();
            }
            else if (agenda.length == 2 && ((Integer.parseInt(agenda[1]) >= 1 && Integer.parseInt(agenda[1]) <= 28) || agenda[1].equals("$"))) Sistema.agendasPagamento.add(descricao);
            else throw new AgendaPagamentoInvalidaException();
        }
        else throw new AgendaPagamentoInvalidaException();
    }

    private static Horista converterHorista(String id, Empregado emp) throws EmpregadoNaoExisteException {
        Horista empregado = new Horista(id, emp.getNome(), emp.getEndereco(), "horista", emp.getSalario());
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }

    private static Comissionado converterComissionado(String id, double comissao, Empregado emp) throws EmpregadoNaoExisteException {
        Comissionado empregado = new Comissionado(id, emp.getNome(), emp.getEndereco(), "comissionado", emp.getSalario(), comissao);
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }

    private static Assalariado converterAssalariado(String id, Empregado emp) throws EmpregadoNaoExisteException {
        Assalariado empregado = new Assalariado(id, emp.getNome(), emp.getEndereco(), "assalariado", emp.getSalario());
        empregado.setMetodoPagamento(emp.getMetodoPagamento());
        empregado.setBanco(emp.getBanco());
        empregado.setAgencia(emp.getAgencia());
        empregado.setContaCorrente(emp.getContaCorrente());
        empregado.setSindicalizado(emp.isSindicalizado());
        empregado.setIdSindicato(emp.getIdSindicato());
        empregado.setTaxaSindical(emp.getTaxaSindical());
        return empregado;
    }
}
