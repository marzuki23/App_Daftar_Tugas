package com.mycompany.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mycompany.db.MongoUtil;
import com.mycompany.model.Task;
import com.mycompany.service.CryptoService;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author ASUS
 */
public class TaskDAO implements GenericDAO<Task, ObjectId> {
    private final MongoCollection<Task> collection;

    public TaskDAO() {
        this.collection = MongoUtil.getDatabase().getCollection("tasks", Task.class);
    }

    @Override
    public void save(Task task) {
        try {
            // Enkripsi sebelum menyimpan
            String encryptedDesc = CryptoService.encrypt(task.getDescription());
            task.setEncryptedDescription(encryptedDesc);
            collection.insertOne(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task findById(ObjectId id) {
        // Implementasi findById jika diperlukan
        return null;
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        collection.find().forEach(task -> {
            try {
                // Dekripsi setelah mengambil dari DB
                String decryptedDesc = CryptoService.decrypt(task.getEncryptedDescription());
                task.setDescription(decryptedDesc);
                tasks.add(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return tasks;
    }

    @Override
    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public void update(Task task) {
        collection.replaceOne(Filters.eq("_id", task.getId()), task, new ReplaceOptions().upsert(false));
    }
}
