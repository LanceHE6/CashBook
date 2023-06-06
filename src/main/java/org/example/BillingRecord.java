package org.example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 账单记录数据结构类
 * 实现Comparable接口
 */
public class BillingRecord implements Comparable<BillingRecord>{
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

    /**
     * 重写比较方法
     * @param other the object to be compared.
     * @return 0
     */
    @Override
    public int compareTo(BillingRecord other) {
        DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
        try {
            Date thisDate = dateFormat.parse(this.date);
            Date otherDate = dateFormat.parse(other.date);
            return otherDate.compareTo(thisDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getYearMonth() {
        DateFormat inputFormat = new SimpleDateFormat("yy/MM/dd");
        DateFormat outputFormat = new SimpleDateFormat("yy-MM");
        try {
            Date parsedDate = inputFormat.parse(this.date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
