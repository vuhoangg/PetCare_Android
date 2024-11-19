package com.example.petcase.Domain;

public class Pet {

    private String petId;
    private String petName;
    private String species;
    private String breed;
    private String age;
    private String ownerUserId;  // ID của chủ nhân thú cưng (liên kết với bảng User)

    public Pet() {
    }

    public Pet(String petId, String petName, String species, String breed, String age, String ownerUserId) {
        this.petId = petId;
        this.petName = petName;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.ownerUserId = ownerUserId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}

