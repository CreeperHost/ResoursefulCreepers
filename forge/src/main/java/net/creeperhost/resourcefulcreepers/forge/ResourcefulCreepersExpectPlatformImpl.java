package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.ResourcefulCreepersExpectPlatform;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ResourcefulCreepersExpectPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void registerSpawns(EntityType<EntityResourcefulCreeper> entityType, int weight)
    {
        SpawnPlacements.register(entityType, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ResourcefulCreepersExpectPlatformImpl::genericGroundSpawn);
    }

    public static boolean genericGroundSpawn(EntityType<? extends Entity> entityType, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random)
    {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn((ServerLevelAccessor) worldIn, pos, random) && worldIn.getBlockState(pos).isValidSpawn(worldIn, pos, entityType);
    }

    public static List<Block> getDefaults()
    {
        return Tags.Blocks.ORES.getValues();
    }

    public static int getColour(ItemStack itemStack)
    {
        if (Minecraft.getInstance().getItemColors() != null)
        {
            return Minecraft.getInstance().getItemColors().getColor(itemStack, 0);
        }
        return 0;
    }

    public static List<Tier> getTierList()
    {
        return TierSortingRegistry.getSortedTiers();
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState)
    {
        return TierSortingRegistry.isCorrectTierForDrops(tier, blockState);
    }
}
