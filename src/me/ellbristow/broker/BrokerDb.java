package me.ellbristow.broker;

import java.io.File;
import java.sql.*;
import java.util.HashMap;

public class BrokerDb {

    private static Broker plugin;
    private Connection conn;
    private File sqlFile;
    private Statement statement;
    private HashMap<Integer, HashMap<String, Object>> rows = new HashMap<Integer, HashMap<String, Object>>();
    private int numRows = 0;
    
    public BrokerDb (Broker instance) {
        plugin = instance;
        sqlFile = new File("plugins/" + plugin.getDataFolder().getName() + File.separator + plugin.getName() + ".db");
    }
    
    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                return open();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public Connection open() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
                plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

                    @Override
                    public void run() {
                        close();
                    }

                }, 3600L, 3600L);
            }
            return conn;
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return null;
    }
    
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
    
    public boolean checkTable(String tableName) {
        DatabaseMetaData dbm;
        try {
            dbm = this.open().getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next())
                return true;
            else
                return false;
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
            return false;
        }
    }
    
    public boolean createTable(String tableName, String[] columns, String[] dims) {
        try {
            if (conn == null || conn.isClosed())
                open();
            statement = conn.createStatement();
            String query = "CREATE TABLE " + tableName + "(";
            for (int i = 0; i < columns.length; i++) {
                if (i!=0) {
                    query += ",";
                }
                query += columns[i] + " " + dims[i];
            }
            query += ")";
            statement.execute(query);
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return true;
    }
    
    public ResultSet query(String query) {
        try {
            if (conn == null || conn.isClosed())
                open();
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(query);
            return results;
        } catch (Exception e) {
            if (!e.getMessage().contains("not return ResultSet") || (e.getMessage().contains("not return ResultSet") && query.startsWith("SELECT"))) {
                plugin.getLogger().severe(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public HashMap<Integer, HashMap<String, Object>> select(String fields, String tableName, String where, String group, String order) {
        if ("".equals(fields) || fields == null) {
            fields = "*";
        }
        String query = "SELECT " + fields + " FROM " + tableName;
        try {
            if (conn == null || conn.isClosed())
                open();
            statement = conn.createStatement();
            if (!"".equals(where) && where != null) {
                query += " WHERE " + where;
            }
            if (!"".equals(group) && group != null) {
                query += " GROUP BY " + group;
            }
            if (!"".equals(order) && order != null) {
                query += " ORDER BY " + order;
            }
            rows.clear();
            numRows = 0;
            ResultSet results = statement.executeQuery(query);
            if (results != null) {
                int columns = results.getMetaData().getColumnCount();
                String columnNames = "";
                for (int i = 1; i <= columns; i++) {
                    if (!"".equals(columnNames)) {
                        columnNames += ",";
                    }
                    columnNames += results.getMetaData().getColumnName(i);
                }
                String[] columnArray = columnNames.split(",");
                numRows = 0;
                while (results.next()) {
                    HashMap<String, Object> thisColumn = new HashMap<String, Object>();
                    for (String columnName : columnArray) {
                        thisColumn.put(columnName, results.getObject(columnName));
                    }
                    rows.put(numRows, thisColumn);
                    numRows++;
                }
                results.close();
                return rows;
            } else {
                return null;
            }
        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean tableContainsColumn(String tableName, String columnName) {
        try {
            if (conn == null || conn.isClosed())
                open();
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT " + columnName + " FROM " + tableName + " LIMIT 1");
            if (rs == null) {
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("no such column: " + columnName)) {
                return false;
            }
            plugin.getLogger().severe(e.getMessage());
        }
        return true;
    }
    
    public void addColumn(String tableName, String columnDef) {
        try {
            if (conn == null || conn.isClosed())
                open();
            statement = conn.createStatement();
            statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnDef);
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
    
}
