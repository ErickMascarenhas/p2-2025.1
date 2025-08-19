package Entities;

public class PF extends Contribuinte {
    private double despesas;

    public PF(String nome, double salario, double despesas) {
        super(nome, salario);
        this.despesas = despesas;
    }
    public double getDespesas() {return despesas;}
    public void setDespesas(double despesas) {this.despesas = despesas;}

    public double imposto(){
        return (getSalario() < 20000) ? getSalario() * 0.15 - getDespesas() / 2 : getSalario() * 0.25 - getDespesas() / 2;
    }
}