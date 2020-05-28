package br.com.pwm.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Tela {
    private static final String PACOTE_VIEW = "/br/com/pwm/view/";

    public static Stage carregaTela(Class c, Stage stage, String titulo) throws IOException {
        FXMLLoader fxml = carregaFXML(c);
        return carregaStage(fxml, stage, titulo);
    }

    public static FXMLLoader carregaFXML(Class c){
        FXMLLoader fxml = new FXMLLoader(c.getResource(String.format("%s%s.fxml", PACOTE_VIEW, c.getSimpleName())));
        return fxml;
    }

    public static Stage carregaStage(FXMLLoader fxml, Stage stage, String titulo) throws IOException {
        Parent root = fxml.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(titulo);

        return stage;
    }

}
