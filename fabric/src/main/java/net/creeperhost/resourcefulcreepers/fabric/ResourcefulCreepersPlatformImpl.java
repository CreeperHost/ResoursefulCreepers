package net.creeperhost.resourcefulcreepers.fabric;

import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ResourcefulCreepersPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static int getColour(ItemStack itemStack) {
        return 0;
    }

    //TODO
    public static List<Block> getDefaults() {
        return Collections.EMPTY_LIST;
    }

    public static boolean isOre(BlockState state) {
        return state.is(ConventionalBlockTags.ORES);
    }

//    //TODO
//    public static List<Tier> getTierList() {
//        return Collections.EMPTY_LIST;
//    }
//
//    //TODO
//    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState) {
//        return false;
//    }
}
