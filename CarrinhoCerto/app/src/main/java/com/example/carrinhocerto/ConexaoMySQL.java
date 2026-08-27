package com.example.carrinhocerto;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {
    private static final String URL = "jdbc:mysql://mysql-27c86d99-lucas-8e52.a.aivencloud.com:10862/carrinho_certo";
    private static final String USUARIO = "avnadmin"; //super usuario do banco de dados
    private static final String SENHA = "AVNS_C_4n2jyksGmcHkG6Z8I";

    public static Connection conectar() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;

        }
    }

    public static void fecharConexao(Connection conexao) {
        try {
            if (conexao != null) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
