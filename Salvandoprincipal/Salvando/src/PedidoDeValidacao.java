public class PedidoDeValidacao extends Comunicado {
    private String cpf;

    public PedidoDeValidacao(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return this.cpf;
    }
}