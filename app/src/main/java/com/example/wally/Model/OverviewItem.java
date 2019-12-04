package com.example.wally.Model;

public class OverviewItem {
    private String category, type;
    private double amount;

    public OverviewItem(String category, double amount, String type) {
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
