package com.mimod.keymapping.client;


import com.mimod.modItems.GuitarPick;
import com.mimod.keymapping.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mimod", value = Dist.CLIENT)
public class ClientKeyHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = Minecraft.getInstance().player;
        if (player == null || !GuitarPick.isPlayingGuitar(player)) return;

        boolean longVersion = ModKeys.LONG_MODIFIER.isDown();

        while (ModKeys.NOTE_MI_LOW.consumeClick()) ModNetwork.sendNote(0, longVersion);
        while (ModKeys.NOTE_LA.consumeClick())   {
            System.out.println("[CLIENTE] Nota LA | long=" + longVersion);
            ModNetwork.sendNote(1, longVersion);

        }
        while (ModKeys.NOTE_RE.consumeClick())     ModNetwork.sendNote(2, longVersion);
        while (ModKeys.NOTE_SOL.consumeClick())    ModNetwork.sendNote(3, longVersion);
        while (ModKeys.NOTE_SI.consumeClick())     ModNetwork.sendNote(4, longVersion);
        while (ModKeys.NOTE_MI_HIGH.consumeClick())ModNetwork.sendNote(5, longVersion);
    }
}