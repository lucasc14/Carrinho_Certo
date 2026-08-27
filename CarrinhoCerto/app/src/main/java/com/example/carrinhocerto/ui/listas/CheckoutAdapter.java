package com.example.carrinhocerto.ui.listas;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.carrinhocerto.R;
import java.util.List;

public class CheckoutAdapter extends ArrayAdapter<ItemCheckout> {

    public CheckoutAdapter(Context context, List<ItemCheckout> itens) {
        super(context, 0, itens);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkout, parent, false);
        }

        ItemCheckout itemAtual = getItem(position);
        TextView txtCategoria = convertView.findViewById(R.id.txtCategoriaHeader);
        TextView txtNome = convertView.findViewById(R.id.txtNomeItem);

        //  LÓGICA DE AGRUPAR POR CATEGORIA
        if (position == 0 || !itemAtual.getCategoria().equals(getItem(position - 1).getCategoria())) {
            txtCategoria.setVisibility(View.VISIBLE);
            txtCategoria.setText(itemAtual.getCategoria());
        } else {
            txtCategoria.setVisibility(View.GONE);
        }

        String textoFormatado = itemAtual.getNome() + " - " + itemAtual.getQuantidade() + " " + itemAtual.getUnidade();
        txtNome.setText(textoFormatado);

        if (itemAtual.isMarcado()) {
            // Risca o texto
            txtNome.setPaintFlags(txtNome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtNome.setTextColor(0xFF999999); // Deixa o texto cinza
        } else {
            // Tira o riscado do texto
            txtNome.setPaintFlags(txtNome.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            txtNome.setTextColor(0xFF000000); // Deixa o texto preto
        }

        return convertView;
    }}