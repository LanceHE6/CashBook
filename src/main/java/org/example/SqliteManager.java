package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * sqlite数据库管理类，直接创建该类示例，并调用相应的借口即可对sqlite数据库进行操作
 * 本类基于 sqlite jdbc v56
 */
public class SqliteManager {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private final String  dbFilePath;

    /**
     * 构造函数
     * @param dbFilePath sqlite db 文件路径
     */
    public SqliteManager(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
        System.out.println("SQLite connection established");
    }

    /**
     * 获取数据库连接
     * @param dbFilePath db文件路径
     * @return 数据库连接
     */
    public Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn;
        // 1、加载驱动
        Class.forName("org.sqlite.JDBC");
        // 2、建立连接
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }

    /**
     * 执行数据库更新sql语句
     */
    public void executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        try {
            getStatement().executeUpdate(sql);
        } finally {
            destroyed();
        }

    }


    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (null == connection) connection = getConnection(dbFilePath);
        return connection;
    }

    private Statement getStatement() throws SQLException, ClassNotFoundException {
        if (null == statement) statement = getConnection().createStatement();
        return statement;
    }

    /**
     * 数据库资源关闭和释放
     */
    public void destroyed() {
        try {
            if (null != connection) {
                connection.close();
                connection = null;
            }

            if (null != statement) {
                statement.close();
                statement = null;
            }

            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (SQLException e) {
            System.out.println("Sqlite数据库关闭时异常 "+ e);
        }
    }


    /**
     * 将单条记录插入进数据库中
     * @param record BillingRecord对象
     */
    public void insertBillingRecord(BillingRecord record) throws SQLException {
        // 创建插入语句
        String insertQuery = "INSERT INTO CashBook (date, name, amount, type) VALUES (?, ?, ?, ?)";

        // 创建预处理语句对象
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            // 设置参数
            statement.setString(1, record.date());
            statement.setString(2, record.name());
            statement.setDouble(3, record.amount());
            statement.setInt(4, record.type());

            // 执行插入操作
            statement.executeUpdate();
        }
    }
    /**
     * 将多条记录插入进数据库中
     * @param records BillingRecord对象列表
     */
    public void insertBillingRecords(List<BillingRecord> records) throws SQLException {
        for (BillingRecord record: records) {
            insertBillingRecord(record);
        }
    }

    /**
     * 向数据库查询所有账单记录
     * @return 返回BillingRecord对象集合
     */
    public List<BillingRecord> queryBillingRecords() throws SQLException, ClassNotFoundException {
        List<BillingRecord> records = new ArrayList<>();

        // 创建查询语句
        String selectQuery = "SELECT * FROM CashBook";

        // 创建查询语句的结果集
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            // 遍历结果集并将数据添加到列表中
            while (resultSet.next()) {
                String date = resultSet.getString("date");
                String name = resultSet.getString("name");
                double amount = resultSet.getDouble("amount");
                int type = resultSet.getInt("type");

                BillingRecord record = new BillingRecord(name, date, amount, type);
                records.add(record);
            }
        }

        return records;
    }

    /**
     * 获取一个分类下的所有账单金额
     * @param type 1收入 0支出
     * @return totalAmount
     *
     */
    public double getTotalAmount(int type) throws SQLException, ClassNotFoundException {
        List<BillingRecord> records = queryBillingRecords();
        double totalAmount = 0;
        for(BillingRecord record: records){
            if(record.type()==type){
                totalAmount += record.amount();
            }
        }
        return totalAmount;
    }

    /**
     * 删除单行数据
     * @param record BillingRecord
     *
     */
    public void deleteRecord(BillingRecord record) throws SQLException, ClassNotFoundException {
        String deleteSql = "Delete From CashBook Where date=\"%s\" and name=\"%s\" and amount=%f and type=%d;".formatted(record.date(),record.name(),record.amount(),record.type());
        executeUpdate(deleteSql);
    }

    /**
     * 删除多行数据
     * @param records List<BillingRecord>
     *
     */
    public void deleteRecords(List<BillingRecord> records) throws SQLException, ClassNotFoundException {
        for (BillingRecord record: records){
            String deleteSql = "Delete From CashBook Where date=\"%s\" and name=\"%s\" and amount=%f and type=%d;".formatted(record.date(),record.name(),record.amount(),record.type());
            executeUpdate(deleteSql);
        }
    }
}