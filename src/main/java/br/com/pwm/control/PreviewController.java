package br.com.pwm.control;

import br.com.pwm.dao.NoticiaDao;
import br.com.pwm.exception.ConexaoException;
import br.com.pwm.exception.DaoException;
import br.com.pwm.exception.ValidacaoException;
import br.com.pwm.model.Noticia;
import br.com.pwm.util.HTMLUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PreviewController implements Initializable {
    final static Logger LOG = Logger.getLogger(PreviewController.class);
    @FXML
    private Button btEnviar;

    @FXML
    private TextField txTitulo;

    @FXML
    private DatePicker dtData;

    @FXML
    private HTMLEditor htEditor;

    @FXML
    private Label txResultado;

    @FXML
    private ProgressIndicator piCarregando;

    private Noticia noticia;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initActions();
        piCarregando.setVisible(false);
    }

    public void initActions(){
        btEnviar.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                new Thread(() -> {
                    Platform.runLater(() -> {
                        txTitulo.setDisable(true);
                        dtData.setDisable(true);
                        btEnviar.setDisable(true);
                    });

                    enviar();
                }).start();
            }
        });

        piCarregando.managedProperty().bindBidirectional(piCarregando.visibleProperty());

    }

    public void preencheConteudoNoticia(){
        txTitulo.setText(noticia.getTitulo().replaceAll("  ", " "));
        dtData.setValue(noticia.getData());
        htEditor.setHtmlText(noticia.montaConteudo());
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    private void close(){
        this.stage.close();
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public void enviar() {
        try {
            String titulo = txTitulo.getText();
            LocalDate data = dtData.getValue();
            String conteudo = htEditor.getHtmlText()
                    .replaceAll("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">", "")
                    .replaceAll("</body></html>", "");

            if (titulo.isEmpty() || conteudo.isEmpty()) {
                throw new ValidacaoException("Existem campos obrigatórios vazios!");
            }
            Platform.runLater(() -> {
                piCarregando.setVisible(true);
                txResultado.setText("Enviando Noticia");
            });
            NoticiaDao dao = new NoticiaDao();
            dao.adicionarNoticia(titulo, data, conteudo);
            Platform.runLater(() -> {
                piCarregando.setVisible(false);
                txResultado.setText("Notícia cadastrada com sucesso!");
                close();
            });

        } catch (ValidacaoException | ConexaoException | DaoException e){
            LOG.error("Erro ao adicionar notícia", e);
            Platform.runLater(() -> {
                piCarregando.setVisible(false);
                txResultado.setText("Erro ao cadastrar notícia no Banco");
                txTitulo.setDisable(false);
                dtData.setDisable(false);
                btEnviar.setDisable(false);
            });
        }
    }
}
