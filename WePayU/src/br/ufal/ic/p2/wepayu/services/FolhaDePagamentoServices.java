package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.core.Sistema;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.specifics.*;
import br.ufal.ic.p2.wepayu.utils.*;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.time.temporal.ChronoUnit;

public class FolhaDePagamentoServices {
    private final EmpregadoServices empregadoServices;

    public FolhaDePagamentoServices(EmpregadoServices empregadoServices) {
        this.empregadoServices = empregadoServices;
    }

    private boolean isDiaDePagamento(Empregado empregado, LocalDate dataFolha) {
        String[] agenda = empregado.getAgendaPagamento().split(" ");
        String tipoAgenda = agenda[0];
        if (tipoAgenda.equals("mensal")) {
            if (agenda[1].equals("$")) return dataFolha.isEqual(dataFolha.with(TemporalAdjusters.lastDayOfMonth()));
            else {
                int diaDoMes = Integer.parseInt(agenda[1]);
                return dataFolha.getDayOfMonth() == diaDoMes;
            }
        }
        else if (tipoAgenda.equals("semanal")) {
            int diaDaSemanaAlvo = Integer.parseInt(agenda[agenda.length - 1]);
            int diaDaSemanaAtual = dataFolha.getDayOfWeek().getValue();
            if (diaDaSemanaAtual != diaDaSemanaAlvo) return false;
            if (agenda.length == 2) return true;
            else {
                int semanas = Integer.parseInt(agenda[1]);
                LocalDate primeiroDiaDoAno = dataFolha.with(TemporalAdjusters.firstDayOfYear());
                LocalDate primeiroDiaDaSemanaAlvo = primeiroDiaDoAno.with(TemporalAdjusters.nextOrSame(dataFolha.getDayOfWeek()));
                long diasDeDiferenca = ChronoUnit.DAYS.between(primeiroDiaDaSemanaAlvo, dataFolha);
                long numeroDaOcorrencia = (diasDeDiferenca / 7) + 1;
                return numeroDaOcorrencia % semanas == 0;
            }
        }
        return false;
    }

    private boolean isAntigo(HashMap<String, Empregado> empregados) {
        if (Sistema.agendasPagamento.size() > 3) return false;
        for (Empregado empregado : empregados.values()) {
            String agendaAtual = empregado.getAgendaPagamento();
            switch (empregado) {
                case Horista horista when !agendaAtual.equals("semanal 5") -> {
                    return false;
                }
                case Assalariado assalariado when !agendaAtual.equals("mensal $") -> {
                    return false;
                }
                case Comissionado comissionado when !agendaAtual.equals("semanal 2 5") -> {
                    return false;
                }
                default -> {}
            }
        }
        return true;
    }

    public String totalFolha(String data) throws Exception {
        HashMap<String, Empregado> empregados = empregadoServices.getEmpregados();
        LocalDate dataFolha = Conversao.converterData(data);
        if (isAntigo(empregados)) return totalFolhaAntiga(dataFolha, empregados);
        else return totalFolhaNova(dataFolha, empregados);
    }

