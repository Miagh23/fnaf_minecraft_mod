package com.mimod.modItems;

import com.mimod.MiMod;
import com.mimod.entity.custom.ChicaEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CupcakePlate extends Item {
    public static final int CHARGE_TICKS = 40;
    public CupcakePlate(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isLoaded(stack)) {
            performShooting(level, player, hand, stack);
            return InteractionResultHolder.consume(stack);
        } else if (!findCupcake(player).isEmpty() || player.getAbilities().instabuild) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int chargeTime = this.getUseDuration(stack, entity) - timeLeft;

            if (chargeTime >= CHARGE_TICKS && !isLoaded(stack)) {
                if (!level.isClientSide()) {
                    ItemStack cupcake = consumeCupcake(player);
                    if (!cupcake.isEmpty() || player.getAbilities().instabuild) {
                        setLoaded(stack, true);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    private void performShooting(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();

            ChicaEntity.Cupcake cupcakeEntity = new ChicaEntity.Cupcake(level, player, lookVec.normalize());
            level.addFreshEntity(cupcakeEntity);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

            setLoaded(stack, false);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    public static boolean isLoaded(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getBoolean("IsLoaded");
    }

    public static void setLoaded(ItemStack stack, boolean loaded) {
        stack.update(DataComponents.CUSTOM_DATA,
                CustomData.EMPTY,
                data -> data.update(tag -> tag.putBoolean("IsLoaded", loaded)));
    }

    private ItemStack findCupcake(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.isEmpty() && s.is(MiMod.CUPCAKE.get())) {
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack consumeCupcake(Player player) {
        if (player.getAbilities().instabuild) {
            return new ItemStack(MiMod.CUPCAKE.get());
        }
        ItemStack cupcake = findCupcake(player);
        if (!cupcake.isEmpty()) {
            return cupcake.split(1);
        }
        return ItemStack.EMPTY;
    }
}