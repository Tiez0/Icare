import java.net.*;
import java.io.*;

public class Cliente {
    public static final String HOST_PADRAO  = "localhost";
    public static final int    PORTA_PADRAO = 3000;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao = null;
        Parceiro servidor = null;

        try {
            String host = HOST_PADRAO;
            int porta   = PORTA_PADRAO;

            if (args.length >= 1)
                host = args[0];

            if (args.length == 2)
                porta = Integer.parseInt(args[1]);

            // Abre conexão com o servidor
            conexao = new Socket(host, porta);

            ObjectOutputStream transmissor =
                    new ObjectOutputStream(conexao.getOutputStream());
            ObjectInputStream receptor =
                    new ObjectInputStream(conexao.getInputStream());

            servidor = new Parceiro(conexao, receptor, transmissor);

            // Thread que fica olhando se chegou ComunicadoDeDesligamento
            Thread tratadora = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadora.start();

            // Loop de cadastro
            for (;;) {
                System.out.println("\n=== CADASTRO DE USUÁRIO ===");

                System.out.print("Nome completo (ou 'sair' para encerrar): ");
                String nome = Teclado.getUmString();
                if (nome == null || nome.equalsIgnoreCase("sair"))
                    break;

                System.out.print("Data de nascimento (dd/mm/aaaa): ");
                String dataNascimento = Teclado.getUmString();

                System.out.print("CPF (apenas números): ");
                String cpf = Teclado.getUmString();

                System.out.print("E-mail: ");
                String email = Teclado.getUmString();

                System.out.print("Senha: ");
                String senha = Teclado.getUmString();

                try {
                    // Envia o pedido de CADASTRO (com todos os campos)
                    servidor.receba(new PedidoDeCadastro(
                            nome,
                            dataNascimento,
                            cpf,
                            email,
                            senha
                    ));

                    // Aguarda a resposta (Resultado) – mesmo padrão que você já usava
                    Comunicado comunicado;
                    do {
                        comunicado = servidor.espie();
                    } while (!(comunicado instanceof Resultado));

                    // Consome a mensagem do buffer e exibe
                    Resultado resultado = (Resultado) servidor.envie();

                    if (resultado.isValido()) {
                        System.out.println("\n>>> Cadastro realizado com SUCESSO!");
                        System.out.println(">>> CPF válido e salvo no MongoDB.\n");
                    } else {
                        System.out.println("\n>>> CPF inválido OU erro ao salvar o cadastro.");
                        System.out.println(">>> Tente novamente.\n");
                    }
                } catch (Exception erro) {
                    System.err.println("Erro de comunicação com o servidor;");
                    System.err.println("Tente novamente!");
                    System.err.println("Caso o erro persista, termine o programa");
                    System.err.println("e volte a tentar mais tarde!\n");
                }
            }

            // Pede para sair educadamente
            try {
                servidor.receba(new PedidoParaSair());
            } catch (Exception erro) {}

            System.out.println("Obrigado por usar este programa!");
        } catch (Exception erro) {
            System.err.println("Erro ao conectar com o servidor:");
            System.err.println(erro.getMessage());
        } finally {
            if (servidor != null) {
                try {
                    servidor.adeus();
                } catch (Exception e) {}
            } else if (conexao != null) {
                try {
                    conexao.close();
                } catch (IOException e) {}
            }
        }

        System.exit(0);
    }
}
