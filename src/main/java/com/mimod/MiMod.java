package com.mimod;

import com.mimod.entity.ModEntities;
import com.mimod.entity.client.renderer.chica.ChicaRenderer;
import com.mimod.entity.client.renderer.freddy.FreddyRenderer;
import com.mimod.modItems.FoxyHook;
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

    /*We declare foxy's hook*/
    public static final RegistryObject<Item> FOXY_HOOK =
            ITEMS.register("foxy_hook",
                    () -> new FoxyHook(new Item.Properties()));

    public MiMod()
    {
       //Se define el event bus
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

      //Se llama al metodo register de cada clase pasandole el modEventbus
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        /*Esta maldita linea es la que hace falta para que se muestre el item cuando se busca en creativo*/
        modEventBus.addListener(MiMod::addCreative);

/*This line registers every declared Item using ITEMS registry*/
      ITEMS.register(modEventBus);
    }

    @SubscribeEvent
    public static void addCreative(
            BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey()== CreativeModeTabs.COMBAT)
        {
            event.accept(FIRE_SWORD.get());
            event.accept(FOXY_HOOK.get());
        }
    }

    /**
     * Para que el event bus vaya descubriendo métodos a los que llamar
     * @param event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    /**
<<<<<<< HEAD
     * Registra en el cliente los metodos en las clases que tengan el @subscribeEvent y el modelo o que se yo.
     * IMPORTANTE METER TODAS LAS ENTIDADES
=======
     * Registra en el cliente los metodos en las clases que tengan el @subscribeEvent
>>>>>>> dev_foxy
     */
    @Mod.EventBusSubscriber(modid = MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

            EntityRenderers.register(ModEntities.CHICA.get(), ChicaRenderer::new);
            EntityRenderers.register(ModEntities.FREDDY.get(), FreddyRenderer::new);

        }
    }

}