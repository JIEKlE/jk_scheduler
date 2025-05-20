package jiekie.scheduler.completer;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SchedulerTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("jk.scheduler")) return Collections.emptyList();
        if(!(sender instanceof Player player)) return Collections.emptyList();
        Block targetBlock = player.getTargetBlockExact(10);

        int length = args.length;
        if(length == 1) {
            return Arrays.asList("활성화", "비활성화", "주기설정", "시각설정", "좌표설정"
                    , "동상좌표설정", "효과음설정", "정보", "도움말");
        }

        String commandType = args[0];
        if(length == 2) {
            switch (commandType) {
                case "활성화", "비활성화" -> { return Arrays.asList("월드초기화", "서버자동종료", "청소", "정각타종음", "부자동상생성"); }
                case "주기설정" -> { return Arrays.asList("월드초기화", "청소", "부자동상생성", "야생이용시간", "지옥이용시간"); }
                case "시각설정" -> { return List.of("서버자동종료"); }
                case "좌표설정", "효과음설정" -> { return List.of("정각타종음"); }
                case "동상좌표설정" -> { return Arrays.asList("1", "2", "3"); }
            }
        }

        String schedulerType = args[1];
        if(length == 3) {
            if(commandType.equals("주기설정")) {
                if(schedulerType.equals("월드초기화") || schedulerType.equals("부자동상생성"))
                    return List.of("주기(일)");

                if(schedulerType.equals("청소") || schedulerType.equals("야생이용시간") || schedulerType.equals("지옥이용시간"))
                    return List.of("주기(분)");
            }

            if(commandType.equals("시각설정"))
                return Arrays.asList("09:00:00", "18:00:00");

            if(commandType.equals("좌표설정") || commandType.equals("동상좌표설정")) {
                if(targetBlock != null)
                    return List.of(String.valueOf(targetBlock.getX()));
            }

            if(commandType.equals("효과음설정"))
                return List.of("사운드");
        }

        if(length == 4) {
            if(commandType.equals("좌표설정") || commandType.equals("동상좌표설정")) {
                if(targetBlock != null)
                    return List.of(String.valueOf(targetBlock.getY()));
            }

            if(commandType.equals("효과음설정"))
                return Arrays.asList("0.5", "1");
        }

        if(length == 5) {
            if(commandType.equals("좌표설정") || commandType.equals("동상좌표설정")) {
                if(targetBlock != null)
                    return List.of(String.valueOf(targetBlock.getZ()));
            }

            if(commandType.equals("효과음설정"))
                return List.of("음악길이(초)");
        }

        return Collections.emptyList();
    }
}
