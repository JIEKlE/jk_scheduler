package jiekie.scheduler;

import jiekie.scheduler.command.SchedulerCommand;
import jiekie.scheduler.completer.SchedulerTabCompleter;
import jiekie.scheduler.event.PlayerEvent;
import jiekie.scheduler.manager.PlayerSchedulerManager;
import jiekie.scheduler.manager.SchedulerManager;
import org.bukkit.entity.Player;
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
        playerSchedulerManager.load();

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
    public void onDisable() {
        kickPlayers();
        playerSchedulerManager.save();
    }

    private void kickPlayers() {
        for(Player player : getServer().getOnlinePlayers()) {
            if(player.isOp()) return;
            player.kickPlayer("서버 점검을 시작했습니다. 디스코드에서 운영자 공지를 확인한 뒤 접속하세요.");
        }
    }
}
