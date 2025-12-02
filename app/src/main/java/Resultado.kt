import java.io.Serializable

class Resultado(
    private val valido: Boolean
) : Comunicado(), Serializable {

    fun isValido(): Boolean = valido

    override fun toString(): String =
        if (valido) "Válido" else "Inválido"
}
