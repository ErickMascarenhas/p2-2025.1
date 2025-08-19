package Entities;

public class PJ extends Contribuinte{
    private int funcionarios;

    public PJ(String nome, double salario, int funcionarios) {
        super(nome, salario);
        this.funcionarios = funcionarios;
    }
    public int getFuncionarios() {return funcionarios;}
    public void setFuncionarios(int funcionarios) {this.funcionarios = funcionarios;}

    public double imposto(){
        return (getFuncionarios() < 10) ? getSalario() * 0.16 : getSalario() * 0.14;
    }
}