package SQL;

import org.bukkit.plugin.Plugin;

import java.sql.*;

public class SQLServer {
    Plugin plugin;
    String host = "localhost";
    String port = "3306";
    String database;
    String username = "root";
    String password = "";
    Boolean disableOnError = false;
    private Connection connection;

    public SQLServer(Plugin plugin, String database, Boolean disableOnError) {
        this.plugin = plugin;
        this.database = database.toUpperCase();
        this.disableOnError = disableOnError;
    }

    public SQLServer(Plugin plugin, String host, String port, String database, String username, String password, Boolean disableOnError) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database.toUpperCase();
        this.username = username;
        this.password = password;
        this.disableOnError = disableOnError;
    }

    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void Connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" +
                                host + ":" + port + "/" + database + "?useSSL=false",
                                username, password);
            }
            catch (SQLException e) {
                plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] The database login data is wrong!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void Disconnect() {
        if (isConnected()) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                if (disableOnError) {
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        }
    }

    public Connection getConnection() {
        return this.connection;
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
}
