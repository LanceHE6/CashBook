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
 * sqlite帮助类，直接创建该类示例，并调用相应的借口即可对sqlite数据库进行操作
 * 本类基于 sqlite jdbc v56
 */
public class SqliteHelper {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private final String  dbFilePath;

    /**
     * 构造函数
     * @param dbFilePath sqlite db 文件路径
     */
    public SqliteHelper(String dbFilePath) throws ClassNotFoundException, SQLException {
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
        // 注意：此处有巨坑，如果后面的 dbFilePath 路径太深或者名称太长，则建立连接会失败
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

    /**
     * 执行数据库更新 sql List
     * @param sqls sql列表
     */
    public void executeUpdate(List<String> sqls) throws SQLException, ClassNotFoundException {
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
     * 是否自动提交事务
     */
    public void setAutoCommit(Boolean status) throws SQLException {
        connection.setAutoCommit(status);
    }
}