    private String totalFolhaNova(LocalDate dataFolha, HashMap<String, Empregado> empregados) {
        BigDecimal totalFolha = BigDecimal.ZERO;
        for (Empregado empregado : empregados.values()) {
            if (isDiaDePagamento(empregado, dataFolha)) {
                String[] agenda = empregado.getAgendaPagamento().split(" ");
                LocalDate dataInicial;
                BigDecimal pagamentoPeriodo = BigDecimal.ZERO;
                if (agenda[0].equals("mensal")) dataInicial = dataFolha.with(TemporalAdjusters.firstDayOfMonth());
                else {
                    int semanas = (agenda.length == 3) ? Integer.parseInt(agenda[1]) : 1;
                    dataInicial = dataFolha.minusWeeks(semanas).plusDays(1);
                }

                switch (empregado) {
                    case Horista horista -> {
                        double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                        BigDecimal salarioHora = BigDecimal.valueOf(horista.getSalario());
                        BigDecimal horasNormais = BigDecimal.valueOf(horas[0]);
                        BigDecimal horasExtras = BigDecimal.valueOf(horas[1]);
                        BigDecimal pagamentoHorasNormais = horasNormais.multiply(salarioHora);
                        BigDecimal pagamentoHorasExtras = horasExtras.multiply(salarioHora).multiply(new BigDecimal("1.5"));
                        pagamentoPeriodo = pagamentoHorasNormais.add(pagamentoHorasExtras);
                    }
                    case Assalariado assalariado -> {
                        BigDecimal salarioMensal = BigDecimal.valueOf(assalariado.getSalario());
                        if (agenda[0].equals("mensal")) pagamentoPeriodo = salarioMensal;
                        else {
                            int semanas = (agenda.length == 3) ? Integer.parseInt(agenda[1]) : 1;
                            pagamentoPeriodo = salarioMensal.multiply(new BigDecimal("12")).multiply(new BigDecimal(semanas)).divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
                        }
                    }
                    case Comissionado comissionado -> {
                        BigDecimal salarioMensal = BigDecimal.valueOf(comissionado.getSalario());
                        BigDecimal pagamentoFixo;
                        if (agenda[0].equals("mensal")) pagamentoFixo = salarioMensal;
                        else {
                            int semanas = (agenda.length == 3) ? Integer.parseInt(agenda[1]) : 1;
                            pagamentoFixo = salarioMensal.multiply(new BigDecimal("12")).multiply(new BigDecimal(semanas)).divide(new BigDecimal("52"), 2, RoundingMode.DOWN);
                        }
                        double totalVendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                        BigDecimal vendasBD = BigDecimal.valueOf(totalVendas);
                        BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                        BigDecimal comissaoValor = vendasBD.multiply(comissaoTaxaBD);
                        pagamentoPeriodo = pagamentoFixo.add(comissaoValor);
                    }
                    default -> {}
                }
                totalFolha = totalFolha.add(pagamentoPeriodo);
            }
        }
        return String.format("%.2f", totalFolha).replace('.', ',');
    }

    private String totalFolhaAntiga(LocalDate dataFolha, HashMap<String, Empregado> empregados) {
        double totalFolha = 0;
        for (Empregado empregado : empregados.values()) {
            if (empregado instanceof Horista horista && dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate dataInicial = dataFolha.minusDays(6);
                double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                totalFolha += horas[0] * empregado.getSalario() + horas[1] * empregado.getSalario() * 1.5;
            }
            else if (empregado instanceof Assalariado && Conversao.isUltimoDiaUtil(dataFolha)) totalFolha += empregado.getSalario();
            else if (empregado instanceof Comissionado comissionado && Conversao.isDiaDePagamentoComissionado(dataFolha)) {
                LocalDate dataInicial = dataFolha.minusDays(13);
                double totalVendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                BigDecimal salarioBD = BigDecimal.valueOf(empregado.getSalario());
                BigDecimal salarioFixoBD = salarioBD.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
                BigDecimal totalVendasBD = BigDecimal.valueOf(totalVendas);
                BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                BigDecimal comissaoBD = totalVendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                totalFolha += salarioFixoBD.add(comissaoBD).doubleValue();
            }
        }
        return String.format("%.2f", totalFolha).replace('.', ',');
    }

