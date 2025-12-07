import com.example.icare.model.*;
import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread {
    private ServerSocket pedido;
    private ArrayList<Parceiro> usuarios;

    public AceitadoraDeConexao(String porta, ArrayList<Parceiro> usuarios) throws Exception {
        if (porta == null) throw new Exception("Porta ausente");
        try {
            this.pedido = new ServerSocket(Integer.parseInt(porta));
        } catch (Exception erro) {
            throw new Exception("Porta invalida");
        }
        if (usuarios == null) throw new Exception("Usuarios ausentes");
        this.usuarios = usuarios;
    }

    public void run() {
        for (;;) {
            Socket conexao = null;
            try {
                conexao = this.pedido.accept();
            } catch (Exception erro) {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao = null;
            try {
                supervisoraDeConexao = new SupervisoraDeConexao(conexao, usuarios);
                // O .start() FICA DENTRO DO TRY! Se falhar a criação, não tenta iniciar.
                supervisoraDeConexao.start();
            } catch (Throwable erro) { // Use Throwable para pegar tudo
                System.err.println("ERRO NO SERVIDOR:");
                erro.printStackTrace(); // <--- Isso é essencial para você ver o erro do Banco!
                try { conexao.close(); } catch (Exception e) {}
            }
        }
    }
}