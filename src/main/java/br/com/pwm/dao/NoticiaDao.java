package br.com.pwm.dao;

import br.com.pwm.connection.ConnectionFactorySBR;
import br.com.pwm.exception.ConexaoException;
import br.com.pwm.exception.DaoException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class NoticiaDao {
    final static Logger LOG = Logger.getLogger(NoticiaDao.class);

    private final String FULLTEXT = "";
    private final String CREATED_BY_ALIAS = "";
    private final String IMAGES = "{\"image_intro\":\"\",\"float_intro\":\"\",\"image_intro_alt\":\"\",\"image_intro_caption\":\"\",\"image_fulltext\":\"\",\"float_fulltext\":\"\",\"image_fulltext_alt\":\"\",\"image_fulltext_caption\":\"\"}";
    private final String URLS = "{\"urla\":false,\"urlatext\":\"\",\"targeta\":\"\",\"urlb\":false,\"urlbtext\":\"\",\"targetb\":\"\",\"urlc\":false,\"urlctext\":\"\",\"targetc\":\"\"}";
    private final String ATTRIBS = "{\"show_title\":\"\",\"link_titles\":\"\",\"show_tags\":\"\",\"show_intro\":\"\",\"info_block_position\":\"\",\"show_category\":\"\",\"link_category\":\"\",\"show_parent_category\":\"\",\"link_parent_category\":\"\",\"show_author\":\"\",\"link_author\":\"\",\"show_create_date\":\"\",\"show_modify_date\":\"\",\"show_publish_date\":\"\",\"show_item_navigation\":\"\",\"show_icons\":\"\",\"show_print_icon\":\"\",\"show_email_icon\":\"\",\"show_vote\":\"\",\"show_hits\":\"\",\"show_noauth\":\"\",\"urls_position\":\"\",\"alternative_readmore\":\"\",\"article_layout\":\"\",\"show_publishing_options\":\"\",\"show_article_options\":\"\",\"show_urls_images_backend\":\"\",\"show_urls_images_frontend\":\"\"}";
    private final Integer ORDERING = 10;
    private final String METADATA = "{\"robots\":\"\",\"author\":\"\",\"rights\":\"\",\"xreference\":\"\"}";

    public void adicionarNoticia(String titulo, LocalDate data, String conteudo, Consumer<String> estado) throws ConexaoException, DaoException {
        LOG.info("Adicionando notícia: "+ titulo);
        estado.accept("Iniciando rotina de envio de notícia");
        String sqlUltimaNoticia = "SELECT ordering FROM jos_content WHERE catid = 10 ORDER BY 1 DESC;";

        String sqlInsertNoticia = "INSERT INTO " +
                "jos_content(asset_id, title, alias, introtext, `fulltext`, state, catid, created, created_by, created_by_alias, modified, modified_by, checked_out, checked_out_time, publish_up, publish_down, images, urls, attribs, version, ordering, metakey, metadesc, access, hits, metadata, featured, language, xreference)" +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            Connection conn = ConnectionFactorySBR.getConnection();
            Integer idAsset = criaAsset(conn, titulo);

            LOG.info("Carregando última ordem");
            estado.accept("Carregando última ordem");
            PreparedStatement stmtUltimaNoticia = conn.prepareStatement(sqlUltimaNoticia);
            ResultSet rsUltima = stmtUltimaNoticia.executeQuery();
            rsUltima.next();
            LocalDateTime dateTime = LocalDateTime.of(data.getYear(), data.getMonth(), data.getDayOfMonth(), 10, 45);

            estado.accept("Preparando parâmetros");
            PreparedStatement stmt = conn.prepareStatement(sqlInsertNoticia);
            stmt = conn.prepareStatement(sqlInsertNoticia);
            int i = 1;
            stmt.setInt(i++ , idAsset);
            stmt.setString(i++ ,titulo);
            stmt.setString(i++, titulo.replaceAll("  ", "").trim().replaceAll(" ", "-"));
            stmt.setString(i++ , conteudo);
            stmt.setString(i++, FULLTEXT);
            stmt.setInt(i++ , 1);
            stmt.setInt(i++ , 10);
            stmt.setString(i++ , dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(i++ , 42);
            stmt.setString(i++, CREATED_BY_ALIAS);
            stmt.setString(i++ ,dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(i++ , 0);
            stmt.setInt(i++ ,0);
            stmt.setString(i++ ,"0000-00-00 00:00:00");
            stmt.setString(i++ ,dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(i++ ,"0000-00-00 00:00:00");
            stmt.setString(i++ , IMAGES);
            stmt.setString(i++ , URLS);
            stmt.setString(i++ , ATTRIBS);
            stmt.setInt(i++ ,1);
            stmt.setInt(i++ ,rsUltima.getInt("ordering") + 1);
            stmt.setString(i++ ,"metakey");
            stmt.setString(i++ , titulo);
            stmt.setInt(i++ , 1);
            stmt.setInt(i++ , 0);
            stmt.setString(i++ , METADATA);
            stmt.setInt(i++ , 0);
            stmt.setString(i++ , "*");
            stmt.setString(i++ , "xreference");
            LOG.info("Executando Statement");
            estado.accept("Enviando Notícia");
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("Houve um erro ao adicionar notícia no banco");
            throw new DaoException("Houve um erro ao adicionar Notícia no Banco", e);
        }
    }

    private int criaAsset(Connection conn, String titulo) throws SQLException {
        String sqlAssetsDados = "select max(lft) as lft, max(rgt) as rgt, max(CAST(SUBSTRING(name, POSITION('article.' in name) + 8) as SIGNED)) as maxArticle from jos_assets where parent_id = 84 and level = 3";
        String insertAssets =  "INSERT INTO jos_assets(parent_id, lft, rgt, level, name, title, rules) VALUES (84, ?, ?, 3, ?, ?, '{\"core.admin\":{\"7\":1},\"core.options\":[],\"core.manage\":{\"6\":1},\"core.create\":{\"3\":1},\"core.delete\":[],\"core.edit\":{\"4\":1},\"core.edit.state\":{\"5\":1},\"core.edit.own\":[]}');";

        PreparedStatement stmt = conn.prepareStatement(sqlAssetsDados);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Integer lft = rs.getInt("lft");
        Integer rgt = rs.getInt("rgt");
        Integer maxArticle = rs.getInt("maxArticle");
        rs.close();
        rs = null;
        stmt.close();
        stmt = null;

        stmt = conn.prepareStatement(insertAssets, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, lft + 2);
        stmt.setInt(2, rgt + 2);
        stmt.setString(3, "com_content.article." + (maxArticle + 1));
        stmt.setString(4, titulo);
        stmt.executeUpdate();
        rs = stmt.getGeneratedKeys();
        rs.next();
        Integer idAsset = rs.getInt(1);
        LOG.info("Asset gerado com sucesso: " + idAsset);
        rs.close();
        rs = null;
        stmt.close();
        stmt = null;

        return idAsset;
    }


}
