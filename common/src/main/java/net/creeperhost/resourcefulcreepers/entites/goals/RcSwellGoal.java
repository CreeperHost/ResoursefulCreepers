package net.creeperhost.resourcefulcreepers.entites.goals;

import net.creeperhost.resourcefulcreepers.config.Config;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RcSwellGoal extends Goal
{
    private final EntityResourcefulCreeper creeper;

    @Nullable
    private LivingEntity target;

    public RcSwellGoal(EntityResourcefulCreeper creeper)
    {
        this.creeper = creeper;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse()
    {
        LivingEntity livingEntity = this.creeper.getTarget();
        if(Config.INSTANCE.nonHostileWhenTamed && this.creeper.isTamed()) return false;
        return this.creeper.getSwellDir() > 0 || livingEntity != null && this.creeper.distanceToSqr(livingEntity) < 9.0D;
    }

    public void start()
    {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    public void stop() {
        this.target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick()
    {
        if (this.target == null)
        {
            this.creeper.setSwellDir(-1);
        }
        else if (this.creeper.distanceToSqr(this.target) > 49.0D)
        {
            this.creeper.setSwellDir(-1);
        }
        else if (!this.creeper.getSensing().hasLineOfSight(this.target))
        {
            this.creeper.setSwellDir(-1);
        }
        else
        {
            this.creeper.setSwellDir(1);
        }
    }
}
