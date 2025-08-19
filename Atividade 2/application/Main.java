package application;

import java.util.Locale;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import Entities.Contribuinte;
import Entities.PF;
import Entities.PJ;

public class Main{
    public static void main(String args[]){
        Locale.setDefault(Locale.US);
        Scanner input = new Scanner(System.in);
        List<Contribuinte> lista = new ArrayList<>();
        System.out.print("Defina quantidade de contribuintes: ");
        int quantidade = input.nextInt();
        for (int i = 0; i < quantidade; i++){
            System.out.println("Dados do contribuinte #" + (i + 1) + ":");
            System.out.print("Pessoa física ou jurídica (f/j)? ");
            char tipo = input.next().strip().charAt(0);
            input.nextLine();
            System.out.print("Nome: ");
            String nome = input.nextLine();
            System.out.print("Salario anual: ");
            double salario = input.nextDouble();
            if (tipo == 'f'){
                System.out.print("Despesas com saúde: ");
                double despesas = input.nextDouble();
                lista.add(new PF(nome, salario, despesas));
            }
            else{
                System.out.print("Quantidade de funcionários: ");
                int funcionarios = input.nextInt();
                lista.add(new PJ(nome, salario, funcionarios));
            }
        }

        System.out.println("\nIMPOSTOS PAGOS:");
        double total = 0;
        for (Contribuinte pessoa : lista){
            System.out.println(pessoa.getNome() + ": $ " + String.format("%.2f", pessoa.imposto()));
            total += pessoa.imposto();
        }

        System.out.println("\nTOTAL DE IMPOSTOS: $ " + String.format("%.2f", total));

        input.close();
    }
}