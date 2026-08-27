package com.example.carrinhocerto.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.carrinhocerto.ConexaoMySQL;
import com.example.carrinhocerto.Login;
import com.example.carrinhocerto.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private EditText etNomeCategoria;
    private Button btnAdicionarCategoria;
    private ListView lvCategorias;

    private CategoriaAdapter adapter;
    private List<Categoria> listaCategorias = new ArrayList<>();
    private int idCategoriaEmEdicao = -1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etNomeCategoria = view.findViewById(R.id.etNomeCategoria);
        btnAdicionarCategoria = view.findViewById(R.id.btnAdicionarCategoria);
        lvCategorias = view.findViewById(R.id.lvCategorias);


        adapter = new CategoriaAdapter();
        lvCategorias.setAdapter(adapter);

        carregarCategorias();

        btnAdicionarCategoria.setOnClickListener(v -> {
            String nomeCategoria = etNomeCategoria.getText().toString().trim();
            if (nomeCategoria.isEmpty()) {
                etNomeCategoria.setError("Digite um nome!");
                return;
            }

            if (idCategoriaEmEdicao == -1) {
                adicionarCategoriaBanco(nomeCategoria);
            } else {
                editarCategoriaBanco(idCategoriaEmEdicao, nomeCategoria);
            }
        });


        return view;
    }

    private void carregarCategorias() {
        listaCategorias.clear();
        try (Connection con = ConexaoMySQL.conectar();
             PreparedStatement stmt = con.prepareStatement("SELECT id_categoria, nome FROM categoria ORDER BY nome ASC");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaCategorias.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nome")));
            }
            adapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao carregar categorias.", Toast.LENGTH_SHORT).show();
        }
    }

    private void adicionarCategoriaBanco(String nome) {
        try (Connection con = ConexaoMySQL.conectar();
             PreparedStatement stmt = con.prepareStatement("INSERT INTO categoria (nome) VALUES (?)")) {

            stmt.setString(1, nome);
            stmt.executeUpdate();

            etNomeCategoria.setText("");
            Toast.makeText(getContext(), "Categoria adicionada!", Toast.LENGTH_SHORT).show();
            carregarCategorias();

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao adicionar. A categoria já existe?", Toast.LENGTH_LONG).show();
        }
    }

    private void editarCategoriaBanco(int id, String novoNome) {
        try (Connection con = ConexaoMySQL.conectar();
             PreparedStatement stmt = con.prepareStatement("UPDATE categoria SET nome = ? WHERE id_categoria = ?")) {

            stmt.setString(1, novoNome);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            etNomeCategoria.setText("");
            btnAdicionarCategoria.setText("Adicionar");
            idCategoriaEmEdicao = -1;

            Toast.makeText(getContext(), "Categoria atualizada!", Toast.LENGTH_SHORT).show();
            carregarCategorias();

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao editar categoria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletarCategoriaBanco(int id) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remover Categoria")
                .setMessage("Tem certeza que deseja remover esta categoria?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    try (Connection con = ConexaoMySQL.conectar();
                         PreparedStatement stmt = con.prepareStatement("DELETE FROM categoria WHERE id_categoria = ?")) {

                        stmt.setInt(1, id);
                        stmt.executeUpdate();

                        Toast.makeText(getContext(), "Categoria removida!", Toast.LENGTH_SHORT).show();
                        carregarCategorias();

                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Não foi possível remover. Existem itens vinculados a esta categoria.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }


    class Categoria {
        int id;
        String nome;

        Categoria(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }
    }


    class CategoriaAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listaCategorias.size();
        }

        @Override
        public Object getItem(int position) {
            return listaCategorias.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_categoria, parent, false);
            }

            TextView tvNome = convertView.findViewById(R.id.tvNomeCategoria);
            ImageButton btnEditar = convertView.findViewById(R.id.btnEditar);
            ImageButton btnRemover = convertView.findViewById(R.id.btnRemover);

            Categoria cat = listaCategorias.get(position);
            tvNome.setText(cat.nome);

            btnEditar.setOnClickListener(v -> {
                etNomeCategoria.setText(cat.nome);
                btnAdicionarCategoria.setText("Salvar");
                idCategoriaEmEdicao = cat.id;
            });

            btnRemover.setOnClickListener(v -> {
                deletarCategoriaBanco(cat.id);
            });

            return convertView;
        }
    }
}