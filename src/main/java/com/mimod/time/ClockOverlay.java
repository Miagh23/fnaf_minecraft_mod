
package com.mimod.time;

import com.mimod.MiMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = MiMod.MOD_ID,
        value = Dist.CLIENT
)
public class ClockOverlay {

    @SubscribeEvent
    public static void onChatOverlay(CustomizeGuiOverlayEvent.Chat event) {

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();

        String timeText = WorldClock.getFormattedTime(minecraft.level);
        String nightText = "NIGHT " + WorldClock.getDayNumber(minecraft.level);

        int screenWidth = event.getWindow().getGuiScaledWidth();

        int timeX = screenWidth - minecraft.font.width(timeText) - 10;
        int nightX = screenWidth - minecraft.font.width(nightText) - 10;

        guiGraphics.drawString(minecraft.font, timeText, timeX, 10, 0xFFFFFF, true);
        guiGraphics.drawString(minecraft.font, nightText, nightX, 22, 0xFF5555, true);
    }
}
