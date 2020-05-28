package br.com.pwm.main;

import br.com.pwm.control.PreviewController;
import br.com.pwm.model.Noticia;
import br.com.pwm.util.Tela;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Preview extends Application {

    private Noticia noticia;

    public Preview(Noticia noticia){
        this.noticia = noticia;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        FXMLLoader fxml = Tela.carregaFXML(getClass());
        stage = Tela.carregaStage(fxml, stage, "Preview - " + noticia.getTitulo());
        PreviewController ctr = fxml.getController();
        ctr.setNoticia(noticia);
        ctr.preencheConteudoNoticia();
        stage.show();
        ctr.setStage(stage);

    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }
}
