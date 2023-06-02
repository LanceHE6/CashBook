package org.example;

/**
 * 账单记录数据结构类
 */
public class BillingRecord {
    private String name;
    private String date;
    private double amount;
    private int type;//1为收入，0为支出

    public BillingRecord(String name, String date, double amount, int type){
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }
}
