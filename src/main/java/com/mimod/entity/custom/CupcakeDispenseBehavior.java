package com.mimod.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class CupcakeDispenseBehavior extends DefaultDispenseItemBehavior {

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        Level level = source.level();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        Position pos = DispenserBlock.getDispensePosition(source);

        Vec3 dir = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        ChicaEntity.Cupcake cupcake =
                new ChicaEntity.Cupcake(level, pos.x(), pos.y(), pos.z(), dir.normalize());
        level.addFreshEntity(cupcake);

        stack.shrink(1); // consume un cupcake
        return stack;
    }

    @Override
    protected void playSound(BlockSource source) {
        // mismo sonido que el plato
        source.level().playSound(null, source.pos(),
                SoundEvents.CROSSBOW_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}