import com.example.icare.model.*; // Importante para reconhecer PedidoDeCadastro, Resultado, etc.
import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;
    private BancoDeDados banco; // Variável para conectar ao MongoDB

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null) throw new Exception("Conexao ausente");
        if (usuarios == null) throw new Exception("Usuarios ausentes");
        this.conexao = conexao;
        this.usuarios = usuarios;

        // Tenta iniciar o banco de dados logo na criação
        try {
            this.banco = new BancoDeDados();
            System.out.println("Supervisora: Banco de Dados instanciado.");
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Falha ao iniciar Banco de Dados na Supervisora.");
            e.printStackTrace();
            // Não lançamos erro aqui para não derrubar a conexão TCP imediatamente,
            // mas o salvamento falhará depois de forma controlada.
        }
    }

    public void run() {
        ObjectOutputStream transmissor;
        try {
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
        } catch (Exception erro) {
            return;
        }

        ObjectInputStream receptor = null;
        try {
            receptor = new ObjectInputStream(this.conexao.getInputStream());
        } catch (Exception erro) {
            try {
                transmissor.close();
            } catch (Exception falha) {}
            return;
        }

        try {
            this.usuario = new Parceiro(this.conexao, receptor, transmissor);
        } catch (Exception erro) {}

        try {
            synchronized(this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            // Loop principal de comunicação
            for (;;) {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null) return;

                    // --- 1. TRATAMENTO DO CADASTRO ---
                else if (comunicado instanceof PedidoDeCadastro) {
                    PedidoDeCadastro pedido = (PedidoDeCadastro) comunicado;
                    System.out.println("Recebido pedido de cadastro para: " + pedido.getNome());

                    try {
                        if (this.banco == null) {
                            System.err.println("ERRO: A variável 'banco' é nula. A conexão com o Mongo falhou na inicialização?");
                            this.usuario.receba(new Resultado(false));
                        } else {
                            this.banco.salvarUsuario(
                                    pedido.getNome(),
                                    pedido.getCpf(),
                                    pedido.getEmail(),
                                    pedido.getSenha()
                            );
                            System.out.println("Sucesso: Usuário salvo no MongoDB!");
                            this.usuario.receba(new Resultado(true));
                        }
                    } catch (Exception e) {
                        System.err.println("ERRO AO TENTAR SALVAR NO MONGODB:");
                        // Este printStackTrace é o mais importante para você descobrir o erro real!
                        e.printStackTrace();
                        this.usuario.receba(new Resultado(false));
                    }
                }

                // --- 2. TRATAMENTO DA VALIDAÇÃO DE CPF ---
                else if (comunicado instanceof PedidoDeValidacao) {
                    PedidoDeValidacao pedido = (PedidoDeValidacao) comunicado;
                    boolean ehValido = validarCPF(pedido.getCpf());
                    this.usuario.receba(new Resultado(ehValido));
                }

                // --- 3. TRATAMENTO DE SAÍDA ---
                else if (comunicado instanceof PedidoParaSair) {
                    synchronized(this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                }
            }
        } catch (Exception erro) {
            try {
                transmissor.close();
                receptor.close();
            } catch (Exception falha) {}
            return;
        }
    }

    // Método auxiliar de validação de CPF
    private boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int sm = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            int r = 11 - (sm % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            sm = 0;
            peso = 11;
            for(int i = 0; i < 10; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (Exception erro) {
            return false;
        }
    }
}