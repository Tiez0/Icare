import java.io.*;
import java.net.*;
import java.util.*;
import java.util.InputMismatchException;

public class SupervisoraDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null)  throw new Exception("Conexao ausente");
        if (usuarios == null) throw new Exception("Usuarios ausentes");
        this.conexao = conexao;
        this.usuarios = usuarios;
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
        } catch (Exception erro) {
            try {
                transmissor.close();
                receptor.close();
            } catch (Exception falha) {}
            return;
        }

        try {
            synchronized (this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            for (;;) {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null)
                    return;

                // NOVO: pedido de cadastro completo
                if (comunicado instanceof PedidoDeCadastro) {
                    PedidoDeCadastro pedido = (PedidoDeCadastro) comunicado;

                    String cpf = pedido.getCpf();

                    // 1) Valida o CPF
                    boolean ehValido = validarCPF(cpf);

                    // 2) Se válido, tenta salvar no MongoDB
                    if (ehValido) {
                        try {
                            CadastroRepository.salvarCadastro(
                                    pedido.getNome(),
                                    pedido.getDataNascimento(),
                                    pedido.getCpf(),
                                    pedido.getEmail(),
                                    pedido.getSenha()
                            );
                        } catch (Exception e) {
                            System.err.println("Erro ao salvar cadastro no MongoDB: " + e.getMessage());
                            // Se deu erro ao salvar, podemos considerar o cadastro como não concluído
                            ehValido = false;
                        }
                    } else {
                        System.out.println("CPF inválido, cadastro não será salvo: " + cpf);
                    }

                    // 3) Envia o resultado da operação para o cliente
                    this.usuario.receba(new Resultado(ehValido));
                }

                // Continua aceitando o fluxo antigo de só validar CPF, se você ainda usar isso
                else if (comunicado instanceof PedidoDeValidacao) {
                    PedidoDeValidacao pedido = (PedidoDeValidacao) comunicado;

                    boolean ehValido = validarCPF(pedido.getCpf());

                    this.usuario.receba(new Resultado(ehValido));
                }

                else if (comunicado instanceof PedidoParaSair) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                    return;
                }
            }
        } catch (Exception erro) {
            try {
                transmissor.close();
                receptor.close();
            } catch (Exception falha) {}
        }
    }

    // Método auxiliar com a lógica padrão de validação de CPF
    private boolean validarCPF(String cpf) {
        // Remove caracteres não numéricos
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica tamanho e se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Cálculo do 1o. dígito verificador
            int sm = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            int r = 11 - (sm % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            // Cálculo do 2o. dígito verificador
            sm = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            // Verifica se os dígitos calculados conferem com os informados
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

        } catch (InputMismatchException erro) {
            return false;
        } catch (Exception erro) {
            return false;
        }
    }
}
