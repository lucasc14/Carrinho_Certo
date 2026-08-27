package com.example.carrinhocerto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Cadastro extends AppCompatActivity {

    EditText usuarioCadastro, senhaCadastro, confirmaSenhaCadastro;
    Button btCadastrar, btVoltarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        usuarioCadastro = findViewById(R.id.usuarioCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        confirmaSenhaCadastro = findViewById(R.id.confirmaSenhaCadastro);
        btCadastrar = findViewById(R.id.btCadastrar);
        btVoltarLogin = findViewById(R.id.btVoltarLogin);


        btVoltarLogin.setOnClickListener(v -> finish());


        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = usuarioCadastro.getText().toString().trim();
                String senha = senhaCadastro.getText().toString();
                String confirmaSenha = confirmaSenhaCadastro.getText().toString();

                if (usuario.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Cadastro.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!senha.equals(confirmaSenha)) {
                    confirmaSenhaCadastro.setError("As senhas não coincidem!");
                    return;
                }

                Connection con = null;
                PreparedStatement stmt = null;

                try {
                    con = ConexaoMySQL.conectar();
                    String sql = "INSERT INTO login (usuario, senha) VALUES (?, UPPER(SHA2(?,512)))";

                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, usuario);
                    stmt.setString(2, senha);

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas > 0) {
                        Toast.makeText(Cadastro.this, "Conta criada com sucesso!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(Cadastro.this, "Erro ao criar conta.", Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(Cadastro.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    try {
                        if (stmt != null) stmt.close();
                        if (con != null) con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}