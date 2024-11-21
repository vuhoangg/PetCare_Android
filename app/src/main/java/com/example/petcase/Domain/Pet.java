package com.example.petcase.Domain;

public class Pet {
    private String petId;
    private String name;
    private String birth;
    private String sex;
    private String imageUrl;
    private double weight;
    private String color;
    private String note;
    private String userId_FK; // Foreign Key liên kết với User

    // Constructor mặc định
    public Pet() {}

    // Constructor đầy đủ
    public Pet(String petId, String name, String birth, String sex, String imageUrl, double weight, String color, String note, String userId_FK) {
        this.petId = petId;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
        this.imageUrl = imageUrl;
        this.weight = weight;
        this.color = color;
        this.note = note;
        this.userId_FK = userId_FK;
    }

    // Getter và Setter cho các trường
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserId_FK() {
        return userId_FK;
    }

    public void setUserId_FK(String userId_FK) {
        this.userId_FK = userId_FK;
    }
}