package net.creeperhost.resourcefulcreepers.fabric;

import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
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
import java.util.function.Predicate;

public class ResourcefulCreepersExpectPlatformImpl
{
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void registerSpawns(EntityType<EntityResourcefulCreeper> entityType, int weight)
    {
        finalBiomeExclusion = new ArrayList<>();

        Predicate<BiomeSelectionContext> spawnPredicate = overWorldNoSwampNoGoZones();
        BiomeModifications.addSpawn(spawnPredicate, MobCategory.MONSTER, entityType, weight, 1, 1);
    }

    //TODO Move to PolyLib
    static ArrayList<ResourceLocation> finalBiomeExclusion;

    public static Predicate<BiomeSelectionContext> overWorldNoSwampNoGoZones() {
        Predicate<BiomeSelectionContext> excluded = Predicate.not(exclusion());
        return BiomeSelectors.all().and(shroomExclusion()).and(netherExclusion()).and(endExclusion()).and(swampExclusion()).and(excluded);
    }

    public static Predicate<BiomeSelectionContext> exclusion() {
        return context -> finalBiomeExclusion.contains(context.getBiomeKey().location());
    }

    public static Predicate<BiomeSelectionContext> shroomExclusion() {
        return Predicate.not(BiomeSelectors.categories(Biome.BiomeCategory.MUSHROOM));
    }

    public static Predicate<BiomeSelectionContext> netherExclusion() {
        return Predicate.not(BiomeSelectors.categories(Biome.BiomeCategory.NETHER));
    }

    public static Predicate<BiomeSelectionContext> endExclusion() {
        return Predicate.not(BiomeSelectors.categories(Biome.BiomeCategory.THEEND));
    }

    public static Predicate<BiomeSelectionContext> swampExclusion() {
        return Predicate.not(BiomeSelectors.categories(Biome.BiomeCategory.SWAMP));
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

    public static void unfreezeRegistry()
    {
    }
}