    public void rodaFolha(String data, String saida) throws Exception {
        LocalDate dataFolha = Conversao.converterData(data);
        List<Empregado> empregadosOrdenados = new ArrayList<>(empregadoServices.getEmpregados().values());
        empregadosOrdenados.sort(Comparator.comparing(Empregado::getNome));
        HashMap<String, List<Cartao>> cartoesPagos = new HashMap<>();
        HashMap<String, List<Venda>> vendasPagas = new HashMap<>();
        StringBuilder relatorio = new StringBuilder();
        double totalFolhaGeral = 0;

        StringBuilder folhaHoristas = new StringBuilder();
        folhaHoristas.append("===============================================================================================================================\n");
        folhaHoristas.append("===================== HORISTAS ================================================================================================\n");
        folhaHoristas.append("===============================================================================================================================\n");
        folhaHoristas.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaHoristas.append("==================================== ===== ===== ============= ========= =============== ======================================\n");
        double totalBrutoHoristas = 0, totalDescontosHoristas = 0, totalLiquidoHoristas = 0;
        int totalHorasNormais = 0, totalHorasExtras = 0;

        StringBuilder folhaAssalariados = new StringBuilder();
        folhaAssalariados.append("===============================================================================================================================\n");
        folhaAssalariados.append("===================== ASSALARIADOS ============================================================================================\n");
        folhaAssalariados.append("===============================================================================================================================\n");
        folhaAssalariados.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaAssalariados.append("================================================ ============= ========= =============== ======================================\n");
        double totalBrutoAssalariados = 0, totalDescontosAssalariados = 0, totalLiquidoAssalariados = 0;

        StringBuilder folhaComissionados = new StringBuilder();
        folhaComissionados.append("===============================================================================================================================\n");
        folhaComissionados.append("===================== COMISSIONADOS ===========================================================================================\n");
        folhaComissionados.append("===============================================================================================================================\n");
        folhaComissionados.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        folhaComissionados.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        double totalFixoComissionados = 0, totalVendasComissionados = 0, totalComissaoComissionados = 0, totalBrutoComissionados = 0, totalDescontosComissionados = 0, totalLiquidoComissionados = 0;

        if (dataFolha.getDayOfWeek() == DayOfWeek.FRIDAY) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Horista horista) {
                    if (horista.isSindicalizado()) horista.setDebitoSindicato(horista.getDebitoSindicato() + horista.getTaxaSindical() * 7);
                    LocalDate dataInicial = dataFolha.minusDays(6);
                    double[] horas = horista.getHorasNoPeriodo(dataInicial, dataFolha);
                    double salarioBruto = horas[0] * horista.getSalario() + horas[1] * horista.getSalario() * 1.5;
                    double descontos = 0, salarioLiquido = 0;
                    if (salarioBruto > 0) {
                        descontos = horista.getTaxasServicoNoPeriodo(dataInicial, dataFolha) + horista.getDebitoSindicato();
                        salarioLiquido = salarioBruto - descontos;
                        if (salarioLiquido < 0) {
                            horista.setDebitoSindicato(descontos - salarioBruto);
                            descontos = salarioBruto;
                            salarioLiquido = 0;
                        }
                        else horista.setDebitoSindicato(0);
                    }
                    String metodoPagamentoStr;
                    switch (horista.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", horista.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", horista.getBanco(), horista.getAgencia(), horista.getContaCorrente());
                    }
                    folhaHoristas.append(String.format("%-36s %5.0f %5.0f %13.2f %9.2f %15.2f %s\n", horista.getNome(), horas[0], horas[1], salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalHorasNormais += (int) horas[0];
                    totalHorasExtras += (int) horas[1];
                    totalBrutoHoristas += salarioBruto;
                    totalDescontosHoristas += descontos;
                    totalLiquidoHoristas += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                    List<Cartao> cartoesDoPeriodo = new ArrayList<>();
                    for(Cartao c : horista.getCartoesDePonto()) if(!c.getDataNaoString().isBefore(dataInicial) && c.getDataNaoString().isBefore(dataFolha.plusDays(1))) cartoesDoPeriodo.add(c);
                    if (!cartoesDoPeriodo.isEmpty()) cartoesPagos.put(horista.getId(), cartoesDoPeriodo);
                }
            }
        }

        if (Conversao.isUltimoDiaUtil(dataFolha)) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Assalariado) {
                    LocalDate dataInicial = dataFolha.with(TemporalAdjusters.firstDayOfMonth());
                    double salarioBruto = empregado.getSalario();
                    double descontos = empregado.getTaxasServicoNoPeriodo(dataInicial, dataFolha);
                    if (empregado.isSindicalizado()) descontos += empregado.getTaxaSindical() * dataFolha.lengthOfMonth();
                    double salarioLiquido = salarioBruto - descontos;
                    String metodoPagamentoStr;
                    switch (empregado.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", empregado.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", empregado.getBanco(), empregado.getAgencia(), empregado.getContaCorrente());
                    }
                    folhaAssalariados.append(String.format("%-48s %13.2f %9.2f %15.2f %s\n", empregado.getNome(), salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalBrutoAssalariados += salarioBruto;
                    totalDescontosAssalariados += descontos;
                    totalLiquidoAssalariados += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                }
            }
        }

        if (Conversao.isDiaDePagamentoComissionado(dataFolha)) {
            for (Empregado empregado : empregadosOrdenados) {
                if (empregado instanceof Comissionado comissionado) {
                    LocalDate dataInicial = dataFolha.minusDays(13);
                    BigDecimal salarioBD = BigDecimal.valueOf(comissionado.getSalario());
                    BigDecimal salarioFixoBD = salarioBD.multiply(new BigDecimal("12")).divide(new BigDecimal("26"), 2, RoundingMode.DOWN);
                    double vendas = comissionado.getVendasNoPeriodo(dataInicial, dataFolha);
                    BigDecimal totalVendasBD = BigDecimal.valueOf(vendas);
                    BigDecimal comissaoTaxaBD = BigDecimal.valueOf(comissionado.getComissao());
                    BigDecimal comissaoBD = totalVendasBD.multiply(comissaoTaxaBD).setScale(2, RoundingMode.DOWN);
                    double salarioFixo = salarioFixoBD.doubleValue();
                    double comissao = comissaoBD.doubleValue();
                    double salarioBruto = salarioFixo + comissao;
                    double descontos = comissionado.getTaxasServicoNoPeriodo(dataInicial, dataFolha);
                    if (comissionado.isSindicalizado()) descontos += comissionado.getTaxaSindical() * 14;
                    double salarioLiquido = salarioBruto - descontos;
                    String metodoPagamentoStr;
                    switch (comissionado.getMetodoPagamento()) {
                        case "emMaos" -> metodoPagamentoStr = "Em maos";
                        case "correios" -> metodoPagamentoStr = String.format("Correios, %s", comissionado.getEndereco());
                        default -> metodoPagamentoStr = String.format("%s, Ag. %s CC %s", comissionado.getBanco(), comissionado.getAgencia(), comissionado.getContaCorrente());
                    }
                    folhaComissionados.append(String.format("%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n", comissionado.getNome(), salarioFixo, vendas, comissao, salarioBruto, descontos, salarioLiquido, metodoPagamentoStr));
                    totalFixoComissionados += salarioFixo;
                    totalVendasComissionados += vendas;
                    totalComissaoComissionados += comissao;
                    totalBrutoComissionados += salarioBruto;
                    totalDescontosComissionados += descontos;
                    totalLiquidoComissionados += salarioLiquido;
                    totalFolhaGeral += salarioBruto;
                    List<Venda> vendasDoPeriodo = new ArrayList<>();
                    for(Venda v : comissionado.getVendas()) if(!v.getDataNaoString().isBefore(dataInicial) && v.getDataNaoString().isBefore(dataFolha.plusDays(1))) vendasDoPeriodo.add(v);
                    if (!vendasDoPeriodo.isEmpty()) vendasPagas.put(comissionado.getId(), vendasDoPeriodo);
                }
            }
        }

        relatorio.append("FOLHA DE PAGAMENTO DO DIA ").append(dataFolha).append("\n");
        relatorio.append("====================================\n\n");
        relatorio.append(folhaHoristas);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL HORISTAS %27d %5d %13.2f %9.2f %15.2f\n\n", totalHorasNormais, totalHorasExtras, totalBrutoHoristas, totalDescontosHoristas, totalLiquidoHoristas));
        relatorio.append(folhaAssalariados);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL ASSALARIADOS %43.2f %9.2f %15.2f\n\n", totalBrutoAssalariados, totalDescontosAssalariados, totalLiquidoAssalariados));
        relatorio.append(folhaComissionados);
        relatorio.append("\n");
        relatorio.append(String.format("TOTAL COMISSIONADOS %10.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n\n", totalFixoComissionados, totalVendasComissionados, totalComissaoComissionados, totalBrutoComissionados, totalDescontosComissionados, totalLiquidoComissionados));
        relatorio.append(String.format("TOTAL FOLHA: %.2f\n", totalFolhaGeral));

        String relatorioFinal = relatorio.toString();
        Comando c = new Comando() {
            @Override
            public void executar() {
                try {
                    FileWriter writer = new FileWriter(saida);
                    writer.write(relatorioFinal);
                    writer.close();
                }
                catch (Exception e) {
                    throw new RuntimeException("Falha ao escrever arquivo de folha.", e);
                }
                for (String empId : cartoesPagos.keySet()) {
                    Horista h = (Horista) empregadoServices.getEmpregados().get(empId);
                    if (h != null) h.getCartoesDePonto().removeAll(cartoesPagos.get(empId));
                }
                for (String empId : vendasPagas.keySet()) {
                    Comissionado co = (Comissionado) empregadoServices.getEmpregados().get(empId);
                    if (co != null) co.getVendas().removeAll(vendasPagas.get(empId));
                }
            }
            @Override
            public void desfazer() {
                File arquivo = new File(saida);
                if (arquivo.exists()) arquivo.delete();
                for (String empId : cartoesPagos.keySet()) {
                    Horista h = (Horista) empregadoServices.getEmpregados().get(empId);
                    if (h != null) h.getCartoesDePonto().addAll(cartoesPagos.get(empId));
                }
                for (String empId : vendasPagas.keySet()) {
                    Comissionado co = (Comissionado) empregadoServices.getEmpregados().get(empId);
                    if (co != null) co.getVendas().addAll(vendasPagas.get(empId));
                }
            }
        };

        c.executar();
        Sistema.undo.push(c);
        Sistema.redo.clear();
    }
}