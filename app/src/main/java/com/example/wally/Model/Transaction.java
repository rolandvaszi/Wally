package com.example.wally.Model;

public class Transaction {
    private String type, date, category, comment, id;
    private double amount;

    public Transaction(){}

    public Transaction(String type, String date, String category, double amount, String comment, String id) {
        this.type = type;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.comment = comment;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public void setType(String type) {
        this.type = type;
    }
}
