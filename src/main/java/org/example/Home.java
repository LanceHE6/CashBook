package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.eltima.components.ui.DatePicker;

/**
 *  主界面 继承JFrame
 */
public class Home extends JFrame {
    private final int windowWidth = 800;
    private final int widowHeight = 525;
    private JLabel lblTotalIncome;//总收入
    private JLabel lblTotalExpense;//总支出
    private JLabel lblBalance;//结余
    private JTable table;//表格
    private DefaultTableModel tableModel;//表头
    private JTextField txtProject;//项目名称
    private JTextField txtAmount;//项目金额

    private JTextField date;//项目日期
    //账目类型 单选框(收入，支出)
    private JRadioButton rdoIncome;
    private JRadioButton rdoExpense;

    public Home() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super("记账本");//调用父类构造函数
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300,500,windowWidth,widowHeight);

        //创建各种组件
        JPanel panel = new JPanel(null);//总容器 绝对布局
        //设置容器边框
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //表头
        tableModel = new DefaultTableModel(new String[]{"日期", "项目", "金额", "分类"}, 0);
        //依据表头创建表格
        table = new JTable(tableModel);
        //表格能够滑动
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20,40,windowWidth-50,widowHeight-175);
        panel.add(scrollPane);

        JLabel totalInProject = new JLabel("总收入：");
        totalInProject.setBounds(220,400,50,35);
        panel.add(totalInProject);
        lblTotalIncome = new JLabel("5000元");
        lblTotalIncome.setBounds(270,400,100,35);
        panel.add(lblTotalIncome);

        JLabel totalExProject = new JLabel("总支出：");
        totalExProject.setBounds(360,400,50,35);
        panel.add(totalExProject);
        lblTotalExpense = new JLabel("10000元");
        lblTotalExpense.setBounds(410,400,100,35);
        panel.add(lblTotalExpense);

        JLabel balanceProject = new JLabel("结余：");
        balanceProject.setBounds(490,400,40,35);
        panel.add(balanceProject);
        lblBalance = new JLabel("5000元");
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
        date = new JTextField();
        date.setBounds(395,445,100,25);
        panel.add(date);

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
    }
}
