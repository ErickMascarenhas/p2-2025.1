package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.List;

public class Sindicato {
    private static List<String> idsSindicato = new ArrayList<>();

    public static boolean inSindicato(String idSindicato){
        for (String id : idsSindicato) if (id.equals(idSindicato)) return true;
        return false;
    }

    public static void zerarSindicato(){
        idsSindicato.clear();
    }

    public static void addIdSindicato(String idSindicato){
        idsSindicato.add(idSindicato);
    }

    public static void removeIdSindicato(String idSindicato){
        idsSindicato.remove(idSindicato);
    }

    public static List<String> getIdsSindicato() {
        return idsSindicato;
    }

    public static void setIdsSindicato(List<String> idsSindicato) {
        Sindicato.idsSindicato = idsSindicato;
    }
}
