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
        try {
            // Inicializa o Parceiro (agora só precisa do Socket)
            this.usuario = new Parceiro(this.conexao);

            synchronized(this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            for (;;) {
                // Lê a mensagem de texto do cliente
                String mensagem = this.usuario.envie();

                if (mensagem == null) return;

                // --- LÓGICA DO PROTOCOLO ---

                // Se receber "VALIDAR:123..."
                if (mensagem.startsWith("VALIDAR:")) {
                    String cpf = mensagem.substring(8); // Pega o número depois dos dois pontos
                    boolean ehValido = validarCPF(cpf);

                    if (ehValido) {
                        this.usuario.receba("RESULTADO:VALIDO");
                    } else {
                        this.usuario.receba("RESULTADO:INVALIDO");
                    }
                }
                // Se receber pedido para sair
                else if (mensagem.equalsIgnoreCase("SAIR")) {
                    synchronized(this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                    return;
                }
                else {
                    this.usuario.receba("ERRO:Comando desconhecido");
                }
            }
        } catch (Exception erro) {
            try {
                if (this.usuario != null) this.usuario.adeus();
            } catch (Exception falha) {}
        }
    }

    // Mantive sua lógica original de validação de CPF intacta
    private boolean validarCPF(String cpf) {
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
        } catch (InputMismatchException erro) {
            return false;
        }
    }
}