package jiekie.scheduler.completer;

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
        if(!(sender instanceof Player)) return Collections.emptyList();

        int length = args.length;
        if(length == 1) {
            return Arrays.asList("활성화", "비활성화", "주기설정", "시각설정", "정보", "도움말");
        }

        String commandType = args[0];
        if(length == 2) {
            switch (commandType) {
                case "활성화", "비활성화" -> { return Arrays.asList("월드초기화", "서버자동종료", "청소", "부자동상생성"); }
                case "주기설정" -> { return Arrays.asList("월드초기화", "청소", "부자동상생성", "야생이용시간", "지옥이용시간"); }
                case "시각설정" -> { return Arrays.asList("서버자동종료"); }
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
        }

        return Collections.emptyList();
    }
}
