package jiekie.scheduler.manager;

import jiekie.economy.api.MoneyAPI;
import jiekie.multiworld.api.MultiWorldAPI;
import jiekie.multiworld.exception.WorldResetException;
import jiekie.nickname.api.NicknameAPI;
import jiekie.nickname.model.PlayerNameData;
import jiekie.scheduler.SchedulerPlugin;
import jiekie.scheduler.exception.SchedulerException;
import jiekie.scheduler.util.ChatUtil;
import jiekie.scheduler.util.NumberUtil;
import jiekie.scheduler.util.SoundUtil;
import jiekie.teleport.api.TeleportAPI;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.UUID;

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
        createWealthyStatue();
        churchBellSound();
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
            case "정각타종음" -> {
                config.set("church_bell_sound_activate", activate);
                churchBellSound();
            }
            case "부자동상생성" -> {
                config.set("wealthy_statue_create_activate", activate);
                createWealthyStatue();
            }
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        plugin.saveConfig();
    }

    public void setInterval(String schedulerName, String intervalString) throws SchedulerException {
        int interval = NumberUtil.getIntervalFromString(intervalString);
        switch (schedulerName) {
            case "월드초기화" -> {
                config.set("world_reset_interval", interval);
                worldReset();
            }
            case "청소" -> {
                config.set("clean_interval", interval);
                clean();
            }
            case "부자동상생성" -> {
                config.set("wealthy_statue_create_interval", interval);
                createWealthyStatue();
            }
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

    public void setLocation(String schedulerName, World world, String xString, String yString, String zString) throws SchedulerException {
        String path;
        switch (schedulerName) {
            case "정각타종음" -> {
                path = "church_bell_sound_location";
                churchBellSound();
            }
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        saveCoordinates(path, world, xString, yString, zString);
    }

    public void setStatueLocation(String rankString, World world, String xString, String yString, String zString) throws SchedulerException {
        int rank = NumberUtil.getRankFromString(rankString);
        String path = "wealthy_statue_create_locations." + rank;
        saveCoordinates(path, world, xString, yString, zString);
        createWealthyStatue();
    }

    public void setSoundEffect(String schedulerName, String sound, String volumeString, String secondsString) throws SchedulerException {
        String path;
        switch (schedulerName) {
            case "정각타종음" -> {
                path = "church_bell_sound";
                churchBellSound();
            }
            default -> throw new SchedulerException(ChatUtil.NO_SUCH_SCHEDULER);
        }

        saveSoundEffect(path, sound, volumeString, secondsString);
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

        player.sendMessage("　최종 월드 초기화 일자 : " + parsedDate.format(displayFormatter));
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

        lastDate = config.getString("wealthy_statue_create_last_date", "");
        parsedDate = LocalDateTime.parse(lastDate, parseFormatter);
        player.sendMessage("　최종 부자 동상 생성 일자 : " + parsedDate.format(displayFormatter));
        player.sendMessage("　");

        activate = config.getBoolean("church_bell_sound_activate", false);
        activateMessage = activate ? ChatColor.GREEN + "활성화" : ChatColor.RED + "비활성화";
        player.sendMessage("　정각 타종음 : " + activateMessage);
        player.sendMessage("　");

        player.sendMessage("　야생 이용 시간 : " + config.getInt("wild_warp_ticket_timer", 30) + "분");
        player.sendMessage("　지옥 이용 시간 : " + config.getInt("hell_warp_ticket_timer", 30) + "분");
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
        }.runTaskTimer(plugin, 0L, 20L * 60 * 60);

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

    private void createWealthyStatue() {
        String taskName = "wealthy_statue_create";
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

                LocalDateTime resetDateTime = now.atStartOfDay();
                config.set(taskName + "_last_date", resetDateTime.format(formatter));
                plugin.saveConfig();

                // remove old statue
                ConfigurationSection nameSection = config.getConfigurationSection(taskName + "_names");
                for(String rank : nameSection.getKeys(false)) {
                    String npcName = nameSection.getString(rank);
                    npcName = npcName.replaceAll("§", "&");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc select " + npcName);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc remove");
                }
                config.set(taskName + "_names", null);

                // create new statue
                ConfigurationSection locationSection = config.getConfigurationSection(taskName + "_locations");
                if(locationSection == null) return;

                int count = 0;
                for(String name : locationSection.getKeys(false)) {
                    int rank = Integer.parseInt(name);
                    if(rank > count) count = rank;
                }
                if(count == 0) return;

                Map<Integer, UUID> topRichestPlayers = MoneyAPI.getInstance().getTopRichestPlayers(count);
                for(String rankString : locationSection.getKeys(false)) {
                    int rank = Integer.parseInt(rankString);
                    if(!topRichestPlayers.containsKey(rank)) continue;

                    // name
                    UUID uuid = topRichestPlayers.get(rank);
                    PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(uuid);
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    String nickname = Bukkit.getOfflinePlayer(uuid).getName();
                    if(playerNameData != null)
                        nickname = playerNameData.getNickname();
                    String statueName = getStatueName(rank, nickname);

                    // coordinates
                    String worldName = locationSection.getString(rankString + ".world", null);
                    if(worldName == null) continue;
                    World world = Bukkit.getWorld(worldName);
                    if(world == null) continue;

                    double x = locationSection.getDouble(rankString + ".x", 0.0);
                    double y = locationSection.getDouble(rankString + ".y", 0.0);
                    double z = locationSection.getDouble(rankString + ".z", 0.0);

                    String locationChat = x + "," + y + "," + z + "," + worldName;

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc create " + statueName + " --at " + locationChat);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc skin " + name);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc look");
                    if(rank == 1)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc sitting");

                    config.set(taskName + "_names." + rankString, statueName);
                }
                plugin.saveConfig();
            }
        }.runTaskTimer(plugin, 0L, 20L * 60 * 60);

        tasks.put(taskName, task);
    }

    private String getStatueName(int rank, String name) {
        ChatColor color;
        if(rank % 3 == 1) color = ChatColor.YELLOW;
        else if(rank % 3 == 2) color = ChatColor.GREEN;
        else color = ChatColor.WHITE;

        return "[ " + color + rank + ChatColor.WHITE + "위 부자 ] " + name;
    }

    private void churchBellSound() {
        String taskName = "church_bell_sound";
        cleanTasks(taskName);

        boolean activate = config.getBoolean(taskName + "_activate", false);
        if(!activate) return;

        String worldName = config.getString(taskName + "_location.world", null);
        if(worldName == null) return;
        World world = Bukkit.getWorld(worldName);
        if(world == null) return;

        double x = config.getDouble(taskName + "_location.x", 0.0);
        double y = config.getDouble(taskName + "_location.y", 0.0);
        double z = config.getDouble(taskName + "_location.z", 0.0);

        String sound = config.getString(taskName + "_name", null);
        if(sound == null) return;
        float volume = (float) config.getDouble(taskName + "_volume", 0.0);
        int seconds = config.getInt(taskName + "_length", 1);

        Location location = new Location(world, x, y, z);
        BukkitTask task = new BukkitRunnable() {
            int lastHour = -1;

            @Override
            public void run() {
                LocalTime now = LocalTime.now();
                if(now.getMinute() != 0 || now.getSecond() != 0) return;

                int currentHour = now.getHour() % 12 == 0 ? 12 : now.getHour() % 12;
                if(currentHour == lastHour) return;

                lastHour = currentHour;
                playChurchBell(currentHour, location, sound, volume, seconds);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        tasks.put(taskName, task);
    }

    private void playChurchBell(int currentHour, Location location, String sound, float volume, int seconds) {
        new BukkitRunnable() {
            final int totalCount = currentHour;
            int playedCount = 1;

            @Override
            public void run() {
                if(playedCount == totalCount)
                    cancel();

                SoundUtil.playSoundAtWorld(location, sound, volume);
                playedCount++;
            }
        }.runTaskTimer(plugin, 0L, 20L * seconds);
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

    private void cleanTasks(String taskName) {
        if(tasks.containsKey(taskName)) {
            tasks.get(taskName).cancel();
            tasks.remove(taskName);
        }
    }

    private void saveCoordinates(String path, World world, String xString, String yString, String zString) throws SchedulerException {
        String worldName = world.getName();
        double x = NumberUtil.getCoordinateFromString(xString);
        double y = NumberUtil.getCoordinateFromString(yString);
        double z = NumberUtil.getCoordinateFromString(zString);

        config.set(path + ".world", worldName);
        config.set(path + ".x", x);
        config.set(path + ".y", y);
        config.set(path + ".z", z);
        plugin.saveConfig();
    }

    private void saveSoundEffect(String path, String sound, String volumeString, String secondsString) throws SchedulerException {
        float volume = NumberUtil.getVolumeFromString(volumeString);
        int seconds = NumberUtil.getSoundLengthFromString(secondsString);

        config.set(path + "_name", sound);
        config.set(path + "_volume", volume);
        config.set(path + "_length", seconds);
        plugin.saveConfig();
    }
}
