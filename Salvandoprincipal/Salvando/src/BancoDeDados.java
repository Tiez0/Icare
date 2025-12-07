import com.example.icare.model.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class BancoDeDados {
    private static final String CONNECTION_STRING = "mongodb+srv://flaviodario2017_db_user:Senha1@cluster0.3jthsk8.mongodb.net/?appName=Cluster0";
    private static final String DB_NAME = "iCare";

    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> usuarios;

    public BancoDeDados() {
        try {
            this.client = MongoClients.create(CONNECTION_STRING);
            this.db = client.getDatabase(DB_NAME);
            this.usuarios = db.getCollection("usuarios");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
        }
    }

    public void salvarUsuario(String nome, String cpf, String email, String senha) {
        Document doc = new Document("nome", nome)
                .append("cpf", cpf)
                .append("email", email)
                .append("senha", senha);
        this.usuarios.insertOne(doc);
    }

    public boolean validarLogin(String email, String senha) {
        Document query = new Document("email", email).append("senha", senha);
        Document found = this.usuarios.find(query).first();
        return found != null;
    }
}