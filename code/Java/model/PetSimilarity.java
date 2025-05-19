// PetSimilarity.java - 放入model包
package model;

public class PetSimilarity {
    private int id;
    private int petId1;
    private int petId2;
    private double similarity;
    private String createTime;
    private String updateTime;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId1() {
        return petId1;
    }

    public void setPetId1(int petId1) {
        this.petId1 = petId1;
    }

    public int getPetId2() {
        return petId2;
    }

    public void setPetId2(int petId2) {
        this.petId2 = petId2;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}