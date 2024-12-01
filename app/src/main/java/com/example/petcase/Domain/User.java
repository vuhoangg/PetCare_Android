package com.example.petcase.Domain;

public class User {

    private String userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private String address;
    private String password; // Thêm trường password

    // Constructor mặc định
    public User(String userId, String name, String email, String phone, String address) {

    }

    // Constructor đầy đủ
    public User(String userId, String userName, String email, String phoneNumber, String address, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    // Getter và Setter cho các thuộc tính
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() { // Getter cho password
        return password;
    }

    public void setPassword(String password) { // Setter cho password
        this.password = password;
    }
}
