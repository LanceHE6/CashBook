package org.example;

import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 *  主界面 继承JFrame
 */
public class Home extends JFrame {
    /**
     * 重写JTable类方法使Table不能直接编辑
     */
    static class  MyTable extends JTable{
        public MyTable(DefaultTableModel tableModel){
            super(tableModel);
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex){
            return false;
        }

    }
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
    private final SqliteManager sqliteManager = new SqliteManager( System.getProperty("user.dir") + "/data/data.db");  // 数据库文件的相对路径);

    public Home() throws ClassNotFoundException, SQLException {
        super("记账本");//调用父类构造函数
        Utils utils = new Utils();//工具类
        int windowWidth=800;
        int widowHeight=550;
        //图标
        ImageIcon imageIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/homeIcon.png");
        setIconImage(imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));

        //数据库建表
        String createTable = "CREATE TABLE IF NOT EXISTS CashBook ( no  INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, name TEXT, amount REAL, type INTEGER);";
        sqliteManager.executeUpdate(createTable);

        //窗口属性设置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(400,300,windowWidth,widowHeight);
        setResizable(false);//不可拉伸窗口

        //菜单栏
        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenu windowMenu = new JMenu("窗口");
        jMenuBar.add(fileMenu);
        jMenuBar.add(windowMenu);
        fileMenu.setFont(new Font("simhei",Font.BOLD,14));
        windowMenu.setFont(new Font("simhei",Font.BOLD,14));
        JMenuItem exportExcelAll = new JMenuItem("导出全部为Excel文件");
        exportExcelAll.setFont(new Font("simhei",Font.PLAIN,14));
        JMenuItem exportExcelSelected = new JMenuItem("导出选中为Excel文件");
        exportExcelSelected.setFont(new Font("simhei",Font.PLAIN,14));
        JMenuItem importExcel = new JMenuItem("从Excel文件导入");
        importExcel.setFont(new Font("simhei",Font.PLAIN,14));
        JMenuItem chart = new JMenuItem("查看图表");
        chart.setFont(new Font("simhei",Font.PLAIN,14));

        fileMenu.add(importExcel);
        fileMenu.add(exportExcelAll);
        fileMenu.add(exportExcelSelected);
        windowMenu.add(chart);

        setJMenuBar(jMenuBar);

        //创建各种组件
        JPanel panel = new JPanel(null);//总容器 绝对布局
        //设置容器边框
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //设置表格
        tableModel = new DefaultTableModel(new String[]{"日期", "项目", "金额", "分类"}, 0);
        // 查询数据库并获取账单信息
        List<BillingRecord> billingRecords = sqliteManager.queryBillingRecords();
        //依据日期排序
        Collections.sort(billingRecords);
        //填充表格账单信息
        for (BillingRecord record : billingRecords) {
            Object[] rowData = {record.date(), record.name(), record.amount(), record.type()==1?"收入":"支出"};
            tableModel.addRow(rowData);
        }
        //依据表头创建表格
        MyTable table = new MyTable(tableModel);

        //滑动表格
        JScrollPane scrollPane = new JScrollPane(table);
        //设置表头不能移动
        table.getTableHeader().setReorderingAllowed(false);
        scrollPane.setBounds(20,40,windowWidth-50,widowHeight-200);
        panel.add(scrollPane);

        //筛选框
        JLabel filterProject = new JLabel("筛选:");
        filterProject.setBounds(20,0,50,35);
        panel.add(filterProject);

        JTableFilterTextField jTableFilterTextField = new JTableFilterTextField();
        jTableFilterTextField.setTable(table);
        jTableFilterTextField.setBounds(55,5,100,24);
        panel.add(jTableFilterTextField);

        //总收入
        JLabel totalInProject = new JLabel("总收入：");
        totalInProject.setBounds(220,400,50,35);
        panel.add(totalInProject);

        double totalIncome = sqliteManager.getTotalAmount(1);
        lblTotalIncome = new JLabel(""+ totalIncome+"元");
        lblTotalIncome.setBounds(270,400,100,35);
        panel.add(lblTotalIncome);

        //总支出
        JLabel totalExProject = new JLabel("总支出：");
        totalExProject.setBounds(360,400,50,35);
        panel.add(totalExProject);

