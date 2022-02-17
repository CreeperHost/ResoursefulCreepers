package net.creeperhost.resourcefulcreepers.fabric;

import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourcefulCreepersExpectPlatformImpl
{
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void registerSpawns(EntityType<EntityResourcefulCreeper> entityType, int weight)
    {
        BiomeModifications.addSpawn(biomeSelectionContext -> biomeSelectionContext.getBiome().getBiomeCategory() != Biome.BiomeCategory.NETHER,
                MobCategory.MONSTER, entityType, weight, 1, 1);
    }

    public static int getColour(ItemStack itemStack)
    {
        return 0;
    }

    //TODO
    public static List<Block> getDefaults()
    {
        return Collections.EMPTY_LIST;
    }

    //TODO
    public static List<Tier> getTierList()
    {
        return Collections.EMPTY_LIST;
    }

    //TODO
    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState)
    {
        return false;
    }
}
