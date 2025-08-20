package application;

import Entities.Exceptions;
import Entities.Reserva;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Main{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        Date atual = new Date();
        System.out.print("Numero da sala: ");
        int numero = Integer.parseInt(input.nextLine());
        System.out.print("Data do check-in (dd/mm/aaaa): ");
        String data1 = input.nextLine();
        System.out.print("Data do check-out (dd/mm/aaaa): ");
        String data2 = input.nextLine();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date chegada = new Date();
        Date saida = new Date();
        try{
            chegada = formato.parse(data1);
            saida = formato.parse(data2);
        } catch (ParseException e){
            System.out.println("Erro na reserva: Formato de data invalido");
        }
        Reserva diaria = new Reserva(numero, chegada, saida);

        boolean continuar = false;
        if (Exceptions.primeiro(diaria, atual, data1, data2, chegada, saida).equals("OK")) continuar = true;

        if (continuar){
            System.out.println("\nPreencha os dados para atualizar a reserva:");
            System.out.print("Data do check-in (dd/mm/aaaa): ");
            data1 = input.nextLine();
            System.out.print("Data do check-out (dd/mm/aaaa): ");
            data2 = input.nextLine();
            try{
                chegada = formato.parse(data1);
                saida = formato.parse(data2);
            } catch (ParseException e){
                System.out.println("Erro na reserva: Formato de data invalido");
            }
            diaria.setChegada(chegada);
            diaria.setSaida(saida);

            Exceptions.primeiro(diaria, atual, data1, data2, chegada, saida);
        }
        input.close();
    }
}
