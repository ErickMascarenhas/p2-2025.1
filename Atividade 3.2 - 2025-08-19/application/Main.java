package application;

import java.util.Scanner;
import Entities.Conta;
import java.util.Locale;

public class Main{
    public static void main(String[] args){
        Locale.setDefault(Locale.US);
        Scanner input = new Scanner(System.in);
        System.out.println("Preencha os dados da conta");
        System.out.print("Numero: ");
        int numero = Integer.parseInt(input.nextLine());
        System.out.print("Usuario: ");
        String usuario = input.nextLine();
        System.out.print("Poupanca inicial: ");
        double poupanca = input.nextDouble();
        System.out.print("Limite de saque: ");
        double limite = input.nextDouble();
        Conta pessoa = new Conta(numero, usuario, poupanca, limite);
        System.out.print("\nInsira valor para saque: ");
        double valor = input.nextDouble();
        pessoa.sacar(pessoa, valor);
    }
}
