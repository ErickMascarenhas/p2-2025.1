package application;

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
        if (diaria.duracao() < 1) System.out.println("Erro na reserva: Data de check-out deve ser depois da data de check-in");
        else if (chegada.before(atual) || saida.before(atual)) System.out.println("Erro na reserva: Datas de reserva devem ser de datas futuras");
        else{
            System.out.println("Reserva: Sala " + numero + ", Check-in: " + data1 + ", Check-out: " + data2 + ", " + diaria.duracao() + " noites");
            continuar = true;
        }

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

            if (diaria.duracao() < 1) System.out.println("Erro na reserva: Data de check-out deve ser depois da data de check-in");
            else if (chegada.before(atual) || saida.before(atual)) System.out.println("Erro na reserva: Datas de reserva para atualizacao devem ser de datas futuras");
            else System.out.println("Reserva: Sala " + numero + ", Check-in: " + data1 + ", Check-out: " + data2 + ", " + diaria.duracao() + " noites");
        }
        input.close();
    }
}