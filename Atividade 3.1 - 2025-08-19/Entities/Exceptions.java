package Entities;

import java.util.Date;

public class Exceptions {
    public static String primeiro(Reserva diaria, Date atual, String data1, String data2, Date chegada, Date saida){
        String resultado = new String();
        try{
            resultado = teste(diaria, atual);
        } catch (Exception e){
            System.out.println("Erro no saque: " + e.getMessage());
        }
        finally {
            if (resultado.equals("OK")){
                diaria.atualizar(chegada, saida);
                System.out.println("Reserva: Sala " + diaria.getNumero() + ", Check-in: " + data1 + ", Check-out: " + data2 + ", " + diaria.duracao() + " noites");
            }
        }
        return resultado;
    }

    public static String teste(Reserva diaria, Date atual) throws Exception{
        if (diaria.duracao() < 1){
            throw new Exception("Data de check-out deve ser depois da data de check-in");
        } else if (diaria.getChegada().before(atual) || diaria.getSaida().before(atual)){
            throw new Exception("Datas de reserva devem ser de datas futuras");
        }
        else return "OK";
    }
}
