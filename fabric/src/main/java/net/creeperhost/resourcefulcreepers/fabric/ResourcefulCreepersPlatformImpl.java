package net.creeperhost.resourcefulcreepers.fabric;

import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

    //TODO
    public static List<Tier> getTierList() {
        return Collections.EMPTY_LIST;
    }

    //TODO
    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState) {
        return false;
    }

    public static <T extends Animal> void addSpawn(Supplier<EntityType<T>> entityType, CreeperType creeperType) {
        try {
            List<TagKey<Biome>> tags = creeperType.getBiomesTags().stream().map(e -> TagKey.create(Registries.BIOME, new ResourceLocation(e))).toList();
            BiomeModifications.addSpawn(e -> tags.stream().anyMatch(e::hasTag), MobCategory.MONSTER, entityType.get(), creeperType.getSpawnWeight(), creeperType.getMinGroup(), creeperType.getMaxGroup());
            SpawnPlacements.register(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, serverLevelAccessor, mobSpawnType, blockPos, randomSource) -> ModEntities.checkMonsterSpawnRules(type, serverLevelAccessor, mobSpawnType, blockPos, randomSource, creeperType));
        } catch (Exception e) {
            ResourcefulCreepers.LOGGER.error("Failed to register creeper type: {}", creeperType.getName(), e);
        }
    }
}
