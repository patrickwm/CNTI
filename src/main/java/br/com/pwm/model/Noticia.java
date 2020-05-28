package br.com.pwm.model;

import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.util.List;

public class Noticia {
    private final SimpleBooleanProperty selecionado;
    private LocalDate data;
    private List<String> paragrafos;
    private String titulo;
    private String fonte;

    public Noticia(LocalDate data, String titulo, List<String> paragrafos, String fonte) {
        this.selecionado = new SimpleBooleanProperty(false);
        this.data = data;
        this.titulo = titulo;
        this.fonte = fonte;
        this.paragrafos = paragrafos;
    }

    public boolean isSelecionado() {
        return selecionado.get();
    }

    public SimpleBooleanProperty selecionadoProperty() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado.set(selecionado);
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String montaConteudo(){
        StringBuilder conteudo = new StringBuilder();

        paragrafos.forEach(p -> {
            conteudo.append(String.format("<p style=\"text-align: justify;\" align=\"justify\">%s</p>", p));
        });

        conteudo.append(String.format("<p style=\"text-align: right;\" align=\"right\"><strong>Fonte: %s</strong></p>", fonte));

        return conteudo.toString();
    }
}
