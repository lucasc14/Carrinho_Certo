package com.example.carrinhocerto.ui.listas;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carrinhocerto.ConexaoMySQL;
import com.example.carrinhocerto.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CriarListasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CriarListasFragment extends Fragment {

    EditText nomeCriarLista;
    Button btCriarLista;

    Connection con = null;
    PreparedStatement stmt = null;
    String sql;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CriarListasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CriarListaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CriarListasFragment newInstance(String param1, String param2) {
        CriarListasFragment fragment = new CriarListasFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_criar_listas, container, false);
       nomeCriarLista = view.findViewById(R.id.nomeCriarLista);
       btCriarLista = view.findViewById(R.id.btCriarLista);

       btCriarLista.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String nomeLista = nomeCriarLista.getText().toString().trim();
               if (nomeLista.isEmpty()) {
                   nomeCriarLista.setError("Por favor, digite um nome para a lista!");
                   return;
               }
               int usuarioLogadoId = 1;
               try {
                   con = ConexaoMySQL.conectar();
                   sql = "INSERT INTO lista (login_id, nome) VALUES (?, ?)";
                   PreparedStatement stmt = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                   stmt.setInt(1, usuarioLogadoId);
                   stmt.setString(2, nomeLista);
                   stmt.executeUpdate();

                   ResultSet rs = stmt.getGeneratedKeys();
                   int idDaNovaLista = -1;

                   if (rs.next()) {
                       idDaNovaLista = rs.getInt(1);
                   }

                   rs.close();
                   stmt.close();
                   con.close();

                   if (idDaNovaLista != -1) {
                       Bundle bundle = new Bundle();
                       bundle.putInt("ID_DA_LISTA", idDaNovaLista);
                       androidx.navigation.Navigation.findNavController(v)
                               .navigate(R.id.nav_editarListas, bundle);
                   } else {
                       Toast.makeText(getContext(), "Erro ao resgatar o ID da lista", Toast.LENGTH_SHORT).show();
                   }
               } catch (SQLException e) {
                   e.printStackTrace();
                   android.util.Log.e("ERRO_CRIAR_LISTA", "Falha: ", e);
                   Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
               }
           }
           });

       return view;


    }

    }

