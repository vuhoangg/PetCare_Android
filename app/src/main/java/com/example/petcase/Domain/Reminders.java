package com.example.petcase.Domain;


public class Reminders {

    private String reminderId;
    private String petId;         // ID thú cưng liên quan
    private String date;
    private String time;
    private String message;

    public Reminders() {
    }

    public Reminders(String reminderId, String petId, String date, String time, String message) {
        this.reminderId = reminderId;
        this.petId = petId;
        this.date = date;
        this.time = time;
        this.message = message;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


