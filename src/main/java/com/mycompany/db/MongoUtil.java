package com.mycompany.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 *
 * @author ASUS
 */
public class MongoUtil {
    private static MongoClient mongoClient;
    private static final String DATABASE_NAME = "uas_pemkom2";

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            // Ini adalah bagian SERIALIZATION: Mengkonfigurasi MongoDB driver
            // agar bisa otomatis mengubah POJO (Task.java) menjadi BSON dan sebaliknya.
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
                PojoCodecProvider.builder().automatic(true).build());
            
            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

            // Koneksi ke MongoDB lokal (operasi NETWORK)
            ConnectionString connectionString = new ConnectionString("mongodb+srv://idris:1234@cluster0.1l6mbxu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            
            MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
            
            mongoClient = MongoClients.create(clientSettings);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
