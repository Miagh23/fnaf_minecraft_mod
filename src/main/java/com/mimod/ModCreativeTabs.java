package com.mimod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MiMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MIMOD_TAB =
            CREATIVE_TABS.register("mimod_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.mimod.mimod_tab"))
                    .icon(() -> new ItemStack(MiMod.FREDDY_MASK.get()))
                    .displayItems((parameters, output) -> {
                        // Añade automáticamente TODOS los items registrados en MiMod.ITEMS
                        MiMod.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
