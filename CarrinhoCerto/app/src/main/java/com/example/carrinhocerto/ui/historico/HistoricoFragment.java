package com.example.carrinhocerto.ui.historico;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
 * Use the {@link HistoricoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoricoFragment extends Fragment {

    ListView listHistorico;
    ArrayList<String> dadosHistorico;
    ArrayAdapter<String> adaptador;

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoricoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoricoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoricoFragment newInstance(String param1, String param2) {
        HistoricoFragment fragment = new HistoricoFragment();
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
        View view = inflater.inflate(R.layout.fragment_historico, container, false);
        listHistorico = view.findViewById(R.id.listHistorico);

        carregarHistorico();

        return view;
    }

    private void carregarHistorico() {


        try {
            con = ConexaoMySQL.conectar();
            // Busca apenas as listas FINALIZADAS (finalizada = 1) do usuário logado
            String sql = "SELECT id_lista, nome, DATE_FORMAT(data_criacao, '%d/%m/%Y') AS data_formatada " +
                    "FROM lista " +
                    "WHERE login_id = 1 AND finalizada = 1 " +
                    "ORDER BY id_lista DESC;";

            stmt = con.prepareStatement(sql);
            dadosHistorico = new ArrayList<>();
            rs = stmt.executeQuery();

            while (rs.next()) {
                dadosHistorico.add("Lista #" + rs.getInt("id_lista") + "\n"
                        + "Nome: " + rs.getString("nome") + "\n"
                        + "Concluída em: " + rs.getString("data_formatada"));
            }

            if (getActivity() != null) {
                adaptador = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dadosHistorico);
                listHistorico.setAdapter(adaptador);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao carregar histórico", Toast.LENGTH_SHORT).show();
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
}
