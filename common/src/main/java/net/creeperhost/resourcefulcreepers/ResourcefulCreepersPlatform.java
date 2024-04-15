package net.creeperhost.resourcefulcreepers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class ResourcefulCreepersPlatform {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<Block> getDefaults() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getColour(ItemStack itemStack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<Tier> getTierList() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Animal> void addSpawn(Supplier<EntityType<T>> entityType, CreeperType creeperType) {
        throw new AssertionError();
    }
}
