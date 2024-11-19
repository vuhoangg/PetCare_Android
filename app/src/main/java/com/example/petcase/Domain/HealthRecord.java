package com.example.petcase.Domain;

public class HealthRecord {

    private String healthRecordId;
    private String petId;          // ID thú cưng liên quan
    private String date;
    private String description;
    private String veterinarian;

    public HealthRecord() {
    }

    public HealthRecord(String healthRecordId, String petId, String date, String description, String veterinarian) {
        this.healthRecordId = healthRecordId;
        this.petId = petId;
        this.date = date;
        this.description = description;
        this.veterinarian = veterinarian;
    }

    public String getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(String healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(String veterinarian) {
        this.veterinarian = veterinarian;
    }
}


