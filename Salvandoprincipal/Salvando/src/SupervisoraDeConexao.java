import com.example.icare.model.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;
    private BancoDeDados banco;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null) throw new Exception("Conexao ausente");
        if (usuarios == null) throw new Exception("Usuarios ausentes");
        this.conexao = conexao;
        this.usuarios = usuarios;

        try {
            this.banco = new BancoDeDados();
            System.out.println("Supervisora: Banco de Dados instanciado.");
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Falha ao iniciar Banco de Dados na Supervisora.");
            e.printStackTrace();
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

            for (;;) {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null) return;


                if (comunicado instanceof PedidoDeCadastro) {
                    PedidoDeCadastro pedido = (PedidoDeCadastro) comunicado;
                    System.out.println("Recebido pedido de cadastro: " + pedido.getNome());

                    // A. Validação Matemática do CPF
                    if (!validarCPF(pedido.getCpf())) {
                        System.out.println("Recusado: CPF Inválido matematicamente.");
                        this.usuario.receba(new Resultado(false, "CPF Inválido!"));
                    }
                    else {
                        try {
                            if (this.banco == null) {
                                this.usuario.receba(new Resultado(false, "Erro Interno: Banco Offline"));
                            } else {

                                if (this.banco.isCpfEmUso(pedido.getCpf())) {
                                    System.out.println("Recusado: CPF já existe no banco.");
                                    this.usuario.receba(new Resultado(false, "Este CPF já está cadastrado!"));
                                }

                                else if (this.banco.isEmailEmUso(pedido.getEmail())) {
                                    System.out.println("Recusado: Email já existe no banco.");
                                    this.usuario.receba(new Resultado(false, "Este Email já está em uso!"));
                                }

                                else {
                                    this.banco.salvarUsuario(
                                            pedido.getNome(),
                                            pedido.getCpf(),
                                            pedido.getEmail(),
                                            pedido.getSenha()
                                    );
                                    System.out.println("Sucesso: Usuário salvo!");
                                    this.usuario.receba(new Resultado(true, "Cadastro realizado com sucesso!"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.usuario.receba(new Resultado(false, "Erro no Banco: " + e.getMessage()));
                        }
                    }
                }


                else if (comunicado instanceof PedidoDeLogin) {
                    PedidoDeLogin pedido = (PedidoDeLogin) comunicado;
                    System.out.println("Recebido pedido de login: " + pedido.getEmail());

                    try {
                        if (this.banco == null) {
                            this.usuario.receba(new Resultado(false, "Erro: Banco Offline"));
                        } else {
                            boolean sucesso = this.banco.validarLogin(pedido.getEmail(), pedido.getSenha());

                            if (sucesso) {
                                System.out.println("Login autorizado!");
                                this.usuario.receba(new Resultado(true, "Login realizado com sucesso!"));
                            } else {
                                System.out.println("Login negado.");
                                this.usuario.receba(new Resultado(false, "Email ou senha incorretos!"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.usuario.receba(new Resultado(false, "Erro no Servidor: " + e.getMessage()));
                    }
                }


                else if (comunicado instanceof PedidoDeValidacao) {
                    PedidoDeValidacao pedido = (PedidoDeValidacao) comunicado;
                    boolean ehValido = validarCPF(pedido.getCpf());
                    String msg = ehValido ? "CPF Válido" : "CPF Inválido";
                    this.usuario.receba(new Resultado(ehValido, msg));
                }


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
            sm = 0; peso = 11;
            for(int i = 0; i < 10; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char)(r + 48);
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (Exception erro) { return false; }
    }
}