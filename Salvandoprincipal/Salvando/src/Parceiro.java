import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class Parceiro {
    private Socket conexao;
    private BufferedReader receptor;
    private PrintWriter transmissor;
    private String proximaMensagem = null;
    private Semaphore mutEx = new Semaphore(1, true);

    public Parceiro(Socket conexao) throws Exception {
        if (conexao == null) throw new Exception("Conexao ausente");
        this.conexao = conexao;
        // Configura para ler e escrever Texto (String)
        this.receptor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        this.transmissor = new PrintWriter(conexao.getOutputStream(), true); // 'true' ativa o envio automático
    }

    public void receba(String msg) throws Exception {
        try {
            this.transmissor.println(msg); // Envia texto com quebra de linha
        } catch (Exception erro) {
            throw new Exception("Erro de transmissao");
        }
    }

    public String espie() throws Exception {
        try {
            this.mutEx.acquireUninterruptibly();
            if (this.proximaMensagem == null)
                this.proximaMensagem = this.receptor.readLine();
            this.mutEx.release();
            return this.proximaMensagem;
        } catch (Exception erro) {
            throw new Exception("Erro de recepcao");
        }
    }

    public String envie() throws Exception {
        try {
            if (this.proximaMensagem == null)
                this.proximaMensagem = this.receptor.readLine();
            String ret = this.proximaMensagem;
            this.proximaMensagem = null;
            return ret;
        } catch (Exception erro) {
            throw new Exception("Erro de recepcao");
        }
    }

    public void adeus() throws Exception {
        try {
            this.transmissor.close();
            this.receptor.close();
            this.conexao.close();
        } catch (Exception erro) {
            throw new Exception("Erro de desconexao");
        }
    }
}