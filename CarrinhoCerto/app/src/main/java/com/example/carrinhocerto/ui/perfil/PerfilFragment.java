package com.example.carrinhocerto.ui.perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.carrinhocerto.Login;
import com.example.carrinhocerto.R;

public class PerfilFragment extends Fragment {

    private TextView tvNomeUsuarioPerfil;
    private Button btnLogoutPerfil;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        tvNomeUsuarioPerfil = view.findViewById(R.id.tvNomeUsuarioPerfil);
        btnLogoutPerfil = view.findViewById(R.id.btnLogoutPerfil);
        SharedPreferences prefs = requireActivity().getSharedPreferences("PrefsCarrinhoCerto", Context.MODE_PRIVATE);
        String nomeUsuario = prefs.getString("NOME_USUARIO", "Usuário Desconhecido");


        tvNomeUsuarioPerfil.setText(nomeUsuario);
        btnLogoutPerfil.setOnClickListener(v -> {
            prefs.edit().clear().apply();

            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}