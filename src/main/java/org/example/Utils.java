package org.example;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /**
     * 获取指定年份和月份的最大天数
     * @param yearValue 年份
     * @param monthValue 月份
     * @return 最大天数
     */
    private static int getMaxDayOfMonth(int yearValue,int monthValue){
        // 使用YearMonth类获取指定年份和月份的最大天数
        YearMonth yearMonth = YearMonth.of(yearValue, monthValue);
        return yearMonth.lengthOfMonth();
    }
    /**
     * 将用户输入的字符串格式化，非指定格式则返回null
     * @param dateString 目标字符串
     * @return xx/xx null 为格式错误
     */
    public String convertDateFormat(String dateString) {
        try {
            // 获取当前年份
            int currentYear = LocalDate.now().getYear();
            // 拆分用户输入的日期
            String[] parts = dateString.split("/");
            int monthValue;
            int dayValue;
            int yearValue;

            if (parts.length == 3) {
                // 用户提供了年份
                monthValue = Integer.parseInt(parts[1]);
                dayValue = Integer.parseInt(parts[2]);
                yearValue = Integer.parseInt(parts[0]);
            } else if (parts.length == 2) {
                // 用户未提供年份，使用当前年份
                monthValue = Integer.parseInt(parts[0]);
                dayValue = Integer.parseInt(parts[1]);
                yearValue = currentYear;
            } else {
                return null; // 格式错误
            }

            // 检查月份和日期是否在有效范围内
            if (yearValue > currentYear || monthValue < 1 || monthValue > 12 || dayValue < 1 || dayValue > getMaxDayOfMonth(yearValue, monthValue)) {
                return null; // 无效的日期
            }else if(yearValue == currentYear && monthValue == LocalDate.now().getMonthValue()){
                if(dayValue > LocalDate.now().getDayOfMonth()){
                    return null;
                }
            }

            // 使用用户提供的年份、月份和日期构建LocalDate对象
            LocalDate date = LocalDate.of(yearValue, monthValue, dayValue);

            // 格式化日期为字符串（"MM/dd"格式）
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
            return date.format(formatter);
        } catch (NumberFormatException e) {
            return null; // 格式错误
        }
    }

    /**
     * 判断字符串是否为数字
     * @param str 目标字符串
     * @return boolean
     */
    public boolean isNumber(String str){

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    public Map<String, List<BillingRecord>> recordsGrouping(List<BillingRecord> billingRecords) {
        Collections.sort(billingRecords);
        // 创建一个Map(字典)用于存储分组后的结果
        Map<String, List<BillingRecord>> groupedRecords = new HashMap<>();

        // 遍历BillingRecord对象列表，按年份和月份进行分组
        for (BillingRecord record : billingRecords) {
            String yearMonth = record.getYearMonth();
            List<BillingRecord> records = groupedRecords.getOrDefault(yearMonth, new ArrayList<>());
            records.add(record);
            groupedRecords.put(yearMonth, records);
        }

        return groupedRecords;
    }
}
