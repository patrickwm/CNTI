package br.com.pwm.exception;

public class DaoException extends Exception {

    public DaoException(String mensagem){
        super(mensagem);
    }

    public DaoException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }

}
