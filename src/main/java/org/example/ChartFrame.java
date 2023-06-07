package org.example;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ChartFrame extends JFrame{

    public ChartFrame(List<BillingRecord> billingRecords) {
        setTitle("查看图表");
        setBounds(400, 300, 600,500);
        //图标
        ImageIcon imageIcon = new ImageIcon(System.getProperty("user.dir") + "/data/chartIcon.png");
        setIconImage(imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));

        Utils utils = new Utils();
        Map<String, List<BillingRecord>> groupedRecords = utils.recordsGrouping(billingRecords);
        //修改图标中文字体，否则会乱码
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");     //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));     //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));     //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));     //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i = 1;
        for (Map.Entry<String, List<BillingRecord>> entry : groupedRecords.entrySet()){
            if (i > 4){
                break;
            }
            String yearMonth = entry.getKey();
            List<BillingRecord> records = entry.getValue();
            double incomeTotal = 0.0;
            double expenseTotal = 0.0;

            for (BillingRecord record : records) {
                if (record.type() == 1) {
                    incomeTotal += record.amount();
                } else {
                    expenseTotal += record.amount();
                }
            }
            dataset.addValue(incomeTotal, "收入", yearMonth);
            dataset.addValue(expenseTotal, "支出", yearMonth);
            i ++ ;
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                "收入支出柱状图", // 图表标题
                "月份", // 横轴标签
                "金额", // 纵轴标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true, // 是否显示图例
                true, // 是否生成工具提示
                false // 是否生成URL链接
        );

        // 自定义图表外观
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN); // 设置收入柱状的颜色
        renderer.setSeriesPaint(1, Color.RED); // 设置支出柱状的颜色

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 400));

        // 将图表面板添加到主窗口
        getContentPane().add(chartPanel, BorderLayout.CENTER);

        // 显示主窗口
        setVisible(true);
    }


}

