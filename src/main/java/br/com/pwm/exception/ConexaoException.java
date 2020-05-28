package br.com.pwm.exception;

public class ConexaoException extends Exception {

    public ConexaoException(String mensagem){
        super(mensagem);
    }

    public ConexaoException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }

}
