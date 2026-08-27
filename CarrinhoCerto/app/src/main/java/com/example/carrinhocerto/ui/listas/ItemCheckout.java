package com.example.carrinhocerto.ui.listas;

public class ItemCheckout {
    private int id;
    private String nome;
    private String categoria;
    private double quantidade;
    private String unidade;
    private boolean marcado;

    public ItemCheckout(int id, String nome, String categoria, double quantidade, String unidade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.marcado = false;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public boolean isMarcado() { return marcado; }
    public void setMarcado(boolean marcado) { this.marcado = marcado; }
}