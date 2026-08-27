package com.example.carrinhocerto.ui.listas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carrinhocerto.ConexaoMySQL;
import com.example.carrinhocerto.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarListasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarListasFragment extends Fragment {

    EditText nomeItemLista, qtdItemLista;
    Spinner spinnerCategoria, spinnerUnidade;
    Button btAdicionarItem;

    ListView listViewItens;


    ArrayAdapter<CharSequence> adapterUnidade;
    ArrayList<String> dadosItens;
    ArrayAdapter<String> adaptadorItens;

    ArrayList<String> listaNomesCategorias = new ArrayList<>();
    ArrayList<Integer> listaIdsCategorias = new ArrayList<>();
    ArrayAdapter<String> adapterCategoriaDinamico;

    Connection con = null;
    PreparedStatement stmt = null;
    String sql;
    int idListaCriada = -1;

    ArrayList<Integer> idsItens = new ArrayList<>();
    ArrayList<String> nomesItens = new ArrayList<>();
    ArrayList<Double> qtdsItens = new ArrayList<>();
    ArrayList<String> unsItens = new ArrayList<>();
    ArrayList<String> catsItens = new ArrayList<>();

    int idItemEmEdicao = -1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditarListasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarListasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarListasFragment newInstance(String param1, String param2) {
        EditarListasFragment fragment = new EditarListasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_listas, container, false);
        nomeItemLista = view.findViewById(R.id.nomeItemLista);
        qtdItemLista = view.findViewById(R.id.qtdItemLista);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        spinnerUnidade = view.findViewById(R.id.spinnerUnidade);
        btAdicionarItem = view.findViewById(R.id.btAdicionarItem);
        listViewItens = view.findViewById(R.id.listViewItens);
        carregarCategoriasDoBanco();


        adapterUnidade = ArrayAdapter.createFromResource(getContext(), R.array.unidadeMedida , android.R.layout.simple_spinner_item);
        adapterUnidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidade.setAdapter(adapterUnidade);



        if (getArguments() != null) {
            idListaCriada = getArguments().getInt("ID_DA_LISTA", -1);

        }

        carregarItens();

        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] opcoes = {"✏️ Editar Item", "🗑️ Excluir Item"};

                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Opções do Item")
                        .setItems(opcoes, (dialog, which) -> {
                            if (which == 0) {

                                prepararEdicao(position);
                            } else if (which == 1) {

                                confirmarExclusaoItem(idsItens.get(position));
                            }
                        }).show();
            }
        });
        btAdicionarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeItem = nomeItemLista.getText().toString().trim();
                String qtdString = qtdItemLista.getText().toString().trim();

                if (nomeItem.isEmpty() || qtdString.isEmpty()) {
                    Toast.makeText(getContext(), "Preencha o nome e a quantidade!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double quantidade = Double.parseDouble(qtdString);
                String unidadeSelecionada = spinnerUnidade.getSelectedItem().toString();
                int posicaoSelecionada = spinnerCategoria.getSelectedItemPosition();
                int categoriaId = listaIdsCategorias.get(posicaoSelecionada);

                if (categoriaId == -1) {
                    Toast.makeText(getContext(), "Selecione uma categoria válida!", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    con = ConexaoMySQL.conectar();

                    if (idItemEmEdicao == -1) {

                        sql = "INSERT INTO item (lista_id, categoria_id, nome, quantidade, unidade) VALUES (?, ?, ?, ?, ?)";
                        stmt = con.prepareStatement(sql);
                        stmt.setInt(1, idListaCriada);
                        stmt.setInt(2, categoriaId);
                        stmt.setString(3, nomeItem);
                        stmt.setDouble(4, quantidade);
                        stmt.setString(5, unidadeSelecionada);
                    } else {

                        sql = "UPDATE item SET categoria_id = ?, nome = ?, quantidade = ?, unidade = ? WHERE id_item = ?";
                        stmt = con.prepareStatement(sql);
                        stmt.setInt(1, categoriaId);
                        stmt.setString(2, nomeItem);
                        stmt.setDouble(3, quantidade);
                        stmt.setString(4, unidadeSelecionada);
                        stmt.setInt(5, idItemEmEdicao);
                    }

                    stmt.executeUpdate();
                    stmt.close();
                    con.close();


                    nomeItemLista.setText("");
                    qtdItemLista.setText("1");
                    nomeItemLista.requestFocus();
                    spinnerCategoria.setSelection(0);
                    spinnerUnidade.setSelection(0);

                    idItemEmEdicao = -1;
                    btAdicionarItem.setText("Adicionar à Lista");

                    carregarItens();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

    private void carregarItens() {
        if (idListaCriada == -1) return;
        String consulta = "SELECT i.id_item, i.nome, i.quantidade, i.unidade, c.nome AS categoria " +
                "FROM item i JOIN categoria c ON c.id_categoria = i.categoria_id " +
                "WHERE i.lista_id = ? ORDER BY c.nome, i.nome";
        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement st = c.prepareStatement(consulta)) {
            st.setInt(1, idListaCriada);
            ResultSet rs = st.executeQuery();

            dadosItens = new ArrayList<>();
            idsItens.clear();
            nomesItens.clear();
            qtdsItens.clear();
            unsItens.clear();
            catsItens.clear();

            while (rs.next()) {
                int id = rs.getInt("id_item");
                String nome = rs.getString("nome");
                double qtd = rs.getDouble("quantidade");
                String un = rs.getString("unidade");
                String cat = rs.getString("categoria");


                idsItens.add(id);
                nomesItens.add(nome);
                qtdsItens.add(qtd);
                unsItens.add(un);
                catsItens.add(cat);

                // Formata o texto que vai aparecer no ListView
                dadosItens.add(nome + " - " + qtd + " " + un + " (" + cat + ")");
            }
            if (getActivity() != null) {
                adaptadorItens = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dadosItens);
                listViewItens.setAdapter(adaptadorItens);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e("ERRO_BANCO", "Falha exata: ", e);
            Toast.makeText(getContext(), "Erro ao carregar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void carregarCategoriasDoBanco() {
        listaNomesCategorias.clear();
        listaIdsCategorias.clear();


        listaNomesCategorias.add("Selecione uma Categoria");
        listaIdsCategorias.add(-1);

        String sql = "SELECT id_categoria, nome FROM categoria ORDER BY nome ASC";

        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                listaIdsCategorias.add(rs.getInt("id_categoria"));
                listaNomesCategorias.add(rs.getString("nome"));
            }

            if (getActivity() != null) {
                adapterCategoriaDinamico = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listaNomesCategorias);
                adapterCategoriaDinamico.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapterCategoriaDinamico);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao carregar categorias do banco.", Toast.LENGTH_SHORT).show();
        }
    }



    private void prepararEdicao(int position) {

        idItemEmEdicao = idsItens.get(position);


        nomeItemLista.setText(nomesItens.get(position));
        qtdItemLista.setText(String.valueOf(qtdsItens.get(position)));


        spinnerCategoria.setSelection(adapterCategoriaDinamico.getPosition(catsItens.get(position)));
        spinnerUnidade.setSelection(adapterUnidade.getPosition(unsItens.get(position)));


        btAdicionarItem.setText("Salvar Alteração");
    }

    private void confirmarExclusaoItem(int idItem) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Excluir Item")
                .setMessage("Deseja realmente remover este item da lista?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    try {
                        con = ConexaoMySQL.conectar();
                        sql = "DELETE FROM item WHERE id_item = ?";
                        stmt = con.prepareStatement(sql);
                        stmt.setInt(1, idItem);
                        stmt.executeUpdate();
                        stmt.close();
                        con.close();

                        Toast.makeText(getContext(), "Item excluído!", Toast.LENGTH_SHORT).show();
                        carregarItens(); // Atualiza a lista
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}