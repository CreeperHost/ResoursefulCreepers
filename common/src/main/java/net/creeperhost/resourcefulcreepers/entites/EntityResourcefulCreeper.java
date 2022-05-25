package net.creeperhost.resourcefulcreepers.entites;

import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.config.Config;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.entites.goals.RcSwellGoal;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityResourcefulCreeper extends Animal implements PowerableMob
{
    private CreeperType creeperType;
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR;
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED;
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED;
    private static final EntityDataAccessor<Boolean> DATA_IS_TAMED;

    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;
    private final EntityType<?> entityType;

    static
    {
        DATA_SWELL_DIR = SynchedEntityData.defineId(EntityResourcefulCreeper.class, EntityDataSerializers.INT);
        DATA_IS_POWERED = SynchedEntityData.defineId(EntityResourcefulCreeper.class, EntityDataSerializers.BOOLEAN);
        DATA_IS_IGNITED = SynchedEntityData.defineId(EntityResourcefulCreeper.class, EntityDataSerializers.BOOLEAN);
        DATA_IS_TAMED = SynchedEntityData.defineId(EntityResourcefulCreeper.class, EntityDataSerializers.BOOLEAN);
    }

    public EntityResourcefulCreeper(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.entityType = entityType;
        String[] split = entityType.getDescriptionId().split("\\.");
        if(split.length > 2)
        {
            String name = split[2];
            CreeperType creeperType = ResourcefulCreepers.getTypeFromName(name);
            if(creeperType != null)
            {
                this.creeperType = creeperType;
            }
        }
    }

    @Override
    public Component getName()
    {
        if(level.isClientSide && creeperType != null)
        {
            if(hasCustomName())
            {
                return getCustomName();
            }
            return new TranslatableComponent(creeperType.getDisplayName());
        }
        if(super.getName() == null)
        {
            return new TextComponent("null");
        }
        return super.getName();
    }

    public CreeperType getCreeperType()
    {
        return creeperType;
    }

    @Override
    public void registerGoals()
    {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RcSwellGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        if(Config.INSTANCE.creepersAttractedToArmourStand) this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ArmorStand.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder prepareAttributes(CreeperType creeperType)
    {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.1D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.25D)
                .add(Attributes.ARMOR, creeperType.getArmourValue())
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.FOLLOW_RANGE, 15.0D);
    }

    @Override
    public void tick()
    {
        if (this.isAlive())
        {
            if(Config.INSTANCE.nonHostileWhenTamed && this.isTamed() && this.getTarget() != null && this.getTarget() instanceof Player)
            {
                this.setTarget(null);
            }
            if(!this.shouldExplode())
            {
                this.setSwellDir(-1);
            }
            this.oldSwell = this.swell;
            if (this.isIgnited())
            {
                this.setSwellDir(1);
            }
            int i = this.getSwellDir();
            if (i > 0 && this.swell == 0)
            {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
            this.swell += i;
            if (this.swell < 0)
            {
                this.swell = 0;
            }
            if (this.swell > this.maxSwell || level.isClientSide && this.swell >= this.maxSwell -1)
            {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    public boolean shouldExplode()
    {
        if(Config.INSTANCE.nonHostileWhenTamed && isTamed()) return false;

        return true;
    }

    private void explodeCreeper()
    {
        if (Config.INSTANCE.explosionsGenerateOres)
        {
            float f = this.isPowered() ? Config.INSTANCE.poweredExplosionMultiplier : Config.INSTANCE.explosionMultiplier;
            this.dead = true;
            if(isBaby()) f = f / 2;
            double x = getX();
            double y = getY();
            double z = getZ();

            if(level.isClientSide)
            {
                this.level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            }
            Explosion explosion = new Explosion(this.level, this, x, y, z, (float) this.explosionRadius * f);
            explosion.explode();
            Block block = Blocks.AIR;
            for (ItemStack itemStack : getCreeperType().getItemDropsAsList())
            {
                if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof BlockItem blockItem)
                {
                    block = blockItem.getBlock();
                    break;
                }
            }
            if(!level.isClientSide)
            {
                for (BlockPos blockPos : explosion.getToBlow())
                {
                    if (Config.INSTANCE.forceAirBlock && level.getBlockState(blockPos).getBlock() == Blocks.AIR || level.getBlockState(blockPos).isAir() || level.getBlockState(blockPos).is(BlockTags.REPLACEABLE_PLANTS) || level.getBlockState(blockPos).is(BlockTags.SNOW))
                    {
                        level.setBlock(blockPos, block.defaultBlockState(), 3);
                    }
                }
            }
            this.discard();
        } else
        {
            Explosion.BlockInteraction blockInteraction = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            float f = this.isPowered() ? 2.0f : 1.0f;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, blockInteraction);
            this.discard();
        }
    }

    public boolean isIgnited()
    {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite()
    {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    public boolean isPowered()
    {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public void setPowered()
    {
        this.entityData.set(DATA_IS_POWERED, true);
    }

    public int getSwellDir()
    {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int i)
    {
        this.entityData.set(DATA_SWELL_DIR, i);
    }

    public float getSwelling(float f)
    {
        return Mth.lerp(f, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
    }

    public boolean isTamed()
    {
        return this.entityData.get(DATA_IS_TAMED);
    }

    public void setTamed()
    {
        this.entityData.set(DATA_IS_TAMED, true);
    }

    public void removedTamed()
    {
        this.entityData.set(DATA_IS_TAMED, false);
    }

    @Override
    public void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl)
    {
        super.dropCustomDeathLoot(damageSource, i, bl);

        if(getCreeperType().shouldDropItemsOnDeath() && !Config.INSTANCE.overrideOreDrops)
        {
            for (ItemStack itemStack : getCreeperType().getItemDropsAsList())
            {
                if (itemStack != null && !itemStack.isEmpty())
                {
                    this.spawnAtLocation(itemStack);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f)
    {
        if(damageSource.getEntity() != null && damageSource.getEntity() instanceof Player)
        {
            removedTamed();
        }
        return super.hurt(damageSource, f);
    }

    //Make our creepers drop Vanilla creepers loot
    @Override
    public ResourceLocation getDefaultLootTable()
    {
        ResourceLocation resourcelocation = new ResourceLocation("minecraft:creeper");
        return new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
    }

    @Override
    public boolean isFood(ItemStack itemStack)
    {
        boolean is = itemStack.is(Items.GUNPOWDER);
        if(is)
        {
            setTamed();
        }
        return is;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
    {
        int currentTier = getCreeperType().getTier();
        int nextTier = currentTier+1;
        AgeableMob baby = (AgeableMob) entityType.create(serverLevel);

        boolean mutation = serverLevel.random.nextInt(100) < 10;
        if(mutation)
        {
            List<EntityType<?>> possible = getFromTier(nextTier, level);
            if (!possible.isEmpty())
            {
                int random = serverLevel.random.nextInt(possible.size());
                baby = (AgeableMob) possible.get(random).create(serverLevel);
            }
        }
        if(ageableMob instanceof EntityResourcefulCreeper entityResourcefulCreeper)
        {
            entityResourcefulCreeper.setTamed();
        }
        return baby;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel serverLevel, Animal animal)
    {
        AgeableMob ageableMob = this.getBreedOffspring(serverLevel, animal);
        if (ageableMob != null) {
            ServerPlayer serverPlayer = this.getLoveCause();
            if (serverPlayer == null && animal.getLoveCause() != null) {
                serverPlayer = animal.getLoveCause();
            }

            if (serverPlayer != null) {
                serverPlayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this, animal, ageableMob);
            }

            this.setAge(6000);
            animal.setAge(6000);
            this.resetLove();
            animal.resetLove();
            ageableMob.setBaby(true);
            ageableMob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            if(ageableMob instanceof EntityResourcefulCreeper entityResourcefulCreeper)
            {
                entityResourcefulCreeper.setTamed();
            }
            serverLevel.addFreshEntityWithPassengers(ageableMob);
            serverLevel.broadcastEntityEvent(this, (byte)18);
            if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }

        }
    }

    public List<EntityType<?>> getFromTier(int tier, Level level)
    {
        List<EntityType<?>> list = new ArrayList<>();
        ModEntities.STORED_TYPES.forEach((entityType, integer) ->
        {
            EntityResourcefulCreeper creeper = (EntityResourcefulCreeper) entityType.create(level);
            if(creeper.getCreeperType().getTier() == tier)
            {
                list.add(entityType);
            }
        });

        return list;
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions)
    {
        return this.isBaby() ? entityDimensions.height * 0.85F : 1.45F;
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_IS_IGNITED, false);
        this.entityData.define(DATA_IS_TAMED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag)
    {
        super.addAdditionalSaveData(compoundTag);
        if (this.entityData.get(DATA_IS_POWERED))
        {
            compoundTag.putBoolean("powered", true);
        }
        compoundTag.putShort("Fuse", (short)this.maxSwell);
        compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compoundTag.putBoolean("ignited", this.isIgnited());
        compoundTag.putBoolean("tamed", this.isTamed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag)
    {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_IS_POWERED, compoundTag.getBoolean("powered"));
        if (compoundTag.contains("Fuse", 99))
        {
            this.maxSwell = compoundTag.getShort("Fuse");
        }

        if (compoundTag.contains("ExplosionRadius", 99))
        {
            this.explosionRadius = compoundTag.getByte("ExplosionRadius");
        }
        if (compoundTag.getBoolean("ignited"))
        {
            this.ignite();
        }
        if (compoundTag.getBoolean("tamed"))
        {
            this.setTamed();
        }
    }

    @Override
    public boolean shouldDespawnInPeaceful()
    {
        //Only allow entity to despawn when the game is switched to peaceful if creeper is not tamed
        if(isTamed()) return false;
        if(isBaby()) return false;
        return true;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource)
    {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    public SoundEvent getDeathSound()
    {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType)
    {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader arg)
    {
        return !arg.containsAnyLiquid(this.getBoundingBox()) && arg.isUnobstructed(this);
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 4;
    }

    @Override
    public boolean removeWhenFarAway(double d)
    {
        return true;
    }
}
