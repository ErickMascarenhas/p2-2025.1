package Entities;

public class Conta{
    private int numero;
    private String usuario;
    private double poupanca;
    private double limitesaque;

    public Conta(int numero, String usuario, double poupanca, double limitesaque) {
        this.numero = numero;
        this.usuario = usuario;
        this.poupanca = poupanca;
        this.limitesaque = limitesaque;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public double getPoupanca() {
        return poupanca;
    }

    public void setPoupanca(double poupanca) {
        this.poupanca = poupanca;
    }

    public double getLimitesaque() {
        return limitesaque;
    }

    public void setLimitesaque(double limitesaque) {
        this.limitesaque = limitesaque;
    }

    public void depositar(double valor){
        setPoupanca(getPoupanca() + valor);
    }

    public void sacar(Conta pessoa, double valor){
        Exceptions.primeiro(pessoa, valor);
    }
}
