package jiekie;

import jiekie.command.SchedulerCommand;
import jiekie.completer.SchedulerTabCompleter;
import jiekie.event.PlayerEvent;
import jiekie.manager.PlayerSchedulerManager;
import jiekie.manager.SchedulerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchedulerPlugin extends JavaPlugin {
    private SchedulerManager schedulerManager;
    private PlayerSchedulerManager playerSchedulerManager;

    @Override
    public void onEnable() {
        // config
        saveDefaultConfig();
        reloadConfig();

        // manager
        schedulerManager = new SchedulerManager(this);
        schedulerManager.init();
        playerSchedulerManager = new PlayerSchedulerManager(this);

        // command
        getCommand("배치").setExecutor(new SchedulerCommand(this));

        // tab completer
        getCommand("배치").setTabCompleter(new SchedulerTabCompleter());

        // event
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);

        getLogger().info("스케줄러 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    public PlayerSchedulerManager getPlayerSchedulerManager() {return playerSchedulerManager;}

    @Override
    public void onDisable() {}
}
