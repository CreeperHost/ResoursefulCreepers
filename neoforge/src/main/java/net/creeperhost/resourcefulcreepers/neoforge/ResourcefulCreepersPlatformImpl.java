package net.creeperhost.resourcefulcreepers.neoforge;

import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.TierSortingRegistry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ResourcefulCreepersPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static List<Block> getDefaults()
    {
        TagKey<Block> tag = Tags.Blocks.ORES;
        Iterable<Holder<Block>> i = BuiltInRegistries.BLOCK.getTagOrEmpty(tag);
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

    public static <T extends Animal> void addSpawn(Supplier<EntityType<T>> entityType, CreeperType creeperType) {
        SpawnPlacements.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, serverLevelAccessor, mobSpawnType, blockPos, randomSource) -> ModEntities.checkMonsterSpawnRules(type, serverLevelAccessor, mobSpawnType, blockPos, randomSource, creeperType));
    }
}
