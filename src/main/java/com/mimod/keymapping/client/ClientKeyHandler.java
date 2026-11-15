package com.mimod.keymapping.client;


import com.mimod.modItems.GuitarPick;
import com.mimod.keymapping.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mimod", value = Dist.CLIENT)
public class ClientKeyHandler {

    private static final int[] BASE_SHIFTS = {0, -6, -4, -2, 2, 4}; // índice 0 = sin tecla
    private static int baseIndex = 0;
    private static boolean up3 = false;

    private static int currentShift() {
        int shift = BASE_SHIFTS[baseIndex] + (up3 ? 3 : 0);
        return shift;
    }

    private static void selectBase(Player player, int index) {
        baseIndex = (baseIndex == index) ? 0 : index; // pulsar otra vez vuelve a 0
        player.displayClientMessage(Component.literal("Shift: " + currentShift()), true);
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = Minecraft.getInstance().player;
        if (player == null || !GuitarPick.isPlayingGuitar(player)) return;

        while (ModKeys.SCALE_SIB.consumeClick()) selectBase(player, 1);
        while (ModKeys.SCALE_DO.consumeClick()) selectBase(player, 2);
        while (ModKeys.SCALE_RE.consumeClick()) selectBase(player, 3);
        while (ModKeys.SCALE_FAS.consumeClick()) selectBase(player, 4);
        while (ModKeys.SCALE_SOLS.consumeClick()) selectBase(player, 5);
        while (ModKeys.SCALE_MAYOR_MODIFIER.consumeClick())
        {up3=!up3;
            player.displayClientMessage(Component.literal("Shift: " + currentShift()), true);}

        boolean longVersion = ModKeys.LONG_MODIFIER.isDown();

        while (ModKeys.NOTE_MI_LOW.consumeClick()) ModNetwork.sendNote(0, longVersion,currentShift());
        while (ModKeys.NOTE_LA.consumeClick())    ModNetwork.sendNote(1, longVersion,currentShift());
        while (ModKeys.NOTE_RE.consumeClick())     ModNetwork.sendNote(2, longVersion,currentShift());
        while (ModKeys.NOTE_SOL.consumeClick())    ModNetwork.sendNote(3, longVersion,currentShift());
        while (ModKeys.NOTE_SI.consumeClick())     ModNetwork.sendNote(4, longVersion,currentShift());
        while (ModKeys.NOTE_MI_HIGH.consumeClick())ModNetwork.sendNote(5, longVersion,currentShift());
    }
}