package jiekie.scheduler.manager;

import jiekie.scheduler.SchedulerPlugin;
import jiekie.api.MultiWorldAPI;
import jiekie.api.TeleportAPI;
import jiekie.scheduler.exception.SchedulerException;
import jiekie.exception.WorldResetException;
import jiekie.scheduler.util.ChatUtil;
import jiekie.scheduler.util.SoundUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class SchedulerManager {
    private final SchedulerPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, BukkitTask> tasks;

    public SchedulerManager(SchedulerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        tasks = new HashMap<>();
    }

    public void init() {
        worldReset();
        serverAutoStop();
        clean();
    }

    private void worldReset() {
        String taskName = "world_reset";
        cleanTasks(taskName);

        boolean activate = config.getBoolean(taskName + "_activate", false);
        if(!activate) return;

        int interval = config.getInt(taskName + "_interval", 1);
        String lastDate = config.getString(taskName + "_last_date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                LocalDate now = LocalDate.now();
                LocalDate lastResetDate = LocalDate.parse(lastDate, formatter);
                LocalDate nextResetDate = lastResetDate.plusDays(interval);

                if(!now.isAfter(nextResetDate) && !now.isEqual(nextResetDate)) return;

                try {
                    MultiWorldAPI.getInstance().resetWorld("wild");
                    resetSpawnCoordinates("wild");
                    MultiWorldAPI.getInstance().resetWorld("hell");
                    resetSpawnCoordinates("hell");

                } catch (WorldResetException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    ChatUtil.broadcastMessage(ChatUtil.WORLD_RESET_ERROR);
                    return;
                }

                LocalDateTime resetDateTime = now.atStartOfDay();
                config.set(taskName + "_last_date", resetDateTime.format(formatter));
                plugin.saveConfig();
            }
        }.runTaskTimer(plugin, 0L, 20L * 60 * 30);

        tasks.put(taskName, task);
    }

    private void resetSpawnCoordinates(String worldName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        World world = Bukkit.getWorld(worldName);
                        if(world == null) return;

                        Location location = world.getSpawnLocation();
                        switch(worldName) {
                            case "wild" -> TeleportAPI.getInstance().setLocation("야생", location);
                            case "hell" -> setSafeSpawn("지옥", location);
                        }

                        cancel();
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }.runTaskLater(plugin, 40L);
    }

    private void setSafeSpawn(String locationName, Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        for(int dx = x - 1 ; dx <= x + 1 ; dx++) {
            for(int dz = z - 1 ; dz <= z + 1 ; dz++) {
                world.getBlockAt(dx, y - 1, dz).setType(Material.NETHERRACK);
                world.getBlockAt(dx, y + 3, dz).setType(Material.NETHERRACK);

                for(int dy = y ; dy <= y + 2 ; dy++) {
                    world.getBlockAt(dx, dy, dz).setType(Material.AIR);
                }
            }
        }

        TeleportAPI.getInstance().setLocation(locationName, location);
    }

    private void clean() {
        String taskName = "clean";
        cleanTasks(taskName);

        boolean activate = config.getBoolean(taskName + "_activate", false);
        if(!activate) return;

        int interval = config.getInt(taskName + "_interval", 30);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 아이템");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 화살");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "청소 경험치");
            }
        }.runTaskTimer(plugin, 0L, 20L * 60 * interval);

        tasks.put(taskName, task);
    }

    private void serverAutoStop() {
        String taskName = "server_auto_stop";
        cleanTasks(taskName);

        boolean activate = config.getBoolean(taskName + "_activate", false);
        if(!activate) return;

        String time = config.getString(taskName + "_time", "01:00:00");
        LocalTime shutdownTime = LocalTime.parse(time).truncatedTo(ChronoUnit.SECONDS);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                long between = ChronoUnit.SECONDS.between(now, shutdownTime);

                if(between == 60 * 30) {
                    ChatUtil.broadcastMessage(ChatUtil.getSpeakerPrefix() + "30분 후 " + ChatUtil.WARN_SHUTDOWN);
                    for(Player player : Bukkit.getOnlinePlayers())
                        SoundUtil.playClockTicking(player);
                }

                if(between == 60 * 5) {
                    ChatUtil.broadcastMessage(ChatUtil.getSpeakerPrefix() + "5분 후 " + ChatUtil.WARN_SHUTDOWN);
                    for(Player player : Bukkit.getOnlinePlayers())
                        SoundUtil.playClockTicking(player);
                }

                if(between == 60) {
                    ChatUtil.broadcastMessage(ChatUtil.getSpeakerPrefix() + "1분 후 " + ChatUtil.WARN_SHUTDOWN);
                    for(Player player : Bukkit.getOnlinePlayers())
                        SoundUtil.playClockTicking(player);
                }

                if(between == 0) {
                    Bukkit.shutdown();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        tasks.put(taskName, task);
    }

    private void cleanTasks(String taskName) {
        if(tasks.containsKey(taskName)) {
            tasks.get(taskName).cancel();
            tasks.remove(taskName);
        }
    }

    public void activateScheduler(String schedulerName, boolean activate) throws SchedulerException {
        switch (schedulerName) {
            case "월드초기화" -> {
                config.set("world_reset_activate", activate);
                worldReset();
            }
            case "서버자동종료" -> {
                config.set("server_auto_stop_activate", activate);
                serverAutoStop();
            }
            case "청소" -> {
                config.set("clean_activate", activate);
                clean();
            }
            case "부자동상생성" -> config.set("wealthy_statue_create_activate", activate);
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        plugin.saveConfig();
    }

    public void setInterval(String schedulerName, String intervalString) throws SchedulerException {
        int interval;

        try {
            interval = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.INTERVAL_NOT_NUMBER);
        }

        if(interval < 1)
            throw new SchedulerException(ChatUtil.INTERVAL_LESS_THAN_ONE);

        switch (schedulerName) {
            case "월드초기화" -> {
                config.set("world_reset_interval", interval);
                worldReset();
            }
            case "청소" -> {
                config.set("clean_interval", interval);
                clean();
            }
            case "부자동상생성" -> config.set("wealthy_statue_create_interval", interval);
            case "야생이용시간" -> config.set("wild_warp_ticket_timer", interval);
            case "지옥이용시간" -> config.set("hell_warp_ticket_timer", interval);
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        plugin.saveConfig();
    }

    public void setTime(String schedulerName, String time) throws SchedulerException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            LocalTime.parse(time, formatter);
        } catch (Exception e) {
            throw new SchedulerException(ChatUtil.INVALID_TIME);
        }

        switch (schedulerName) {
            case "서버자동종료" -> {
                config.set("server_auto_stop_time", time);
                serverAutoStop();
            }
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        plugin.saveConfig();
    }

    public void showInfo(Player player) {
        boolean activate = config.getBoolean("world_reset_activate", false);
        String activateMessage = activate ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        player.sendMessage("　월드 초기화 : " + activateMessage);
        player.sendMessage("　월드 초기화 주기 : " + config.getInt("world_reset_interval", 1) + "일");

        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String lastDate = config.getString("world_reset_last_date", "");
        LocalDateTime parsedDate = LocalDateTime.parse(lastDate, parseFormatter);

        player.sendMessage("　최종 월드 초기화 일시 : " + parsedDate.format(displayFormatter));
        player.sendMessage("　");

        activate = config.getBoolean("server_auto_stop_activate", false);
        activateMessage = activate ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        player.sendMessage("　서버 자동 종료 : " + activateMessage);
        player.sendMessage("　서버 자동 종료 시각 : " + config.getString("server_auto_stop_time", "01:00:00"));
        player.sendMessage("　");

        activate = config.getBoolean("clean_activate", false);
        activateMessage = activate ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        player.sendMessage("　서버 청소 : " + activateMessage);
        player.sendMessage("　서버 청소 주기 : " + config.getInt("clean_interval", 1) + "분");
        player.sendMessage("　");

        activate = config.getBoolean("wealthy_statue_create_activate", false);
        activateMessage = activate ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        player.sendMessage("　부자 동상 생성 : " + activateMessage);
        player.sendMessage("　부자 동상 생성 주기 : " + config.getInt("wealthy_statue_create_interval", 1) + "일");
        player.sendMessage("　");
        player.sendMessage("　야생 이용 시간 : " + config.getInt("wild_warp_ticket_timer", 30) + "분");
        player.sendMessage("　지옥 이용 시간 : " + config.getInt("hell_warp_ticket_timer", 30) + "분");
    }
}
