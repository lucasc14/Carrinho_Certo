package com.example.carrinhocerto.ui.listas;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.carrinhocerto.ConexaoMySQL;
import com.example.carrinhocerto.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListasFragment extends Fragment {

    SearchView buscaListas;
    ListView listListas;
    ArrayList<String> dadosLista;
    ArrayList<Integer> idsLista;
    ArrayAdapter<String> adaptador;
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListasFragment newInstance(String param1, String param2) {
        ListasFragment fragment = new ListasFragment();
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
        View view = inflater.inflate(R.layout.fragment_listas, container, false);
        buscaListas = view.findViewById(R.id.buscaListas);
        listListas = view.findViewById(R.id.listListas);
        atualizaLista();



        listListas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idSelecionado = idsLista.get(position);

                String[] opcoes = {"🛒 Fazer Checkout", "✏️ Editar Lista", "🗑️ Excluir Lista"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("O que deseja fazer?");
                builder.setItems(opcoes, (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_DA_LISTA", idSelecionado);

                    if (which == 0) {
                        // Opção 0: Vai para o Checkout
                        Navigation.findNavController(view).navigate(R.id.nav_checkoutListas, bundle);
                    }
                    else if (which == 1) {
                        // Opção 1: Vai para a tela de Editar Itens
                        Navigation.findNavController(view).navigate(R.id.nav_editarListas, bundle);
                    }
                    else if (which == 2) {
                        // Opção 2: Pede confirmação para excluir
                        confirmarExclusaoDaLista(idSelecionado);
                    }
                });
                builder.show();
            }
        });

        @SuppressLint("CutPasteId") SearchView searchView = view.findViewById(R.id.buscaListas); // Substitua pelo ID real

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarLista(1, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()) {
                    atualizaLista();
                } else {

                    pesquisarLista(1, newText);
                }
                return false;
            }
        });
        return view;
    }

    public void atualizaLista (){

        try {
            con = ConexaoMySQL.conectar();
            sql = "SELECT id_lista, nome, DATE_FORMAT(data_criacao, '%d/%m/%Y') AS data_formatada, finalizada " +
                    "FROM lista " +
                    "WHERE login_id = 1 " +
                    "ORDER BY id_lista DESC;";
            stmt = con.prepareStatement(sql);
            dadosLista =  new ArrayList<String>();
            idsLista = new ArrayList<Integer>();
            rs = stmt.executeQuery();
            while (rs.next()){
                idsLista.add(rs.getInt("id_lista"));
               dadosLista.add( "Nome: " + rs.getString("nome") + "\n"
                        + "Criada em: " + rs.getString("data_formatada"));
            }
            adaptador = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dadosLista);
            listListas.setAdapter(adaptador);
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void pesquisarLista(int loginId, String termoPesquisa) {
        try {
            con = ConexaoMySQL.conectar();

            sql = "SELECT id_lista, nome, DATE_FORMAT(data_criacao, '%d/%m/%Y') AS data_formatada, finalizada " +
                    "FROM lista " +
                    "WHERE login_id = ? AND nome LIKE ? " +
                    "ORDER BY id_lista DESC;";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, loginId);
            stmt.setString(2, "%" + termoPesquisa + "%");

            dadosLista = new ArrayList<String>();
            idsLista = new ArrayList<Integer>();
            rs = stmt.executeQuery();

            while (rs.next()){
                idsLista.add(rs.getInt("id_lista"));
                String status = rs.getInt("finalizada") == 1 ? "Finalizada" : "Em aberto";

                dadosLista.add("Lista #" + rs.getString("id_lista") + "\n"
                        + "Nome: " + rs.getString("nome") + "\n"
                        + "Criada em: " + rs.getString("data_formatada") + "\n"
                        + "Status: " + status);
            }

            adaptador = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dadosLista);
            listListas.setAdapter(adaptador);

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmarExclusaoDaLista(int idLista) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Excluir Lista")
                .setMessage("Tem certeza que deseja excluir esta lista? Todos os itens dela também serão apagados permanentemente.")
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {

                    excluirListaNoBanco(idLista);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {

                    dialog.dismiss();
                })
                .show();
    }


    private void excluirListaNoBanco(int idLista) {
        Connection con = null;
        PreparedStatement stmtItem = null;
        PreparedStatement stmtLista = null;

        try {
            con = ConexaoMySQL.conectar();

            String sqlItens = "DELETE FROM item WHERE lista_id = ?";
            stmtItem = con.prepareStatement(sqlItens);
            stmtItem.setInt(1, idLista);
            stmtItem.executeUpdate();

            String sqlLista = "DELETE FROM lista WHERE id_lista = ?";
            stmtLista = con.prepareStatement(sqlLista);
            stmtLista.setInt(1, idLista);
            stmtLista.executeUpdate();

            Toast.makeText(getContext(), "Lista excluída com sucesso!", Toast.LENGTH_SHORT).show();
            atualizaLista();

        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e("ERRO_EXCLUSAO", "Erro ao excluir lista", e);
            Toast.makeText(getContext(), "Erro ao excluir a lista: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (stmtItem != null) stmtItem.close();
                if (stmtLista != null) stmtLista.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}