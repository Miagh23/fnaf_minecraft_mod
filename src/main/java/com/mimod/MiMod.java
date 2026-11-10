package com.mimod;

import com.mimod.entity.ModEntities;
import com.mimod.entity.client.renderer.chica.ChicaRenderer;
import com.mimod.entity.client.renderer.freddy.FreddyRenderer;
import com.mimod.modItems.BonnieGuitar;
import com.mimod.modItems.CupcakePlate;
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
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

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

    public static final RegistryObject<Item> BONNIE_GUITAR =
            ITEMS.register("bonnie_guitar",
                    () -> new BonnieGuitar(new Item.Properties()));

    public static final RegistryObject<Item> CUPCAKE_PLATE =
            ITEMS.register("cupcake_plate",
                    () -> new CupcakePlate(new Item.Properties()));

    public static final RegistryObject<Item> CUPCAKE =
            ITEMS.register("cupcake",
                    () -> new Item(new Item.Properties().stacksTo(64)));

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
            event.accept(BONNIE_GUITAR.get());
            event.accept(CUPCAKE_PLATE.get());
            event.accept(CUPCAKE.get());
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
    @Mod.EventBusSubscriber(modid = MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.CHICA.get(), ChicaRenderer::new);
            EntityRenderers.register(ModEntities.FREDDY.get(), FreddyRenderer::new);

            event.enqueueWork(() -> {
                ItemProperties.register(CUPCAKE_PLATE.get(),
                        ResourceLocation.withDefaultNamespace("pulling"),
                        (stack, level, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

                ItemProperties.register(CUPCAKE_PLATE.get(),
                        ResourceLocation.withDefaultNamespace("pull"),
                        (stack, level, entity, seed) -> {
                            if (entity == null || !entity.isUsingItem() || entity.getUseItem() != stack) return 0.0F;
                            float ticks = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
                            return Math.min(ticks / (float) CupcakePlate.CHARGE_TICKS, 1.0F);
                        });

                ItemProperties.register(CUPCAKE_PLATE.get(),
                        ResourceLocation.withDefaultNamespace("charged"),
                        (stack, level, entity, seed) -> CupcakePlate.isLoaded(stack) ? 1.0F : 0.0F);
            });
        }
    }
}
