package org.example;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

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
     * 执行sql查询
     * @param sql sql select 语句
     * @param rse 结果集处理类对象
     * @return 查询结果
     */
    public <T> T executeQuery(String sql, ResultSetExtractor<T> rse) throws SQLException, ClassNotFoundException {
        try {
            resultSet = getStatement().executeQuery(sql);
            return rse.extractData(resultSet);
        } finally {
            destroyed();
        }
    }

    /**
     * 执行select查询，返回结果列表
     *
     * @param sql sql select 语句
     * @param rm 结果集的行数据处理类对象
     */
    public <T> List<T> executeQuery(String sql, RowMapper<T> rm) throws SQLException, ClassNotFoundException {
        List<T> rsList = new ArrayList<>();
        try {
            resultSet = getStatement().executeQuery(sql);
            while (resultSet.next()) {
                rsList.add(rm.mapRow(resultSet, resultSet.getRow()));
            }
        } finally {
            destroyed();
        }
        return rsList;
    }

    /**
     * 执行数据库更新sql语句
     * @return 更新行数
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        try {
            return getStatement().executeUpdate(sql);
        } finally {
            destroyed();
        }

    }

    /**
     * 执行多个sql更新语句
     */
    public void executeUpdate(String...sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
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
     * @throws SQLException
     */
    public void insertBillingRecord(BillingRecord record) throws SQLException {
        // 创建插入语句
        String insertQuery = "INSERT INTO CashBook (date, name, amount, type) VALUES (?, ?, ?, ?)";

        // 创建预处理语句对象
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            // 设置参数
            statement.setString(1, record.getDate());
            statement.setString(2, record.getName());
            statement.setDouble(3, record.getAmount());
            statement.setInt(4, record.getType());

            // 执行插入操作
            statement.executeUpdate();
        }
    }

    /**
     * 向数据库查询所有账单记录
     * @return 返回BillingRecord对象集合
     * @throws SQLException
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
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public double getTotalAmount(int type) throws SQLException, ClassNotFoundException {
        List<BillingRecord> records = queryBillingRecords();
        double totalAmount = 0;
        for(BillingRecord record: records){
            if(record.getType()==type){
                totalAmount += record.getAmount();
            }
        }
        return totalAmount;
    }
}