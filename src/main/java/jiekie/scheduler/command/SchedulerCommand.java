package jiekie.scheduler.command;

import jiekie.scheduler.SchedulerPlugin;
import jiekie.scheduler.exception.SchedulerException;
import jiekie.scheduler.util.ChatUtil;
import jiekie.scheduler.util.SoundUtil;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SchedulerCommand implements CommandExecutor {
    private final SchedulerPlugin plugin;

    public SchedulerCommand(SchedulerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            ChatUtil.notPlayer(sender);
            return true;
        }

        if(!player.isOp()) {
            ChatUtil.notOp(player);
            return true;
        }

        if(args == null || args.length == 0) {
            ChatUtil.commandHelper(player);
            return true;
        }

        switch(args[0]) {
            case "활성화":
                activateScheduler(player, args, true);
                break;

            case "비활성화":
                activateScheduler(player, args, false);
                break;

            case "주기설정":
                setInterval(player, args);
                break;

            case "시각설정":
                setTime(player, args);
                break;

            case "좌표설정":
                setLocation(player, args);
                break;

            case "효과음설정":
                setSoundEffect(player, args);
                break;

            case "동상좌표설정":
                setStatueLocation(player, args);
                break;

            case "정보":
                showInfo(player);
                break;

            case "도움말":
                ChatUtil.commandList(sender);
                break;

            default:
                ChatUtil.commandHelper(sender);
                break;
        }

        return true;
    }

    private void activateScheduler(Player player, String[] args, boolean activate) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 활성화|비활성화 항목)");
            return;
        }

        try {
            plugin.getSchedulerManager().activateScheduler(args[1], activate);

            ChatUtil.showMessage(player, activate ? ChatUtil.ACTIVATE_SCHEDULER : ChatUtil.DEACTIVATE_SCHEDULER);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setInterval(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 주기설정 항목 주기)");
            return;
        }

        try {
            plugin.getSchedulerManager().setInterval(args[1], args[2]);
            ChatUtil.showMessage(player, ChatUtil.SET_INTERVAL);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setTime(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 시각설정 항목 시각)");
            return;
        }

        try {
            plugin.getSchedulerManager().setTime(args[1], args[2]);
            ChatUtil.showMessage(player, ChatUtil.SET_TIME);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setLocation(Player player, String[] args) {
        if(args.length < 5) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 좌표설정 항목 x y z)");
            return;
        }

        try {
            plugin.getSchedulerManager().setLocation(args[1], player.getWorld(), args[2], args[3], args[4]);
            ChatUtil.showMessage(player, ChatUtil.SET_LOCATION);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setSoundEffect(Player player, String[] args) {
        if(args.length < 5) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 효과음설정 항목 사운드 볼륨 음악길이(초))");
            return;
        }

        try {
            plugin.getSchedulerManager().setSoundEffect(args[1], args[2], args[3], args[4]);
            ChatUtil.showMessage(player, ChatUtil.SET_SOUND_EFFECT);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setStatueLocation(Player player, String[] args) {
        if(args.length < 5) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/배치 동상좌표설정 순위 x y z)");
            return;
        }

        try {
            plugin.getSchedulerManager().setStatueLocation(args[1], player.getWorld(), args[2], args[3], args[4]);
            ChatUtil.showMessage(player, ChatUtil.SET_STATUE_LOCATION);
            SoundUtil.playNoteBlockBell(player);

        } catch (SchedulerException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void showInfo(Player player) {
        ChatUtil.schedulerInfoPrefix(player);
        plugin.getSchedulerManager().showInfo(player);
        ChatUtil.horizontalLineSuffix(player);
        SoundUtil.playNoteBlockBell(player);
    }
}
