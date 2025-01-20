package com.DuyunDz23;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimatedMOTD extends JavaPlugin {
    private MOTDManager motdManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        motdManager = new MOTDManager(this);
        Bukkit.getLogger().info("AnimatedMOTD đã được kích hoạt!");
        configManager = new ConfigManager(this);
        String motd = configManager.getString("motd", "Chào mừng đến với server!");
        getLogger().info("MOTD hiện tại: " + motd);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("AnimatedMOTD đã dừng!");
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