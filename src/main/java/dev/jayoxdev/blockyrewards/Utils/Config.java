package dev.jayoxdev.blockyrewards.Utils;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Config {
    private final BlockyRewards plugin;
    private File configFile;
    private File messageFile;
    private File rewardsFile;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration rewards;

    public Config(BlockyRewards plugin) {
        this.plugin = plugin;
        saveDefaultFiles();
        loadFiles();
    }

    private void saveDefaultFiles() {
        saveDefaultFile("messages.yml");
        saveDefaultFile("config.yml");
        saveDefaultFile("rewards.yml");
    }

    private void saveDefaultFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try (InputStream in = plugin.getResource(fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFiles() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        messageFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messageFile);

        rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        rewards = YamlConfiguration.loadConfiguration(rewardsFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getRewards() {
        return rewards;
    }

    public void reloadFiles() {
        reloadConfig();
        reloadMessages();
        reloadRewards();
    }

    public void reloadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadMessages() {
        File messageFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messageFile);
    }

    public void reloadRewards() {
        File rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        rewards = YamlConfiguration.loadConfiguration(rewardsFile);
    }


    public void saveAll() {
        saveConfig();
        saveMessages();
        saveRewards();
    }

    public void saveConfig() {
        saveFile(config, configFile);
    }


    public void saveMessages() {
        saveFile(messages, messageFile);
    }

    public void saveRewards() {
        saveFile(rewards, rewardsFile);
    }

    private void saveFile(FileConfiguration fileConfig, File file) {
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getConfigVersion() {
        return getConfig().get("config");
    }

    public int getLastestConfigVersion() {
        String[] versionParts = plugin.getVersion().split("\\.");
        return Integer.parseInt(versionParts[0]);
    }
}
