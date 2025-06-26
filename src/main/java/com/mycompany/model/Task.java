package com.mycompany.model;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;

/**
 *
 * @author ASUS
 */
public class Task {
    private ObjectId id;
    private String encryptedDescription; // Deskripsi yang terenkripsi
    private LocalDateTime dueDate;
    private boolean completed;

    // Transient agar tidak disimpan di DB
    private transient String description; 

    // Constructor kosong wajib untuk MongoDB POJO Codec
    public Task() {}

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getEncryptedDescription() { return encryptedDescription; }
    public void setEncryptedDescription(String encryptedDescription) { this.encryptedDescription = encryptedDescription; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
