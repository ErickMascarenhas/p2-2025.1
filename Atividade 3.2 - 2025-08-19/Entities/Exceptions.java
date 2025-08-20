package Entities;

public class Exceptions {
    public static void primeiro(Conta pessoa, double saque){
        String resultado = new String();
        try{
            resultado = teste(pessoa.getPoupanca(), saque, pessoa.getLimitesaque());
        } catch (Exception e){
            System.out.println("Erro no saque: " + e.getMessage());
        }
        finally {
            if (resultado.equals("OK")){
                pessoa.setPoupanca(pessoa.getPoupanca() - saque);
                System.out.printf("Nova poupanca: %.2f\n", pessoa.getPoupanca());
            }
        }
    }

    public static String teste(double poupanca, double saque, double limitesaque) throws Exception{
        if (poupanca < saque){
            throw new Exception("Poupanca insuficiente");
        } else if (limitesaque < saque) {
            throw new Exception("Valor excede limite de saque");
        }
        else return "OK";
    }
}
