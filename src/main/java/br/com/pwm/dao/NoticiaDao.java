package br.com.pwm.dao;

import br.com.pwm.connection.ConnectionFactorySBR;
import br.com.pwm.exception.ConexaoException;
import br.com.pwm.exception.DaoException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoticiaDao {
    final static Logger LOG = Logger.getLogger(NoticiaDao.class);

    public void adicionarNoticia(String titulo, LocalDate data, String conteudo) throws ConexaoException, DaoException {
        LOG.info("Adicionando notícia: "+ titulo);
        String sqlUltimaNoticia = "SELECT `fulltext`, created_by_alias, images, urls, attribs, ordering, metadata FROM jos_content WHERE catid = 10 ORDER BY 1 DESC;";

        String sqlInsertNoticia = "INSERT INTO " +
                "jos_content(asset_id, title, alias, introtext, `fulltext`, state, catid, created, created_by, created_by_alias, modified, modified_by, checked_out, checked_out_time, publish_up, publish_down, images, urls, attribs, version, ordering, metakey, metadesc, access, hits, metadata, featured, language, xreference)" +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            Connection conn = ConnectionFactorySBR.getConnection();
            Integer idAsset = criaAsset(conn, titulo);

            LOG.info("Carregando última notícia postada");
            PreparedStatement stmtUltimaNoticia = conn.prepareStatement(sqlUltimaNoticia);
            ResultSet rsUltima = stmtUltimaNoticia.executeQuery();
            rsUltima.next();
            LocalDateTime dateTime = LocalDateTime.of(data.getYear(), data.getMonth(), data.getDayOfMonth(), 10, 45);

            LOG.info("Preparando Insert");
            PreparedStatement stmt = conn.prepareStatement(sqlInsertNoticia);
            stmt = conn.prepareStatement(sqlInsertNoticia);
            int i = 1;
            stmt.setInt(i++ , idAsset);
            stmt.setString(i++ ,titulo);
            stmt.setString(i++,titulo.replaceAll("  ", "").trim().replaceAll(" ", "-"));
            stmt.setString(i++ , conteudo);
            stmt.setString(i++, rsUltima.getString("fulltext"));
            stmt.setInt(i++ , 1);
            stmt.setInt(i++ , 10);
            stmt.setString(i++ , dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(i++ , 42);
            stmt.setString(i++, rsUltima.getString("created_by_alias"));
            stmt.setString(i++ ,dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(i++ , 0);
            stmt.setInt(i++ ,0);
            stmt.setString(i++ ,"0000-00-00 00:00:00");
            stmt.setString(i++ ,dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(i++ ,"0000-00-00 00:00:00");
            stmt.setString(i++ ,rsUltima.getString("images"));
            stmt.setString(i++ ,rsUltima.getString("urls"));
            stmt.setString(i++ ,rsUltima.getString("attribs"));
            stmt.setInt(i++ ,1);
            stmt.setInt(i++ ,rsUltima.getInt("ordering") + 1);
            stmt.setString(i++ ,"metakey");
            stmt.setString(i++ , titulo);
            stmt.setInt(i++ , 1);
            stmt.setInt(i++ , 0);
            stmt.setString(i++ , rsUltima.getString("metadata"));
            stmt.setInt(i++ , 0);
            stmt.setString(i++ , "*");
            stmt.setString(i++ , "xreference");
            LOG.info("Executando Statement");
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
