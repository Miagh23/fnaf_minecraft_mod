package com.mimod.modItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FlashlightItem extends Item {
    public FlashlightItem(Properties properties) {
        super(properties); // Constructor que inicializa las propiedades base del ítem (como durabilidad o grupo en el inventario)
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand); // Obtenemos el ítem con el que se ha hecho clic derecho

        if (!level.isClientSide()) { // Ejecutamos la lógica de cambio de estado solo en el servidor para evitar duplicidades
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY); // Obtenemos los datos actuales o un objeto vacío
            CompoundTag tag = customData.copyTag(); // Copiamos el tag para poder modificar sus valores

            boolean currentStatus = tag.getBoolean("IsOn"); // Leemos el estado actual ("IsOn": true o false)
            boolean newStatus = !currentStatus; // Invertimos el estado (si estaba apagada se enciende, y viceversa)

            tag.putBoolean("IsOn", newStatus); // Guardamos el nuevo estado booleano en el tag

            // Si la apagamos, limpiamos cualquier bloque de luz que hubiera dejado activo en el mundo
            if (!newStatus && tag.contains("LightX")) {
                BlockPos oldLightPos = new BlockPos(tag.getInt("LightX"), tag.getInt("LightY"), tag.getInt("LightZ"));
                if (level.getBlockState(oldLightPos).is(Blocks.LIGHT)) { // Si el bloque en esa posición es un bloque de luz
                    level.setBlock(oldLightPos, Blocks.AIR.defaultBlockState(), 3); // Lo borramos convirtiéndolo en aire
                }
                tag.remove("LightX"); // Borramos la coordenada X del NBT
                tag.remove("LightY"); // Borramos la coordenada Y del NBT
                tag.remove("LightZ"); // Borramos la coordenada Z del NBT
            }

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); // Guardamos los datos NBT actualizados de vuelta en el ítem
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide()); // Devuelve éxito en la interacción para que la mano reproduzca su animación correctamente
    }
}