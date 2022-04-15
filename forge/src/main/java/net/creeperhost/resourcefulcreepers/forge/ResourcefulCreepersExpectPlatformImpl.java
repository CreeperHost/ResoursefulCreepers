package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
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
        SpawnPlacements.register(entityType, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, ResourcefulCreepersExpectPlatformImpl::genericGroundSpawn);
    }

    public static boolean genericGroundSpawn(EntityType<? extends Entity> entityType, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random)
    {
        if (worldIn.getBrightness(LightLayer.SKY, pos) > random.nextInt(32))
        {
            return false;
        }
        else if (worldIn.getBrightness(LightLayer.BLOCK, pos) > 0)
        {
            return false;
        }
        return true;
    }

    public static List<Block> getDefaults()
    {
        TagKey<Block> tag = Tags.Blocks.ORES;
        Iterable<Holder<Block>> i = Registry.BLOCK.getTagOrEmpty(tag);
        List<Block> blockList = new ArrayList<>();
        for (Holder<Block> blockHolder : i)
        {
            blockList.add(blockHolder.value());
        }
        System.out.println(blockList);
        return blockList;
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
