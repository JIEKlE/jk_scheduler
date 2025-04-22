package jiekie;

import jiekie.tasks.Clean;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchedulerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Clean.clean(this);

        getLogger().info("스케줄러 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    @Override
    public void onDisable() {}
}
