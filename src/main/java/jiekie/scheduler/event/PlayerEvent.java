package jiekie.scheduler.event;

import jiekie.scheduler.SchedulerPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {
    private final SchedulerPlugin plugin;

    public PlayerEvent(SchedulerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerSchedulerManager().startWorldTimer(e.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        plugin.getPlayerSchedulerManager().changeWorld(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayerSchedulerManager().stopWorldTimer(e.getPlayer());
    }
}
