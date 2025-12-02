public class PedidoDeCadastro extends Comunicado {
    private String nome;
    private String dataNascimento;
    private String cpf;
    private String email;
    private String senha;

    public PedidoDeCadastro(String nome,
                            String dataNascimento,
                            String cpf,
                            String email,
                            String senha) {
        this.nome           = nome;
        this.dataNascimento = dataNascimento;
        this.cpf            = cpf;
        this.email          = email;
        this.senha          = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
