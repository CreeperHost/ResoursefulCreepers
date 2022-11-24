package net.creeperhost.resourcefulcreepers.entites.goals;

import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class RcBreedGoal extends Goal
{
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected final EntityResourcefulCreeper creeper;
    private final Class<? extends Animal> partnerClass;
    protected final Level level;
    @Nullable
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;

    public RcBreedGoal(EntityResourcefulCreeper creeper, double d)
    {
        this(creeper, d, creeper.getClass());
    }

    public RcBreedGoal(EntityResourcefulCreeper creeper, double d, Class<? extends Animal> class_)
    {
        this.creeper = creeper;
        this.level = creeper.level;
        this.partnerClass = class_;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse()
    {
        if (!this.creeper.isInLove())
        {
            return false;
        }
        else
        {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse()
    {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }

    public void stop()
    {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick()
    {
        this.creeper.getLookControl().setLookAt(this.partner, 10.0F, (float)this.creeper.getMaxHeadXRot());
        this.creeper.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.creeper.distanceToSqr(this.partner) < 9.0D)
        {
            this.breed();
        }
    }

    @Nullable
    private Animal getFreePartner()
    {
        List<? extends Animal> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.creeper, this.creeper.getBoundingBox().inflate(8.0D));
        double d = 1.7976931348623157E308D;
        EntityResourcefulCreeper animal = null;
        Iterator var5 = list.iterator();

        while(var5.hasNext())
        {
            EntityResourcefulCreeper animal2 = (EntityResourcefulCreeper) var5.next();
            if (this.creeper.canMate(animal2) && this.creeper.distanceToSqr(animal2) < d && this.creeper.getCreeperType().getTier() == animal2.getCreeperType().getTier())
            {
                animal = animal2;
                d = this.creeper.distanceToSqr(animal2);
            }
        }
        return animal;
    }

    protected void breed()
    {
        this.creeper.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
    }
}
