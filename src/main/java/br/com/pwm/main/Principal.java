package br.com.pwm.main;

import br.com.pwm.connection.ConnectionFactorySBR;
import br.com.pwm.exception.ConexaoException;
import br.com.pwm.util.Tela;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Principal extends Application {
    final static Logger LOG = Logger.getLogger(Principal.class);

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                LOG.info("Criando conexão com SBR");
                ConnectionFactorySBR.criaConexao();
                LOG.info("Conexão criada com SBR");
            } catch (ConexaoException e) {
                LOG.error("Erro ao criar conexão", e);
                System.exit(0);
            }
        }).start();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("https.protocols", "TLSv1.1");
        stage = Tela.carregaTela(getClass(), stage, "Principal");
        stage.show();
    }
}
