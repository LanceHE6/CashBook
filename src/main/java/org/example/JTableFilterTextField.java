package org.example;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * 表格过滤文本框，过滤表格中的数据，显示与本文本框中匹配的文字
 */
public class JTableFilterTextField extends JTextField implements KeyListener {
    private JTable mTable;

    private final ImageIcon icon;

    public JTableFilterTextField() {
        this.addKeyListener(this);
        //获取当前路径下的图片
        icon = new ImageIcon(System.getProperty("user.dir") + "/data/searchIcon.png");
        Insets insets = new Insets(0, 24, 0, 0);
        //设置文本输入距左边24
        this.setMargin(insets);
    }

    /**
     * 设置需要过滤的表格
     * @param table JTable
     */
    public void setTable(JTable table) {
        this.mTable = table;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(mTable == null) {
            return;
        }

        TableRowSorter<TableModel> sorter;
        sorter = (TableRowSorter<TableModel>) mTable.getRowSorter();
        if(sorter == null) {
            sorter = new TableRowSorter<>(mTable.getModel());
            mTable.setRowSorter(sorter);
        }

        String text = this.getText();

        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //设置RowFilter 用于从模型中过滤条目，使得这些条目不会在视图中显示
            sorter.setRowFilter(RowFilter.regexFilter(text));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        int Height = this.getHeight();
        //在文本框中画上之前图片
        icon.paintIcon(this, g, (insets.left - iconWidth)/2, (Height - iconHeight) / 2);
    }
}
