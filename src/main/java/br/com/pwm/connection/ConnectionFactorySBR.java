package br.com.pwm.connection;

import br.com.pwm.exception.ConexaoException;
import br.com.pwm.util.Propriedades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactorySBR {
    private static String usuario = Propriedades.get("usuario_sbr");
    private static String senha = Propriedades.get("senha_sbr");
    private static String banco = Propriedades.get("banco_sbr");
    private static String url = "jdbc:mysql://" + Propriedades.get("url_acesso_sbr");

    private static Connection conexao;

    public static synchronized void criaConexao() throws ConexaoException {
        String driverName = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driverName);
            conexao = DriverManager.getConnection(url + banco, usuario, senha);
        } catch (SQLException e) {
            throw new ConexaoException("Não foi possível se conectar ao servidor de banco de dados", e);
        } catch (ClassNotFoundException e) {
            throw new ConexaoException("Classe do driver do banco não foi encontrada", e);
        }
    }

    public static Connection getConnection() throws ConexaoException {
        try {
            if (conexao != null && !conexao.isClosed()) {
                return conexao;
            } else {
                criaConexao();
                return conexao;
            }
        } catch (SQLException e) {
            criaConexao();
            return conexao;
        }
    }




}
