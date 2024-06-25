package dev.jayoxdev.blockyrewards.Managers;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionStartup {
    private final BlockyRewards plugin;
    private MySQL mySQL;
    private SQLite sqLite;

    public ConnectionStartup(BlockyRewards plugin) {
        this.plugin = plugin;
        initializeDatabase();

    }

    private void initializeDatabase() {
        FileConfiguration config = plugin.getConfig();
        String dbType = config.getString("database.type");

        if (dbType.equalsIgnoreCase("MySQL")) {
            String user = config.getString("database.mysql.user");
            String password = config.getString("database.mysql.password");
            String endpoint = config.getString("database.mysql.endpoint");
            String database = config.getString("database.mysql.database");
            mySQL = new MySQL(user, password, endpoint, database);
        } else if (dbType.equalsIgnoreCase("SQLite")) {
            String filePath = plugin.getDataFolder() + "/database.sqlite";
            sqLite = new SQLite(filePath);
        }
    }

    public Connection getConnection() throws SQLException {
        if (mySQL != null) {
            return mySQL.getConnection();
        } else if (sqLite != null) {
            return sqLite.getConnection();
        } else {
            throw new SQLException("No database configured.");
        }
    }

    public void close() {
        if (mySQL != null) {
            mySQL.close();
        }
        if (sqLite != null) {
            sqLite.close();
        }
    }

    public void createTables(String... createTableQueries) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            for (String query : createTableQueries) {
                statement.executeUpdate(query);
            }
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            return statement.executeQuery(query);
        }
    }

    public int executeUpdate(String query) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        }
    }
}
