package br.com.pwm.util;

import br.com.pwm.dao.NoticiaDao;
import br.com.pwm.exception.ConexaoException;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HTTPUtil {
    final static Logger LOG = Logger.getLogger(NoticiaDao.class);

    public static TagNode getHTML(String endereco) throws ConexaoException {
        LOG.info("Conectando-se a "+ endereco);
        try {
            URL url = new URL(endereco);

            return HTMLUtil.carregaHTML(url);

            /*
            SimpleHtmlSerializer htmlSerializer = new SimpleHtmlSerializer(props);
            htmlSerializer.writeToStream(node, System.out);*/
        } catch (IOException e) {
            throw new ConexaoException("Houve um erro ao se conectar no endereço "+endereco, e);
        }
    }

    public static String getHttp(String URL) throws ConexaoException {
        LOG.info("Acessando URL: " + URL);
        BufferedReader bufferedReader = null;
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet();
            //request.setHeader("Content-Type", "text/html; charset=utf-8");
            request.setURI(new URI(URL));
            HttpResponse response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");

            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + NL);
            }
            bufferedReader.close();

            return stringBuffer.toString();
        } catch (IOException | URISyntaxException e) {
            LOG.error("Erro ao acessar URL: " + URL);
            throw new ConexaoException("Houve um erro ao tentar acessar " + URL + " . Informe o desenvolvimento", e);
        } finally{
            if (bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    LOG.error("Erro ao fechar bufferdReader");
                }
            }
        }
    }

    public static void sendGet(String url) throws ConexaoException {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity, Charsets.UTF_8);
                System.out.println(result);
            }
            httpClient.close();
        } catch (IOException e) {
            LOG.error("Erro ao acessar URL: " + url);
            throw new ConexaoException("Houve um erro ao tentar acessar " + url + " . Informe o desenvolvimento", e);
        }

    }
}
