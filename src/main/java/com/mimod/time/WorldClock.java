
package com.mimod.time;

import net.minecraft.world.level.Level;

public class WorldClock {

    // Duración de un día de Minecraft
    private static final long TICKS_PER_DAY = 24000L;

    // Obtener el número de día
    public static long getDayNumber(Level level) {

        if (level == null) return 1;

        return Math.max(
                1L,
                Math.floorDiv(
                        level.getDayTime() + 6000L,
                        TICKS_PER_DAY
                )
        );
    }

    // Obtener la hora del mundo
    public static int getHour(Level level) {

        if (level == null) return 6;

        long time = Math.floorMod(
                level.getDayTime(),
                TICKS_PER_DAY
        );

        return (int) ((time / 1000 + 6) % 24);
    }

    // Obtener los minutos
    public static int getMinute(Level level) {

        if (level == null) return 0;

        long time = Math.floorMod(
                level.getDayTime(),
                TICKS_PER_DAY
        );

        return (int) ((time % 1000) * 60 / 1000);
    }

    // Formato de hora AM/PM
    public static String getFormattedTime(Level level) {

        int hour = getHour(level);
        int minute = getMinute(level);

        String period = hour < 12 ? "AM" : "PM";

        int displayHour = hour % 12;

        if (displayHour == 0) {
            displayHour = 12;
        }

        return String.format(
                "%02d:%02d %s",
                displayHour,
                minute,
                period
        );
    }
}
