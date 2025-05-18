package model;

import java.sql.Timestamp;

public class FavoriteItem {
    private int id;
    private int folderId;
    private int petId;
    private Timestamp createdAt;
    private String petType;
    private int count;
    private Timestamp lastUpdated;
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getFolderId() {
        return folderId;
    }
    
    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }
    
    public int getPetId() {
        return petId;
    }
    
    public void setPetId(int petId) {
        this.petId = petId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getPetType() {
        return petType;
    }
    
    public void setPetType(String petType) {
        this.petType = petType;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}