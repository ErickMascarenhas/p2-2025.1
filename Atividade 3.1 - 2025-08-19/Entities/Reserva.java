package Entities;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reserva{
    int numero;
    Date chegada;
    Date saida;

    public Reserva(int numero, Date chegada, Date saida) {
        this.numero = numero;
        this.chegada = chegada;
        this.saida = saida;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getChegada() {
        return chegada;
    }

    public void setChegada(Date chegada) {
        this.chegada = chegada;
    }

    public Date getSaida() {
        return saida;
    }

    public void setSaida(Date saida) {
        this.saida = saida;
    }

    public int duracao(){
        long diferenca = getSaida().getTime() - getChegada().getTime();
        return (int) TimeUnit.DAYS.convert(diferenca, TimeUnit.MILLISECONDS);
    }

    public void atualizar(Date chegada, Date saida){
        setChegada(chegada);
        setSaida(saida);
    }
}