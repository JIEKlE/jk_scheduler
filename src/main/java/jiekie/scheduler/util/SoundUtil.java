package jiekie.scheduler.util;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playNoteBlockBell(Player player) {
        player.playSound(player.getLocation(), "minecraft:block.note_block.bell", SoundCategory.MASTER, 0.5f, 1.0f);
    }

    public static void playClockTicking(Player player) {
        player.playSound(player.getLocation(), "minecraft:jk.clock_ticking", SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public static void playSoundAtWorld(Location location, String sound, float volume) {
        World world = location.getWorld();
        world.playSound(location, sound, SoundCategory.MASTER, volume, 1.0f);
    }
}
