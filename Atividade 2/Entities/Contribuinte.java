package Entities;

public abstract class Contribuinte{
    private String nome;
    private double salario;

    public Contribuinte(String nome, double salario){
        this.nome = nome;
        this.salario = salario;
    }

    public String getNome() {return nome;}
    public double getSalario() {return salario;}
    public void setNome(String nome) {this.nome = nome;}
    public void setSalario(double salario) {this.salario = salario;}

    public double imposto() {
        return 0;
    }
}