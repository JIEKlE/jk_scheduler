package jiekie.scheduler.manager;

import jiekie.scheduler.SchedulerPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerSchedulerManager {
    private final SchedulerPlugin plugin;
    private final FileConfiguration config;
    private final Map<UUID, Integer> remainingTime;
    private final Map<UUID, BukkitTask> tasks;
    private final List<String> targetWorlds;

    public PlayerSchedulerManager(SchedulerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        remainingTime = new HashMap<>();
        tasks = new HashMap<>();
        targetWorlds = List.of("wild", "hell");
    }

    public void load() {
        remainingTime.clear();
        ConfigurationSection section = config.getConfigurationSection("remaining_time");
        if(section == null) return;
        for(String uuidString : section.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int remaining = section.getInt(uuidString);
            remainingTime.put(uuid, remaining);
        }
    }

    public void startWorldTimer(Player player) {
        if(player.isOp()) return;

        UUID uuid = player.getUniqueId();
        stopWorldTimer(player);

        String worldName = player.getWorld().getName();
        if(!targetWorlds.contains(worldName)) return;

        int seconds = remainingTime.containsKey(uuid) ? remainingTime.get(uuid) : config.getInt(worldName + "_warp_ticket_timer", 30) * 60;
        BukkitTask task = new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                int hour = remaining / 3600;
                int minute = (remaining % 3600) / 60;
                int second = remaining % 60;
                String remainingMessage = String.format("\uA015 %02d:%02d:%02d", hour, minute, second);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(remainingMessage));

                if(remaining <= 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "텔레포트 이동 스폰 " + player.getName());
                    tasks.remove(uuid);
                    remainingTime.remove(uuid);
                    cancel();
                    return;
                }

                remaining--;
                remainingTime.put(uuid, remaining);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        tasks.put(uuid, task);
    }

    public void changeWorld(Player player) {
        stopWorldTimer(player);
        resetRemainingTime(player);
        startWorldTimer(player);
    }

    private void resetRemainingTime(Player player) {
        UUID uuid = player.getUniqueId();
        remainingTime.remove(uuid);
    }

    public void stopWorldTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if(tasks.containsKey(uuid)) {
            tasks.get(uuid).cancel();
            tasks.remove(uuid);
        }
    }

    public void save() {
        if(remainingTime.isEmpty()) return;
        for(UUID uuid : remainingTime.keySet()) {
            int remaining = remainingTime.get(uuid);
            config.set("remaining_time." + uuid.toString(), remaining);
        }

        plugin.saveConfig();
    }
}
