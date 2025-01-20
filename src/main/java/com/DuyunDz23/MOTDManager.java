package com.DuyunDz23;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MOTDManager {
    private final JavaPlugin plugin;
    private List<String> animatedMessages;
    private AtomicInteger index = new AtomicInteger(0);

    public MOTDManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadMOTD();
        startAnimation();
    }

    public void reloadMOTD() {
        FileConfiguration config = plugin.getConfig();
        animatedMessages = config.getStringList("motd.messages");
    }

    private void startAnimation() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (animatedMessages.isEmpty()) return;
            String motd = animatedMessages.get(index.getAndIncrement() % animatedMessages.size());
            Bukkit.getServer().setMotd(motd);
        }, 0L, plugin.getConfig().getLong("motd.interval"));
    }
}
