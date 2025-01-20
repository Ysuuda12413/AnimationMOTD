package com.DuyunDz23;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;

public class AnimatedMOTD extends JavaPlugin {
    private MOTDManager motdManager;
    private ConfigManager configManager;
    private List<String> frames;
    private int currentFrameIndex = 0;
    private int delay;
    private boolean isAnimationEnabled;

    private static final String GITHUB_RELEASES_URL = "https://api.github.com/repos/Ysuuda12413/AnimationMOTD/releases/latest";
    private static final String DOWNLOAD_URL_PREFIX = "https://github.com/Ysuuda12413/AnimationMOTD/releases/download/";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        motdManager = new MOTDManager(this);
        configManager = new ConfigManager(this);

        // In thông tin khi plugin được kích hoạt
        Bukkit.getLogger().info("+===========================================================================+\n" +
                "|    _          _                 _   _             __  __  ___ _____ ____  |\n" +
                "|   / \\   _ __ (_)_ __ ___   __ _| |_(_) ___  _ __ |  \\/  |/ _ \\_   _|  _ \\ |\n" +
                "|  / _ \\ | '_ \\| | '_ ` _ \\ / _` | __| |/ _ \\| '_ \\| |\\/| | | | || | | | | ||\n" +
                "| / ___ \\| | | | | | | | | | (_| | |_| | (_) | | | | |  | | |_| || | | |_| ||\n" +
                "|/_/   \\_\\_| |_|_|_| |_| |_|\\__,_|\\__|_|\\___/|_| |_|_|  |_|\\___/ |_| |____/ |\n" +
                "| By: DuyunDz23" +
                "+===========================================================================+");

        String motd = configManager.getString("motd", "Chào mừng đến với server!");
        getLogger().info("MOTD hiện tại: " + motd);

        isAnimationEnabled = configManager.getBoolean("animation.enabled", false);
        if (isAnimationEnabled) {
            frames = configManager.getList("animation.frames");
            delay = configManager.getInt("animation.delay", 5);
            startAnimation();
        } else {
            Bukkit.getServer().setMotd(motd);
        }
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("AnimatedMOTD đã dừng!");
    }

    private void checkForUpdates() {
        String currentVersion = getDescription().getVersion();
        String newVersion = getLatestVersionFromGitHub();

        if (newVersion != null && !currentVersion.equals(newVersion)) {
            getLogger().info("New version available: " + newVersion);
            downloadNewVersion(newVersion);
            replaceOldVersion();
        }
    }

    private String getLatestVersionFromGitHub() {
        try {
            URL url = new URL(GITHUB_RELEASES_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                // Parse JSON response to get the latest version tag
                String versionTag = response.split("\"tag_name\":\"")[1].split("\"")[0];
                return versionTag;
            }
        } catch (IOException e) {
            getLogger().severe("Failed to check for updates: " + e.getMessage());
            return null;
        }
    }

    private void downloadNewVersion(String newVersion) {
        String downloadUrl = DOWNLOAD_URL_PREFIX + newVersion + "/AnimatedMOTD.jar";
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream("plugins/AnimatedMOTD-new.jar")) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            getLogger().severe("Failed to download new version: " + e.getMessage());
        }
    }

    private void replaceOldVersion() {
        try {
            Path oldJar = Paths.get("plugins/AnimatedMOTD.jar");
            Path newJar = Paths.get("plugins/AnimatedMOTD-new.jar");
            Files.deleteIfExists(oldJar); // Delete the old jar file
            Files.move(newJar, oldJar); // Rename new jar to the old jar name
            getLogger().info("Plugin updated successfully. Please restart the server to apply the changes.");
        } catch (IOException e) {
            getLogger().severe("Failed to replace old version: " + e.getMessage());
        }
    }

    private void startAnimation() {
        if (frames != null && !frames.isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Cập nhật MOTD theo frame hiện tại
                    String frame = frames.get(currentFrameIndex);
                    Bukkit.getServer().setMotd(frame);

                    // Cập nhật chỉ số frame
                    currentFrameIndex = (currentFrameIndex + 1) % frames.size();  // Nếu hết frame thì quay lại frame đầu
                }
            }.runTaskTimer(this, 0L, delay * 20L); // delay * 20L vì 1 giây = 20 ticks
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("animatedmotd") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            motdManager.reloadMOTD();
            sender.sendMessage("§aCấu hình MOTD đã được tải lại!");
            return true;
        }
        return false;
    }
}