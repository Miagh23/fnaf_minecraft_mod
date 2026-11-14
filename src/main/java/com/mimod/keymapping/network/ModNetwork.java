package com.mimod.keymapping.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModNetwork {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath("mimod", "main"))
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {
        CHANNEL.play()
                .serverbound()
                .add(PlayNotePacket.class, PlayNotePacket.STREAM_CODEC, PlayNotePacket::handle);
    }

    public static void sendNote(int note, boolean longVersion) {
        CHANNEL.send(new PlayNotePacket(note, longVersion), PacketDistributor.SERVER.noArg());
    }
}