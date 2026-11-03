package com.mimod.Goals;

import com.mimod.interfaces.Fleeing;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FleeingGoal<T extends PathfinderMob & Fleeing> extends Goal {
    private final T mob;
    private final double speed;
    private int fleeTicks;

    public FleeingGoal(T mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.isFleeing();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isFleeing() && fleeTicks > 0;
    }

    @Override
    public void start() {
        fleeTicks = mob.getFleeingTime();
        runAway();
    }

    @Override
    public void tick() {
        fleeTicks--;
        if (mob.getNavigation().isDone()) {
            runAway();
        }
    }

    private void runAway() {
        LivingEntity target = mob.getTarget();
        Vec3 from = target != null ? target.position() : mob.position();
        Vec3 pos = DefaultRandomPos.getPosAway(mob, 16, 7, from);
        if (pos != null) {
            mob.getNavigation().moveTo(pos.x, pos.y, pos.z, speed);
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        mob.setFleeing(false);
        mob.setAttackCooldown(mob.getMaxAttackCooldown());
    }
}