package com.mimod.keymapping.network;

import com.mimod.ModSounds;
import com.mimod.modItems.GuitarPick;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record PlayNotePacket(int note) {

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayNotePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PlayNotePacket::note,
                    PlayNotePacket::new
            );

    public static void handle(PlayNotePacket msg, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;

        ctx.enqueueWork(() -> {
            if (!GuitarPick.isPlayingGuitar(player)) return;

            // Validar que el índice existe en la lista de sonidos
            if (msg.note() < 0 || msg.note() >= ModSounds.GUITAR_NOTES.size()) return;

            SoundEvent sound = ModSounds.GUITAR_NOTES.get(msg.note()).get();
            player.level().playSound(null, player.blockPosition(),
                    sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        });
        ctx.setPacketHandled(true);
    }
}