        double totalExpense = sqliteManager.getTotalAmount(0);
        lblTotalExpense = new JLabel(""+totalExpense+"元");
        lblTotalExpense.setBounds(410,400,100,35);
        panel.add(lblTotalExpense);

        //结余
        JLabel balanceProject = new JLabel("结余：");
        balanceProject.setBounds(490,400,40,35);
        panel.add(balanceProject);

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
        txtDate.setToolTipText("xxxx/xx/xx");
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
        btnDelete.setBounds(668,5,100,30);
        panel.add(btnDelete);


        add(panel);

        setVisible(true);
        //UIManager.setLookAndFeel(new NimbusLookAndFeel());//主題

        //添加事件监听器
        btnSubmit.addActionListener(e -> {
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
            //金额格式判断
            if(!utils.isNumber(amountString) || (amountString.indexOf(".")>0&&amountString.length()-(amountString.indexOf(".")+1) > 2)){
                JOptionPane.showMessageDialog(null,"金额格式不正确");
                return;
            }
            if(dateString.isEmpty()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
                dateString = dateFormat.format(new Date());
            }else {
                dateString = utils.convertDateFormat(dateString);
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

            } catch (NumberFormatException | SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "提交项目时出错: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int index1 = table.getSelectedRow();
            int[] index2 = table.getSelectedRows();//获取选中的行
            if(index1==-1){
                JOptionPane.showMessageDialog(null, "请选中需要删除的行");
                return;
            }
            //如果选中多行
            if(index2.length>1){
                List<BillingRecord> deleteRecords = new ArrayList<>();
                for(int row:index2){
                    String name = (String) table.getValueAt(row,1);
                    String date = (String) table.getValueAt(row,0);
                    double amount = (double) table.getValueAt(row, 2);
                    int type = table.getValueAt(index1,3)=="收入"?1:0;
                    BillingRecord record = new BillingRecord(name,date,amount,type);
                    deleteRecords.add(record);
                }
                try {

                    int userChoose = JOptionPane.showConfirmDialog(null,"是否删除选中的行","删除项目",JOptionPane.YES_NO_OPTION);
                    if(userChoose == JOptionPane.YES_OPTION){
                        sqliteManager.deleteRecords(deleteRecords);
                    }else {
                        return;
                    }

                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null,"删除失败");
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null,"删除成功");
                try {
                    refreshData();
                    return;
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }

            String name = (String) table.getValueAt(index1,1);
            String date = (String) table.getValueAt(index1,0);
            double amount = (double) table.getValueAt(index1, 2);
            int type = table.getValueAt(index1,3)=="收入"?1:0;
            BillingRecord deleteRecord = new BillingRecord(name, date, amount, type);

            int userChoose = JOptionPane.showConfirmDialog(null,"是否删除选中的行","删除项目",JOptionPane.YES_NO_OPTION);
            if(userChoose == JOptionPane.YES_OPTION){
                try {
                    sqliteManager.deleteRecord(deleteRecord);

                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null,"删除失败");
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null,"删除成功");
                try {
                    refreshData();
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });

        exportExcelAll.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showDialog(null,"选择为Excel保存路径");
            if(result == JFileChooser.APPROVE_OPTION) {
                String savePath = String.valueOf(fileChooser.getSelectedFile());
                try {
                    writeExcel(savePath, sqliteManager.queryBillingRecords());
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exportExcelSelected.addActionListener(e -> {
            int[] index = table.getSelectedRows();//获取选中的行
            //如果选中多行
            if (index.length == 0) {
                JOptionPane.showMessageDialog(null, "请选中需要导出的行");
            }else {
                List<BillingRecord> exportRecords = new ArrayList<>();
                for (int row : index) {
                    String name = (String) table.getValueAt(row, 1);
                    String date = (String) table.getValueAt(row, 0);
                    double amount = (double) table.getValueAt(row, 2);
                    int type = table.getValueAt(row, 3) == "收入" ? 1 : 0;
                    BillingRecord record = new BillingRecord(name, date, amount, type);
                    exportRecords.add(record);
                }
                //路径选择
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showDialog(null,"选择为Excel保存路径");
                if(result == JFileChooser.APPROVE_OPTION) {
                    String savePath = String.valueOf(fileChooser.getSelectedFile());
                    writeExcel(savePath, exportRecords);
                }
            }
        });

        chart.addActionListener(e -> {
            try {
                new ChartFrame(sqliteManager.queryBillingRecords());
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        importExcel.addActionListener(e -> {
            //路径选择
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //设置文件过滤器
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".xls");
                }
                @Override
                public String getDescription() {
                    return "Excel文件(*.xls)";
                }
            });
            int result = fileChooser.showDialog(null,"选择Excel文件");
            if(result == JFileChooser.APPROVE_OPTION) {
                String file = String.valueOf(fileChooser.getSelectedFile());
                try {
                    List<BillingRecord> exportRecords = new ArrayList<>();
                    FileInputStream fis = new FileInputStream(file);
                    //StringBuilder sb = new StringBuilder();
                    Workbook rwb = Workbook.getWorkbook(fis);
                    Sheet rs = rwb.getSheet(0);
                    for (int i = 1; i < rs.getRows(); i++) {
                        Cell[] cells = rs.getRow(i);

                        String date = cells[0].getContents();
                        String name = cells[1].getContents();
                        double amount = Double.parseDouble(cells[2].getContents());
                        int type = Objects.equals(cells[3].getContents(), "收入") ? 1 : 0;

                        exportRecords.add(new BillingRecord(name, date, amount, type));

                    }
                    sqliteManager.insertBillingRecords(exportRecords);
                } catch (IOException | BiffException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "导入失败:\n%s".formatted(ex));
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null, "导入成功");
                try {
                    refreshData();//刷新
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() throws SQLException, ClassNotFoundException {
        //清空表格数据
        tableModel.setRowCount(0);
        // 查询数据库并获取账单信息
        List<BillingRecord> billingRecords = sqliteManager.queryBillingRecords();
        // 排序
        Collections.sort(billingRecords);
        //填充账单信息
        for (BillingRecord record : billingRecords) {
            Object[] rowData = {record.date(), record.name(), record.amount(), record.type()==1?"收入":"支出"};
            tableModel.addRow(rowData);
        }

        //更新统计信息
        double totalIncome = sqliteManager.getTotalAmount(1);
        double totalExpense = sqliteManager.getTotalAmount(0);

        lblTotalIncome.setText(""+totalIncome+"元");
        lblTotalExpense.setText(""+totalExpense+"元");
        lblBalance.setText(""+(totalIncome-totalExpense)+"元");

    }

    /**
     * 写入Excel文件
     * @param savePath 文件保存路径
     */
    private void writeExcel(String savePath, List<BillingRecord> records){
        //开始写入excel,创建模型文件头
        String[] titleA = {"日期","项目","金额","类型"};
        //创建Excel文件，B库CD表文件
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-hhmm"));
        File fileA = new File(savePath+"\\"+nowTime+".xls");
        System.out.println(savePath+"/"+nowTime+".xls");
        if(fileA.exists()){
            //如果文件存在就删除
            //noinspection ResultOfMethodCallIgnored
            fileA.delete();
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            fileA.createNewFile();
            //创建工作簿
            WritableWorkbook workbookA = Workbook.createWorkbook(fileA);
            //创建sheet
            WritableSheet sheetA = workbookA.createSheet("sheet1", 0);
            Label labelA;
            //设置列名
            for (int i = 0; i < titleA.length; i++) {
                labelA = new Label(i,0,titleA[i]);
                sheetA.addCell(labelA);
            }
            int column = 1;
            for (BillingRecord record: records) {
                labelA = new Label(0,column,record.date());
                sheetA.addCell(labelA);
                labelA = new Label(1,column,record.name());
                sheetA.addCell(labelA);
                labelA = new Label(2,column,String.valueOf(record.amount()));
                sheetA.addCell(labelA);
                labelA = new Label(3,column,record.type() == 1? "收入": "支出");
                sheetA.addCell(labelA);
                column ++;
            }
            workbookA.write();    //写入数据
            workbookA.close();  //关闭连接
            JOptionPane.showMessageDialog(null, "导出%s成功".formatted(savePath+"\\"+nowTime+".xls"));
            System.out.println("成功写入文件");

        } catch (Exception e) {
            System.out.printf("写入文件失败：%s",e);
        }
    }
}
