package org.example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 账单记录数据结构类
 * 实现Comparable接口
 *
 * @param type 1为收入，0为支出
 */
public record BillingRecord(String name, String date, double amount, int type) implements Comparable<BillingRecord> {

    /**
     * 重写比较接口
     *
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

    /**
     * 获取数据的年份和月份
     * @return yy-MM 23-06
     */
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
