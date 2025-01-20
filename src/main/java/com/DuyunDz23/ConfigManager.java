package com.DuyunDz23;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final Plugin plugin;
    private File configFile;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    // Tạo hoặc tải file config.yml
    public void saveDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Lấy giá trị từ config
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    // Đặt giá trị trong config
    public void set(String path, Object value) {
        config.set(path, value);
        saveConfig();
    }

    // Lưu cấu hình vào file
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể lưu config.yml!");
            e.printStackTrace();
        }
    }

    // Tải lại config từ file
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Trả về đối tượng cấu hình để dùng trong plugin
    public FileConfiguration getConfig() {
        return config;
    }
}
