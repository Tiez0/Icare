import java.net.*;
import java.io.*;

public class Cliente {
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao = null;
        try {
            String host = Cliente.HOST_PADRAO;
            int porta = Cliente.PORTA_PADRAO;
            if (args.length > 0) host = args[0];
            if (args.length == 2) porta = Integer.parseInt(args[1]);
            conexao = new Socket(host, porta);
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectOutputStream transmissor = null;
        try {
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectInputStream receptor = null;
        try {
            receptor = new ObjectInputStream(conexao.getInputStream());
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!\n");
            return;
        }

        Parceiro servidor = null;
        try {
            servidor = new Parceiro(conexao, receptor, transmissor);
        } catch (Exception erro) {
            System.err.println("Indique o servidor e a porta corretos!\n");
            return;
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        try {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
        } catch (Exception erro) {}

        tratadoraDeComunicadoDeDesligamento.start();

        // Loop principal do Cliente
        for(;;) {
            System.out.print("Digite o CPF para validar (ou 'sair' para terminar): ");
            String cpf = null;
            try {
                cpf = Teclado.getUmString();
            } catch (Exception erro) {
                System.err.println("Erro ao ler teclado!\n");
                continue;
            }

            if (cpf == null || cpf.equalsIgnoreCase("sair")) {
                break;
            }

            try {
                // Envia o pedido de validação
                servidor.receba(new PedidoDeValidacao(cpf));

                // Aguarda a resposta (Resultado)
                Comunicado comunicado = null;
                do {
                    comunicado = (Comunicado) servidor.espie();
                } while (!(comunicado instanceof Resultado));

                // Consome a mensagem do buffer e exibe
                Resultado resultado = (Resultado) servidor.envie();

                if (resultado.isValido()) {
                    System.out.println(">>> O CPF " + cpf + " é VÁLIDO.\n");
                } else {
                    System.out.println(">>> O CPF " + cpf + " é INVÁLIDO.\n");
                }

            } catch (Exception erro) {
                System.err.println("Erro de comunicacao com o servidor;");
                System.err.println("Tente novamente!");
                System.err.println("Caso o erro persista, termine o programa");
                System.err.println("e volte a tentar mais tarde!\n");
            }
        }

        try {
            servidor.receba(new PedidoParaSair());
        } catch (Exception erro) {}

        System.out.println("Obrigado por usar este programa!");
        System.exit(0);
    }
}