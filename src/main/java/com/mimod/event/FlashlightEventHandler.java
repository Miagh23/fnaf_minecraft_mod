package com.mimod.event;



import com.mimod.modItems.FlashlightItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FlashlightEventHandler {

    @SubscribeEvent // escucha los eventos de tic del jugador
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return; // Si el tic está en la fase final, lo ignoramos para no duplicar trabajo innecesario
        Player player = event.player; // Obtenemos la instancia del jugador afectado por el tic
        Level level = player.level(); // Obtenemos el mundo en el que se encuentra el jugador

        if (level.isClientSide()) return; // Solo ejecutamos esta lógica en el servidor para evitar desyncs y conflictos multijugador

        ItemStack mainHand = player.getMainHandItem(); // Obtenemos el ítem que el jugador lleva en la mano principal
        ItemStack offHand = player.getOffhandItem(); // Obtenemos el ítem que el jugador lleva en la mano secundaria (mano torpe)

        ItemStack activeFlashlight = null; // Variable para almacenar la linterna si la encontramos activa
        if (mainHand.getItem() instanceof FlashlightItem) {
            activeFlashlight = mainHand; // Si la linterna está en la mano principal, la seleccionamos
        } else if (offHand.getItem() instanceof FlashlightItem) {
            activeFlashlight = offHand; // Si la linterna está en la mano secundaria, la seleccionamos
        }

        if (activeFlashlight != null) { // Si el jugador lleva una linterna en alguna mano
            CustomData customData = activeFlashlight.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY); // Obtenemos los datos personalizados del ítem
            CompoundTag tag = customData.copyTag(); // Copiamos el CompoundTag para leer sus propiedades NBT
            boolean isOn = tag.getBoolean("IsOn"); // Comprobamos si la linterna está encendida ("IsOn" == true)

            if (isOn) { // Si la linterna está encendida
                BlockPos currentPos = player.blockPosition().above(); // Calculamos la posición del bloque justo encima de los pies del jugador

                boolean posChanged = !tag.contains("LightX") || // Comprobamos si no hay luz registrada O si el jugador se ha movido de bloque
                        tag.getInt("LightX") != currentPos.getX() ||
                        tag.getInt("LightY") != currentPos.getY() ||
                        tag.getInt("LightZ") != currentPos.getZ();

                if (posChanged) { // Si el jugador se ha movido a una nueva posición
                    // Borrar luz anterior para que no deje rastro detrás
                    if (tag.contains("LightX")) {
                        BlockPos oldLight = new BlockPos(tag.getInt("LightX"), tag.getInt("LightY"), tag.getInt("LightZ"));
                        if (level.getBlockState(oldLight).is(Blocks.LIGHT)) { // Si el bloque anterior sigue siendo un bloque de luz del mod
                            level.setBlock(oldLight, Blocks.AIR.defaultBlockState(), 3); // Lo convertimos en aire (lo borramos)
                        }
                    }

                    // Poner luz nueva usando la propiedad LEVEL nativa de Minecraft 1.21
                    if (level.isEmptyBlock(currentPos) || level.getBlockState(currentPos).is(Blocks.LIGHT)) { // Si la posición está vacía o ya es luz
                        BlockState lightState = Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, 10); // Creamos un estado de luz con nivel 10 (tenue y realista, no de día)
                        level.setBlock(currentPos, lightState, 3); // Colocamos el bloque de luz invisible en el mundo

                        // Guardamos las nuevas coordenadas en el NBT de la linterna
                        tag.putInt("LightX", currentPos.getX());
                        tag.putInt("LightY", currentPos.getY());
                        tag.putInt("LightZ", currentPos.getZ());
                        activeFlashlight.set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); // Guardamos el NBT actualizado en el ítem
                    }
                }
            }
        }
    }
}