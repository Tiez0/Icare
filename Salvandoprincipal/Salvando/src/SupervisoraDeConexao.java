import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null) throw new Exception("Conexao ausente");
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
        } catch (Exception erro) {}

        try {
            synchronized(this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            for (;;) {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null) return;
                else if (comunicado instanceof PedidoDeValidacao) {
                    // Recebeu um CPF para validar
                    PedidoDeValidacao pedido = (PedidoDeValidacao) comunicado;

                    // Executa a validação
                    boolean ehValido = validarCPF(pedido.getCpf());

                    // Envia o resultado de volta
                    this.usuario.receba(new Resultado(ehValido));

                } else if (comunicado instanceof PedidoParaSair) {
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

    // Método auxiliar com a lógica padrão de validação de CPF
    private boolean validarCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica tamanho e se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Calculo do 1o. Digito Verificador
            int sm = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            int r = 11 - (sm % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(int i = 0; i < 10; i++) {
                int num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

        } catch (InputMismatchException erro) {
            return false;
        }
    }
}