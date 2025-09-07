package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

public class Assalariado extends Empregado {
    public Assalariado(String id, String nome, String endereco, String tipo, double salario) throws EmpregadoNaoExisteException {
        super(id, nome, endereco, tipo, salario);
    }

    public Assalariado(){}

    public Assalariado(Assalariado assalariado){
        super(assalariado);
    }
}
