package com.mimod.keymapping.network;

import com.mimod.ModSounds;
import com.mimod.modItems.GuitarPick;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Debug;

import java.util.List;

public record PlayNotePacket(int note, boolean longVersion, int shift) {


    public static final StreamCodec<RegistryFriendlyByteBuf, PlayNotePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PlayNotePacket::note,
                    ByteBufCodecs.BOOL, PlayNotePacket::longVersion,
                    ByteBufCodecs.INT, PlayNotePacket::shift,
                    PlayNotePacket::new
            );



    public static void handle(PlayNotePacket msg, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;

        ctx.enqueueWork(() -> {
            if (!GuitarPick.isPlayingGuitar(player)) return;

            System.out.println("[SERVIDOR] note=" + msg.note()
                    + " long=" + msg.longVersion());

            List<RegistryObject<SoundEvent>> notes = msg.longVersion()
                    ? ModSounds.GUITAR_RIFFS_LONG
                    : ModSounds.GUITAR_NOTES;

            // Validar que el índice existe en la lista de sonidos
            if (msg.note() < 0 || msg.note() >= ModSounds.GUITAR_NOTES.size()) return;

            // Validar el shift (rango permitido: -7 a +7)
            if (msg.shift() < -7 || msg.shift() > 7) return;

            float pitch = (float) Math.pow(2.0, msg.shift() / 12.0);//calcula el semitono para esta escala

            SoundEvent sound = notes.get(msg.note()).get();
            System.out.println("[SERVIDOR] Reproduciendo: " + sound.getLocation());
            player.level().playSound(null, player.blockPosition(),
                    sound, SoundSource.PLAYERS, 1.0F, pitch);
        });
        ctx.setPacketHandled(true);
    }
}