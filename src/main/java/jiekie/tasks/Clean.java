package jiekie.tasks;

import jiekie.SchedulerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Clean {
    public static void clean(SchedulerPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 아이템");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 화살");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 경험치");
                Bukkit.broadcastMessage("");
            }
        }.runTaskTimer(plugin, 0L, 30 * 20L);
    }
}
