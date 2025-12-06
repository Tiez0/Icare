package com.example.icare.model;

public class PedidoDeCadastro extends Comunicado {
    private String nome;
    private String cpf;
    private String email;
    private String senha;

    public PedidoDeCadastro(String nome, String cpf, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}