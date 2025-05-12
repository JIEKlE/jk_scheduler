package jiekie.scheduler.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
    /* error */
    public static String INTERVAL_NOT_NUMBER = getXPrefix() + "주기는 숫자만 입력할 수 있습니다.";
    public static String INTERVAL_LESS_THAN_ONE = getXPrefix() + "주기는 1 이상만 입력 가능합니다.";
    public static String INVALID_TIME = getXPrefix() + "시각 형식이 올바르지 않습니다. (HH:MM:SS)";
    public static String NO_SUCH_SCHEDULER = getXPrefix() + "존재하지 않는 배치 항목을 입력했습니다.";
    public static String WORLD_RESET_ERROR = getXPrefix() + "월드를 초기화 하는 데 오류가 발생했습니다. 로그를 확인하세요.";

    /* feedback */
    public static String ACTIVATE_SCHEDULER = getCheckPrefix() + "배치를 활성화 했습니다.";
    public static String DEACTIVATE_SCHEDULER = getCheckPrefix() + "배치를 비활성화 했습니다.";
    public static String SET_INTERVAL = getCheckPrefix() + "배치 주기를 설정했습니다.";
    public static String SET_TIME = getCheckPrefix() + "배치 실행 시각을 설정했습니다.";

    /* broadcast */
    public static String WARN_SHUTDOWN = "서버가 종료됩니다. 활동을 정리해주세요.";

    /* prefix */
    public static String getCheckPrefix() {
        return "\uA001 ";
    }

    public static String getXPrefix() {
        return "\uA002 ";
    }

    public static String getWarnPrefix() {
        return "\uA003 ";
    }

    public static String getSpeakerPrefix() {
        return "\uA007 ";
    }

    /* validate */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    public static void showMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    /* info */
    public static void schedulerInfoPrefix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("─────────── 배치정보 ───────────");
        sender.sendMessage("");
    }

    public static void horizontalLineSuffix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("──────────────────────────");
        sender.sendMessage("");
    }

    /* command */
    public static void commandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/배치 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void commandList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(getWarnPrefix() + "배치 명령어 목록");
        sender.sendMessage("　　　① /배치 활성화 항목");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 배치를 활성화합니다.");
        sender.sendMessage("　　　② /배치 비활성화 항목");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 배치를 비활성화합니다.");
        sender.sendMessage("　　　③ /배치 주기설정 항목 주기");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 배치의 주기를 설정합니다.");
        sender.sendMessage("　　　④ /배치 시각설정 항목 시각");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 배치가 실행될 시각을 설정합니다.");
        sender.sendMessage("　　　⑤ /배치 정보");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 배치 정보를 조회합니다.");
        sender.sendMessage("　　　⑥ /배치 도움말");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
    }
}
