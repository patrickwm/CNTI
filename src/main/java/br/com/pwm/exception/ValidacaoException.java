package br.com.pwm.exception;

public class ValidacaoException extends Exception {

    public ValidacaoException(String mensagem){
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }

}
