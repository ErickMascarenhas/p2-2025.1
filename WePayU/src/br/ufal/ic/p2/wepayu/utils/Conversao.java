package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

public class Conversao {
    public static double converterSalario(String numero) throws Exception {
        if (numero.isEmpty()) throw new SalarioNuloException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new SalarioNegativoException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new SalarioNaoNumericoException();
        }
    }
    public static double converterComissao(String numero) throws Exception {
        if (numero.isEmpty()) throw new ComissaoNulaException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new ComissaoNegativaException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new ComissaoNaoNumericaException();
        }
    }

    public static double converterTaxaSindical(String numero) throws Exception {
        if (numero.isEmpty()) throw new TaxaSindicalNulaException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado < 0) throw new TaxaSindicalNegativaException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new TaxaSindicalNaoNumericaException();
        }
    }

    public static double receberHora(String numero) throws Exception {
        if (numero.isEmpty()) throw new HorasNulasException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado <= 0) throw new HorasNegativasException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new HorasNaoNumericasException();
        }
    }

    public static double receberValor(String numero) throws Exception {
        if (numero.isEmpty()) throw new ValorNuloException();
        try {
            double resultado = Double.parseDouble(numero.replace(",", "."));
            if (resultado <= 0) throw new ValorNegativoException();
            return resultado;
        }
        catch(NumberFormatException e) {
            throw new ValorNaoNumericoException();
        }
    }

    public static LocalDate converterData(String data) throws Exception {
        if (data.isEmpty()) throw new DataInvalidaException();
        try {
            if (data.contains("30/2") || data.contains("30/02")) throw new DataInvalidaException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalidaException();
        }
    }

    public static LocalDate converterDataInicial(String data) throws Exception {
        if (data.isEmpty()) throw new DataInvalidaException();
        try {
            if (data.contains("30/2") || data.contains("30/02")) throw new DataInicialInvalidaException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInicialInvalidaException();
        }
    }

    public static LocalDate converterDataFinal(String data) throws Exception {
        if (data.isEmpty()) throw new DataInvalidaException();
        try {
            if (data.contains("30/2/") || data.contains("30/02/")) throw new DataFinalInvalidaException();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new DataFinalInvalidaException();
        }
    }

    public static String converterHora(double numero) {
        DecimalFormat hora = new DecimalFormat();
        hora.applyPattern("#.##");
        return (hora.format(numero).replace(".", ","));
    }

    public static String darId(int i){
        return String.format("id%d", i);
    }

    public static boolean isUltimoDiaUtil(LocalDate data) {
        LocalDate ultimoDiaDoMes = data.with(TemporalAdjusters.lastDayOfMonth());
        while (ultimoDiaDoMes.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaDoMes.getDayOfWeek() == DayOfWeek.SUNDAY) ultimoDiaDoMes = ultimoDiaDoMes.minusDays(1);
        return data.isEqual(ultimoDiaDoMes);
    }

    public static boolean isDiaDePagamentoComissionado(LocalDate data) {
        if (data.getDayOfWeek() != DayOfWeek.FRIDAY) return false;
        LocalDate primeiroPagamento = LocalDate.of(2005, 1, 14);
        if (data.isBefore(primeiroPagamento)) return false;
        long diasDesdePrimeiroPagamento = java.time.temporal.ChronoUnit.DAYS.between(primeiroPagamento, data);
        return diasDesdePrimeiroPagamento % 14 == 0;
    }
}
