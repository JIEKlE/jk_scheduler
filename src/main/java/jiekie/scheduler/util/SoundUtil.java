package jiekie.scheduler.util;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playNoteBlockBell(Player player) {
        player.playSound(player.getLocation(), "minecraft:block.note_block.bell", SoundCategory.MASTER, 0.5f, 1.0f);
    }

    public static void playClockTicking(Player player) {
        player.playSound(player.getLocation(), "minecraft:jk.clock_ticking", SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public static void playTowerBell(Player player) {
        player.playSound(player.getLocation(), "sepia_film_sounds:sound_effect.tower_bell", SoundCategory.MASTER, 1.0f, 1.0f);
    }
}
