import java.io.Serializable

class PedidoDeCadastro(
    val nome: String,
    val dataNascimento: String,
    val cpf: String,
    val email: String,
    val senha: String
) : Comunicado(), Serializable
