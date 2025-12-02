import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class CadastroRepository {

    // Salva TODOS os campos do cadastro no MongoDB
    public static void salvarCadastro(String nome,
                                      String dataNascimento,
                                      String cpf,
                                      String email,
                                      String senha) {

        MongoCollection<Document> collection = MongoConnection.getUsuariosCollection();

        // (Opcional) Evitar CPF duplicado
        Document existente = collection.find(Filters.eq("cpf", cpf)).first();
        if (existente != null) {
            // Já existe um usuário com esse CPF, não insere de novo
            System.out.println("CPF " + cpf + " já cadastrado. Cadastro não será duplicado.");
            return;
        }

        Document doc = new Document("nome", nome)
                .append("dataNascimento", dataNascimento)
                .append("cpf", cpf)
                .append("email", email)
                .append("senha", senha);

        collection.insertOne(doc);
        System.out.println("Cadastro salvo no MongoDB com sucesso para o CPF: " + cpf);
    }
}
