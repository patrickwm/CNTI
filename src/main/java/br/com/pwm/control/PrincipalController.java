package br.com.pwm.control;

import br.com.pwm.exception.ConexaoException;
import br.com.pwm.main.Preview;
import br.com.pwm.model.Noticia;
import br.com.pwm.util.HTTPUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PrincipalController implements Initializable {
    final static Logger LOG = Logger.getLogger(PrincipalController.class);

    @FXML
    private AnchorPane anpFundo;

    @FXML
    private TableView<Noticia> tbNoticias;

    @FXML
    private TableColumn<Noticia, Boolean> clmCheck;

    @FXML
    private TableColumn<Noticia, LocalDate> clmData;

    @FXML
    private TableColumn<Noticia, String> clmTitulo;

    @FXML
    private TextField txLink;

    @FXML
    private Button btCarregarNoticias;

    @FXML
    private Button btPreview;

    @FXML
    private Label txResultado;

    public void initialize(URL location, ResourceBundle resources) {
        initActions();
        initTable();
        setPadroes();
    }

    public void initActions() {
        btCarregarNoticias.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                analisar();
            }
        });

        btPreview.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                abrePreview();
            }
        });
    }

    public void initTable() {
        clmCheck.setCellValueFactory(new PropertyValueFactory<>("selecionado"));
        clmData.setCellValueFactory(new PropertyValueFactory<>("data"));
        clmTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        clmCheck.setCellFactory(CheckBoxTableCell.forTableColumn(clmCheck));
    }

    public void setPadroes() {
        txLink.setText("http://cnti.org.br/html/noticias.htm");
    }

    public void analisar() {
        String endereco = txLink.getText();
        if (!endereco.trim().isEmpty()) {
            try {
                TagNode tag = HTTPUtil.getHTML(endereco);
                String conteudo = getConteudoHTML(tag);
                tbNoticias.setItems(FXCollections.observableArrayList(formatarConteudo(conteudo)));
            } catch (ConexaoException e) {
                LOG.error(e.getCause());
                txResultado.setText(e.getMessage());
            }
        }

    }

    public String getConteudoHTML(TagNode html) {
        TagNode conteudo = html.getChildTags()[1] //BODY
                .getChildTags()[0] //<div align="center">
                .getChildTags()[0] //<center>
                .getChildTags()[0] //<table>
                .getChildTags()[0] //<tbody>
                .getChildTags()[1] //<tr conteudo>
                .getChildTags()[0]; //<td>

        return conteudo.getText().toString();
    }

    public List<Noticia> formatarConteudo(String conteudoCru) {
        List<String> noticiasSeparadas = new ArrayList<>();
        conteudoCru = retiraCaracteres(conteudoCru, "Blog - Últimas Notícias");
        //conteudoCru.split("\\d{2}\\/\\d{2}\\/\\d{4}") -> separar por datas
        String[] spt = conteudoCru.split("Fonte:");
        for (int i = 0; i < spt.length; i++) {
            if ((i + 1) < spt.length) {
                String s = spt[i].substring(spt[i].indexOf("\r"));
                String fonte = spt[i + 1];
                fonte = fonte.substring(0, fonte.indexOf("\r"));
                noticiasSeparadas.add(retiraCaracteres(s + " Fonte: " + fonte, "  ", "&nbsp;").trim()); //"\r", "\n",
            }
        }

        List<Noticia> noticias = new ArrayList<>();

        int erro = 0;
        for (String linha : noticiasSeparadas) {
            try {
                String[] s = linha.split(" -");
                String data = s[0];
                String resto = "";
                for (int i = 1; i < s.length; i++) {
                    resto += s[i];
                }
                //Retirando excesso de \r\n do começo da linha
                resto = resto.substring(45);
                String[] s2 = resto.split("\\r\\n\\r\\n\\r\\n\\r\\n\\r\\n");
                String titulo = retiraCaracteres(s2[0], "\r", "\n").trim();
                resto = "";
                for (int i = 1; i < s2.length; i++) {
                    resto += s2[i];
                }

                String[] paragrafos = resto.split("\\r\\n\\r\\n\\r\\n");

                String ultimoParagrafo = paragrafos[paragrafos.length - 1];
                int inicioFonte = ultimoParagrafo.indexOf("Fonte:");

                String fonte = ultimoParagrafo.substring(inicioFonte);
                paragrafos[paragrafos.length - 1] = ultimoParagrafo.substring(0, inicioFonte);

                List<String> paragrafosLimpos = Arrays.stream(paragrafos)
                    .map(this::verificaQuebraParagrafos)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

                noticias.add(
                    new Noticia(
                        LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        titulo,
                        paragrafosLimpos,
                        fonte.replaceAll("Fonte:", "")
                    )
                );
            } catch (Exception e){
                LOG.error("ERRO ao carregar notícia: " + retiraCaracteres(linha, "\r", "\n").trim());
                erro ++;
            }

        }

        if(erro > 0) {
            LOG.info(String.format("Houveram %d notícias que não puderam ser carregadas", erro));
        }
        return noticias;
    }

    private List<String> verificaQuebraParagrafos(String texto){
        String textoLimpo = retiraCaracteres(texto,  "\r", "\n");
        textoLimpo = textoLimpo.replaceAll("“", "\"");
        textoLimpo = textoLimpo.replaceAll("”", "\"");

        String textoAnalisado = procuraNovoParagrafo(textoLimpo);
        String[] resultadoAnalise = textoAnalisado.split("\n");
        return Arrays.asList(resultadoAnalise);
    }

    private String procuraNovoParagrafo(String texto) {
        if(texto == null || texto.isEmpty()) {
            return texto;
        }  else {
            int proximoPonto = texto.indexOf(".") + 1;
            if(proximoPonto > 1) {
                if(texto.length() > proximoPonto && texto.length() > proximoPonto + 1) {
                    String proximoCaracter = "" + texto.charAt(proximoPonto);
                    String proximoDoProximoCaracter = "" + texto.charAt(proximoPonto + 1);
                    if (proximoCaracter.equals(" ") || proximoCaracter.matches("^[0-9]")
                        || (proximoCaracter.equals("\"") && proximoDoProximoCaracter.equals(" "))) {
                        return texto.substring(0, proximoPonto) + procuraNovoParagrafo(texto.substring(proximoPonto));
                    } else if ((proximoCaracter.equals("\"") && proximoDoProximoCaracter.equals("."))) {
                        return texto.substring(0, proximoPonto + 2) + procuraNovoParagrafo(texto.substring(proximoPonto + 2));
                    } else if ((proximoCaracter.equals("\"") && !proximoDoProximoCaracter.equals(" "))) {
                        return texto.substring(0, proximoPonto + 1) + "\n" + procuraNovoParagrafo(texto.substring(proximoPonto + 1));
                    } else {
                        return texto.substring(0, proximoPonto) + "\n" + procuraNovoParagrafo(texto.substring(proximoPonto));
                    }
                } else {
                    return texto;
                }
            } else {
                return texto;
            }
        }
    }

    private String retiraCaracteres(String conteudo, String... caracteres) {
        for (String r : caracteres) {
            conteudo = conteudo.replaceAll(r, "");
        }

        return conteudo;
    }

    public void abrePreview(){
        tbNoticias.getItems().stream()
            .filter(Noticia::isSelecionado)
            .collect(Collectors.toList())
            .forEach(n -> {
                Preview p = new Preview(n);
                try {
                    p.start(new Stage());
                } catch (IOException e) {
                    LOG.error("Houve um erro ao abrir preview da notícia "+ n.getTitulo());
                }
            });
    }
}
