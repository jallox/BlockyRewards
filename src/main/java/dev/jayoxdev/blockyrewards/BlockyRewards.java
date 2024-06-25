package dev.jayoxdev.blockyrewards;

import dev.jayoxdev.blockyrewards.Commands.BRCommand;
import dev.jayoxdev.blockyrewards.Commands.RedeemCommand;
import dev.jayoxdev.blockyrewards.Managers.ConnectionStartup;
import dev.jayoxdev.blockyrewards.TabCompleters.BRCommandTabCompleter;
import dev.jayoxdev.blockyrewards.Utils.Config;
import dev.jayoxdev.blockyrewards.Utils.Message;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


@SuppressWarnings({"unused"})


public final class BlockyRewards extends JavaPlugin {

    private Message messageUtil;
    private Config configUtil;
    private ConnectionStartup connectionStartup;

    private String prefix;
    private String version;

    // TODO: Add bStats metrics
    // TODO: Add wiki at config.yml
    // TODO: Add wiki at rewards.yml
    @Override
    public void onEnable() {
        configUtil = new Config(this);
        messageUtil = new Message("Legacy");

        prefix = getMessageUtil().parse(getConfigUtil().getMessages().getString("prefix"));
        version = getDescription().getVersion();
        getConfigUtil().saveAll();
        getMessageUtil().sendConsole(prefix + "&aStarting Blocky&lRewards");
        getMessageUtil().sendConsole(prefix + "&aUsing parser " + getMessageUtil().getParser());
        getMessageUtil().sendConsole(prefix + "&aConfig version " + String.valueOf(getConfigUtil().getConfigVersion()));
        if (getConfigUtil().getConfigVersion().equals(getConfigUtil().getLastestConfigVersion())) {
            getMessageUtil().sendConsole(prefix + "&aYou have the latest config version!");
        } else {
            getMessageUtil().sendConsole(prefix + "&cYour config is outdated! Please update it by deleting the file plugins/BlockyRewards/config.yml or adding the missing fields.");
            getMessageUtil().sendConsole(prefix + "&cINFO: Having outdated config may produce server crashes or, in most of cases, the plugin won't work correctly!");

        }
        getMessageUtil().sendConsole(prefix + "PluginThread. &aContinue to enabling plugin...");
        getMessageUtil().sendConsole(prefix + "&aStarting DB (HikariCP)");

        // Initialize the database connection
        connectionStartup = new ConnectionStartup(this);
        getMessageUtil().sendConsole(prefix + "&aHikariCP Done!");
        getMessageUtil().sendConsole(prefix + "&aCreating tables");
        try {
            connectionStartup.createTables(
                    "CREATE TABLE IF NOT EXISTS users (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY, \n" +
                            "    uuid VARCHAR(36) NOT NULL UNIQUE, \n" +
                            "    last_login TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                            "    rewards_claimed INT NOT NULL DEFAULT 0\n" +
                            ");",
                    "CREATE TABLE IF NOT EXISTS user_rewards (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY, \n" +
                            "    user_id INT NOT NULL, \n" +
                            "    reward_date DATE NOT NULL, \n" +
                            "    FOREIGN KEY (user_id) REFERENCES users(id)\n" +
                            ");",
                    "CREATE TABLE IF NOT EXISTS commandClaims (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "uuid VARCHAR(36) NOT NULL, " +
                            "claim_code VARCHAR(50) NOT NULL, " +
                            "claim_count INT NOT NULL DEFAULT 0, " +
                            "last_claim TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (uuid) REFERENCES users(uuid)" +
                            ");"
            );
            getMessageUtil().sendConsole(prefix + "&aTables created successfully!");
        } catch (SQLException e) {
            getMessageUtil().sendConsole(prefix + "&cFailed to create tables: " + e.getMessage());
            e.printStackTrace();
        }
        PluginCommand brcmd = getCommand("blockyrewards");
        brcmd.setTabCompleter(new BRCommandTabCompleter());
        brcmd.setExecutor(new BRCommand(this));
        getCommand("redeem").setExecutor(new RedeemCommand(this));
        getMessageUtil().sendConsole(prefix + "&a +---------------------------+");
        getMessageUtil().sendConsole(prefix + "&a |    BlockyRewards v" + getVersion() + "   |");
        getMessageUtil().sendConsole(prefix + "&a +---------------------------+");
        getMessageUtil().sendConsole(prefix + "&a ");
        getMessageUtil().sendConsole(prefix + "&a Running on " + getServer().getVersion());
        getMessageUtil().sendConsole(prefix + "&7 " + getServer().getBukkitVersion());
        getMessageUtil().sendConsole(prefix + "&a " + getServer().getName());
        getMessageUtil().sendConsole(prefix + "&a ");
        getMessageUtil().sendConsole(prefix + "&a Thanks for using my plugin! <3");
        getMessageUtil().sendConsole(prefix + "&a ");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (connectionStartup != null) {
            connectionStartup.close();
        }
    }

    public Config getConfigUtil() {
        return configUtil;
    }

    public @NotNull FileConfiguration getConfig() {
        return getConfigUtil().getConfig();
    }

    public FileConfiguration getMessages() {
        return getConfigUtil().getMessages();
    }

    public Message getMessageUtil() {
        return messageUtil;
    }

    public String getVersion() {
        return version;
    }

    public String getPrefix() {
        return prefix;
    }

    public ConnectionStartup getDatabase() {
        return connectionStartup;
    }
}
