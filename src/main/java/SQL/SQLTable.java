package SQL;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SQLTable {
    Map<String, String> structure;
    String name;
    SQLServer server;
    private Plugin plugin;
    private Connection connection;
    private String database;
    private Boolean disableOnError;

    public SQLTable(HashMap<String, String> structure, SQLServer server, String name) {
        this.structure = structure;
        this.name = name;
        this.server = server;
        this.plugin = server.plugin;
        this.connection = server.getConnection();
        this.database = server.database;
        this.disableOnError = server.disableOnError;
    }

    private static String tabletypeString = "";
    public void createTable(HashMap<String, String> tabletype, String PrimaryKey) {
        tabletype.forEach((key, value) -> {
            tabletypeString += key + " " + value + ",";
        });
        tabletypeString = "(" + tabletypeString + "PRIMARY KEY (" + PrimaryKey + ")" + ")";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ? ?");
            ps.setString(1, this.database);
            ps.setString(2, tabletypeString);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                tabletypeString = "";
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        tabletypeString = "";
    }

    public void createTable(String PrimaryKey) {
        this.structure.forEach((key, value) -> {
            tabletypeString += key + " " + value + ",";
        });
        tabletypeString = "(" + tabletypeString + "PRIMARY KEY (" + PrimaryKey + ")" + ")";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ? ?");
            ps.setString(1, this.database);
            ps.setString(2, tabletypeString);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                tabletypeString = "";
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        tabletypeString = "";
    }

    public void addData(HashMap<String, String> data, String checkKey) {
        try {
            if (!exsits(checkKey, data.get(checkKey)) && Objects.equals(getKeys(), getKeys(data))) {
                PreparedStatement ps = connection.prepareStatement("IF EXISTS INSERT IGNORE INTO ? ? VALUES ?");
                ps.setString(1, this.database);
                ps.setString(2, getKeys());
                ps.setString(3, getValues(data));
                keys = "";
                values = "";
                ps.executeUpdate();
            } else {
                if (disableOnError) {
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public String getData(String key, String checkKey, String checkValue) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT ? FROM ? WHERE ?=?");
            ps.setString(1, key.toUpperCase());
            ps.setString(2, this.database);
            ps.setString(3, checkKey.toUpperCase());
            ps.setString(4, checkValue.toUpperCase());
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return results.getString(key.toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        return null;
    }
    
    public void deleteData(String key, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("IF EXISTS DELETE FROM ? WHERE ?=?");
            ps.setString(1, this.name);
            ps.setString(2, key);
            ps.setString(3, value);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void deleteTable(String table) {
        try {
            PreparedStatement ps = connection.prepareStatement("IF EXISTS DROP TABLE ?");
            ps.setString(1, this.name);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void customUpdateStatement(String Statement) {
        try {
            PreparedStatement ps = connection.prepareStatement(Statement);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public ResultSet customQueryStatement(String Statement) {
        try {
            PreparedStatement ps = connection.prepareStatement(Statement);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        return null;
    }

    private Boolean exsits(String key, String value) {
        try {
            if (!this.structure.containsKey(key)) {
                if (disableOnError) {
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
                return null;
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ? WHERE ?=?");
            ps.setString(1, this.database);
            ps.setString(2, key.toUpperCase());
            ps.setString(3, value.toUpperCase());
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            if (disableOnError) {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
            return false;
        }
    }

    private static String keys = "";
    private String getKeys() {
        this.structure.forEach((key, value) -> {
            keys += key.toUpperCase() + ",";
        });
        keys = "(" + keys + ")";
        return keys;
    }

    private String getKeys(HashMap<String, String> data) {
        data.forEach((key, value) -> {
            keys += key.toUpperCase() + ",";
        });
        keys = "(" + keys + ")";
        return keys;
    }

    private static String values = "";
    private String getValues(HashMap<String, String> data) {
        data.forEach((key, value) -> {
            values += value.toUpperCase();
        });
        values = "(" + values + ")";
        return values;
    }
}
