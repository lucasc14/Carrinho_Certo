package com.example.carrinhocerto.ui.listas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
 * Use the {@link CheckoutListasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutListasFragment extends Fragment {

    ListView listCheckout;
    Button btnFinalizarCompra;
    ArrayList<ItemCheckout> itensDaLista;
    CheckoutAdapter adapter;
    int idListaAtual = -1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CheckoutListasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckoutListasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutListasFragment newInstance(String param1, String param2) {
        CheckoutListasFragment fragment = new CheckoutListasFragment();
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
        View view = inflater.inflate(R.layout.fragment_checkout_listas, container, false);

        listCheckout = view.findViewById(R.id.listCheckout);
        btnFinalizarCompra = view.findViewById(R.id.btnFinalizarCompra);

        if (getArguments() != null) {
            idListaAtual = getArguments().getInt("ID_DA_LISTA", -1);
        }

        if (idListaAtual != -1) {
            carregarItensNoBanco(idListaAtual);
        } else {
            Toast.makeText(getContext(), "Erro ao carregar a lista.", Toast.LENGTH_SHORT).show();
        }

        //  Configura o CLIQUE no ListView (para riscar o item)
        listCheckout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Pega o item clicado na nossa lista paralela
                ItemCheckout itemClicado = itensDaLista.get(position);

                // Inverte o estado atual (se false vira true, se true vira false)
                itemClicado.setMarcado(!itemClicado.isMarcado());

                // Avisa o adapter que mudou, para ele re-desenhar a tela aplicando o risco
                adapter.notifyDataSetChanged();
            }
        });


        btnFinalizarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  separar os itens que não foram riscados
                ArrayList<ItemCheckout> itensPendentes = new ArrayList<>();
                for (ItemCheckout item : itensDaLista) {
                    if (!item.isMarcado()) {
                        itensPendentes.add(item);
                    }
                }

                if (itensPendentes.isEmpty()) {
                    //  Comprou tudo
                    finalizarListaNoBanco(idListaAtual);
                    Toast.makeText(getContext(), "Compra Finalizada com sucesso!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).popBackStack();
                } else {
                    //  Ficaram itens sem comprar
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Itens Pendentes")
                            .setMessage("Você deixou " + itensPendentes.size() + " itens sem marcar na lista. Deseja criar uma nova lista com esses itens pendentes para a próxima compra?")
                            .setPositiveButton("Sim, criar nova lista", (dialog, which) -> {

                                criarListaDePendentes(itensPendentes); //
                                finalizarListaNoBanco(idListaAtual);   //

                                Toast.makeText(getContext(), "Nova lista de pendentes criada!", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(v).popBackStack();
                            })
                            .setNegativeButton("Não, apenas finalizar", (dialog, which) -> {

                                finalizarListaNoBanco(idListaAtual);
                                Toast.makeText(getContext(), "Compra Finalizada!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(v).popBackStack();

                            })
                            .show();
                }
            }
        });

        return view;
    }


    private void carregarItensNoBanco(int idLista) {
        itensDaLista = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConexaoMySQL.conectar();

            String sql = "SELECT i.id_item, i.nome AS nome_item, i.quantidade, i.unidade, c.nome AS categoria " +
                    "FROM item i " +
                    "INNER JOIN categoria c ON i.categoria_id = c.id_categoria " +
                    "WHERE i.lista_id = ? " +
                    "ORDER BY c.nome ASC, i.nome ASC";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, idLista);
            rs = stmt.executeQuery();

            while (rs.next()) {

                itensDaLista.add(new ItemCheckout(
                        rs.getInt("id_item"),
                        rs.getString("nome_item"),
                        rs.getString("categoria"),
                        rs.getDouble("quantidade"),
                        rs.getString("unidade")
                ));
            }

            if (getActivity() != null) {
                adapter = new CheckoutAdapter(getActivity(), itensDaLista);
                listCheckout.setAdapter(adapter);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro de conexão com o banco", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void finalizarListaNoBanco(int idLista) {
        try (Connection con = ConexaoMySQL.conectar();
             PreparedStatement stmt = con.prepareStatement("UPDATE lista SET finalizada = 1 WHERE id_lista = ?")) {
            stmt.setInt(1, idLista);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void criarListaDePendentes(ArrayList<ItemCheckout> pendentes) {
        Connection con = null;
        PreparedStatement stmtLista = null;
        PreparedStatement stmtItem = null;
        ResultSet rs = null;

        try {
            con = ConexaoMySQL.conectar();

            String sqlLista = "INSERT INTO lista (login_id, nome) VALUES (1, '⚠️ Itens Pendentes')";
            stmtLista = con.prepareStatement(sqlLista, java.sql.Statement.RETURN_GENERATED_KEYS);
            stmtLista.executeUpdate();

            rs = stmtLista.getGeneratedKeys();
            int novaListaId = -1;
            if (rs.next()) {
                novaListaId = rs.getInt(1);
            }

            // Transfere os itens não comprados da lista velha para a lista nova
            if (novaListaId != -1) {
                String sqlItem = "UPDATE item SET lista_id = ? WHERE id_item = ?";
                stmtItem = con.prepareStatement(sqlItem);

                for (ItemCheckout item : pendentes) {
                    stmtItem.setInt(1, novaListaId);
                    stmtItem.setInt(2, item.getId());
                    stmtItem.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmtItem != null) stmtItem.close();
                if (stmtLista != null) stmtLista.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}