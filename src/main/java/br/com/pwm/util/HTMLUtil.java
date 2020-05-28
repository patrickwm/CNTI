package br.com.pwm.util;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;

public class HTMLUtil {

    public static TagNode carregaHTML(String html){
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = null;
        node = cleaner.clean(html);

        return node;
    }

    public static TagNode carregaHTML(URL url) throws IOException {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = null;
        node = cleaner.clean(url);

        return node;
    }
}
