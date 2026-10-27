package com.mimod;

import com.mimod.entity.ModEntities;
import com.mimod.entity.client.RhinoRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//Este es el archivo del mod en sí
@Mod(MiMod.MOD_ID)
public class MiMod {
    public static final String MOD_ID = "mimod";

    //LivingEntity si es una entidad como un mob
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> FIRE_SWORD=
            ITEMS.register("firesword",() -> new FireSword(new Item.Properties()));

    public MiMod()
    {
       //Se define el event bus
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

      //Se llama al metodo register de cada clase pasandole el modEventbus
        ModEntities.register(modEventBus);


      ITEMS.register(modEventBus);
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey()== CreativeModeTabs.COMBAT)
        {
            event.accept(FIRE_SWORD.get());
        }
    }

    /**
     * Para que el event bus vay descubriendo métodos a los que llamar
     * @param event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    /**
     * Registra en el cliente los metodos en las clases que tengan el @subscribeEvent
     */
    @Mod.EventBusSubscriber(modid = MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.RHINO.get(), RhinoRenderer::new);
        }
    }

}