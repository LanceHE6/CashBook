package org.example;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *  主界面 继承JFrame
 */
public class Home extends JFrame {
    private final JTextField txtProject;//项目名称
    private final JTextField txtAmount;//项目金额
    private final DefaultTableModel tableModel;//表格
    private final JTextField txtDate;//项目日期

    private final JLabel lblTotalIncome;
    private final JLabel lblTotalExpense;
    private final JLabel lblBalance;
    //账目类型 单选框(收入，支出)
    private final JRadioButton rdoIncome;
    private final JRadioButton rdoExpense;

    //数据库连接
    private SqliteManager sqliteManager = new SqliteManager("/data.db");

    public Home(int windowWidth, int widowHeight) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        super("记账本");//调用父类构造函数

        //数据库建表
        String createTable = "CREATE TABLE IF NOT EXISTS CashBook ( no  INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, name TEXT, amount REAL, type INTEGER);";
        sqliteManager.executeUpdate(createTable);

        //窗口属性设置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(400,300,windowWidth,widowHeight);
        setResizable(false);//不可拉伸窗口

        //创建各种组件
        JPanel panel = new JPanel(null);//总容器 绝对布局
        //设置容器边框
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //设置表格
        tableModel = new DefaultTableModel(new String[]{"日期", "项目", "金额", "分类"}, 0);
        // 查询数据库并获取账单信息
        List<BillingRecord> billingRecords = sqliteManager.queryBillingRecords();
        //填充账单信息
        for (BillingRecord record : billingRecords) {
            Object[] rowData = {record.getDate(), record.getName(), record.getAmount(), record.getType()==1?"收入":"支出"};
            tableModel.addRow(rowData);
        }
        //依据表头创建表格
        JTable table = new JTable(tableModel);
        //表格能够滑动
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20,40,windowWidth-50,widowHeight-175);
        panel.add(scrollPane);

        JLabel totalInProject = new JLabel("总收入：");
        totalInProject.setBounds(220,400,50,35);
        panel.add(totalInProject);
        //总收入
        double totalIncome = sqliteManager.getTotalAmount(1);
        lblTotalIncome = new JLabel(""+ totalIncome+"元");
        lblTotalIncome.setBounds(270,400,100,35);
        panel.add(lblTotalIncome);

        JLabel totalExProject = new JLabel("总支出：");
        totalExProject.setBounds(360,400,50,35);
        panel.add(totalExProject);
        //总支出
        double totalExpense = sqliteManager.getTotalAmount(0);
        lblTotalExpense = new JLabel(""+totalExpense+"元");
        lblTotalExpense.setBounds(410,400,100,35);
        panel.add(lblTotalExpense);

        JLabel balanceProject = new JLabel("结余：");
        balanceProject.setBounds(490,400,40,35);
        panel.add(balanceProject);
        //结余
        lblBalance = new JLabel(""+(totalIncome-totalExpense)+"元");
        lblBalance.setBounds(530,400,100,35);
        panel.add(lblBalance);

        //创建输入组件及其提醒标签
        JLabel lblProject = new JLabel("账单项目：");
        lblProject.setBounds(20,440,100,35);
        panel.add(lblProject);
        txtProject = new JTextField();
        txtProject.setBounds(80,445,100,25);
        panel.add(txtProject);

        JLabel lblAmount = new JLabel("账单金额：");
        lblAmount.setBounds(190,440,100,35);
        panel.add(lblAmount);
        txtAmount = new JTextField();
        txtAmount.setBounds(250,445,100,25);
        panel.add(txtAmount);

        JLabel dateProject = new JLabel("日期：");
        dateProject.setBounds(360,440,100,35);
        panel.add(dateProject);
        txtDate = new JTextField();
        txtDate.setBounds(395,445,100,25);
        txtDate.setToolTipText("xx/xx");
        panel.add(txtDate);

        JLabel lblCategory = new JLabel("项目分类：");
        lblCategory.setBounds(510,440,100,35);
        panel.add(lblCategory);

        rdoIncome = new JRadioButton("收入");
        rdoIncome.setBounds(570,440,50,35);
        panel.add(rdoIncome);
        rdoExpense = new JRadioButton("支出");
        rdoExpense.setBounds(620,440,50,35);
        panel.add(rdoExpense);
        // 设置单选框组
        ButtonGroup categoryGroup = new ButtonGroup();
        categoryGroup.add(rdoIncome);
        categoryGroup.add(rdoExpense);

        //功能按钮
        JButton btnSubmit = new JButton("提交项目");
        btnSubmit.setBounds(680,440,90,30);
        panel.add(btnSubmit);

        JButton btnDelete = new JButton("删除项目");
        btnDelete.setBounds(20,5,100,30);
        panel.add(btnDelete);

        JButton btnExport = new JButton("导出Excel文件");
        btnExport.setBounds(windowWidth-180,5,150,30);
        panel.add(btnExport);

        JButton btnChart = new JButton("查看图表");
        btnChart.setBounds(windowWidth-300,5,100,30);
        panel.add(btnChart);

        add(panel);

        setVisible(true);
        UIManager.setLookAndFeel(new NimbusLookAndFeel());//主題

        //添加事件监听器
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取面板上的值
                String project = txtProject.getText();
                String amountString = txtAmount.getText();
                String dateString = txtDate.getText();
                boolean isIncome = rdoIncome.isSelected();
                boolean isExpense = rdoExpense.isSelected();
                //空值判断
                if(project.isEmpty() || amountString.isEmpty() || (!isIncome&&!isExpense)){
                    JOptionPane.showMessageDialog(null,"请填写完整数据");
                    return;
                }
                if(!isNumber(amountString)){
                    JOptionPane.showMessageDialog(null,"非法金额输入");
                    return;
                }
                if(dateString.isEmpty()){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                    dateString = dateFormat.format(new Date());
                }else {
                    dateString = convertDateFormat(dateString);
                    if (dateString == null) {
                        JOptionPane.showMessageDialog(null, "日期格式不符合要求");
                        return;
                    }
                }
                try {
                    // 将金额字符串转换为浮点数
                    double amount = Double.parseDouble(amountString);
                    // 根据选择的收入或支出设置类型值
                    int type = isIncome ? 1 : 0;

                    // 插入数据到数据库中
                    BillingRecord billingRecord = new BillingRecord(project,dateString,amount,type);
                    sqliteManager.insertBillingRecord(billingRecord);

                    // 清空输入框内容
                    txtProject.setText("");
                    txtAmount.setText("");
                    txtDate.setText("");
                    categoryGroup.clearSelection();

                   // 刷新表格数据
                    refreshData();

                    //JOptionPane.showMessageDialog(null, "项目提交成功");
                } catch (NumberFormatException | SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "提交项目时出错: " + ex.getMessage());
                }
            }


        });
    }

    /**
     * 判断字符串是否为数字
     * @param str 目标字符串
     * @return boolean
     */
    private static boolean isNumber(String str){

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;


    }

    /**
     * 将用户输入的字符串格式化，非指定格式则返回null
     * @param dateString 目标字符串
     * @return xx/xx
     */
    private static String convertDateFormat(String dateString) {
        try {

            // 拆分用户输入的日期
            String[] parts = dateString.split("/");
            int monthValue = Integer.parseInt(parts[0]);
            int dayValue = Integer.parseInt(parts[1]);

            // 检查月份和日期是否在有效范围内
            if (monthValue < 1 || monthValue > 12 || dayValue < 1 || dayValue > 31) {
                return null; // 无效的日期
            }

            // 构建日期字符串
            return String.format("%02d/%02d", monthValue, dayValue);
        } catch (NumberFormatException e) {
            return null; // 格式错误
        }
    }

    /**
     * 刷新数据
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void refreshData() throws SQLException, ClassNotFoundException {
        //清空表格数据
        tableModel.setRowCount(0);
        // 查询数据库并获取账单信息
        List<BillingRecord> billingRecords = sqliteManager.queryBillingRecords();
        //填充账单信息
        for (BillingRecord record : billingRecords) {
            Object[] rowData = {record.getDate(), record.getName(), record.getAmount(), record.getType()==1?"收入":"支出"};
            tableModel.addRow(rowData);
        }

        //更新统计信息
        double totalIncome = sqliteManager.getTotalAmount(1);
        double totalExpense = sqliteManager.getTotalAmount(0);

        lblTotalIncome.setText(""+totalIncome+"元");
        lblTotalExpense.setText(""+totalExpense+"元");
        lblBalance.setText(""+(totalIncome-totalExpense)+"元");

    }
}
