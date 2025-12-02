import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoConnection {

    // TROQUE <db_password> pela senha real do usuário do Atlas (sem os < >)
    private static final String CONNECTION_STRING =
            "mongodb+srv://flaviodario2017_db_user:senha1@cluster0.3jthsk8.mongodb.net/?appName=Cluster0";

    // Nome do banco e da coleção
    private static final String DATABASE_NAME   = "Cadastro";
    private static final String COLLECTION_NAME = "usuarios";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static void conectar() {
        if (mongoClient == null) {
            ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();

            mongoClient = MongoClients.create(settings);
            database    = mongoClient.getDatabase(DATABASE_NAME);
        }
    }

    public static MongoCollection<Document> getUsuariosCollection() {
        if (database == null) {
            conectar();
        }
        return database.getCollection(COLLECTION_NAME);
    }
}
