package net.creeperhost.resourcefulcreepers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourcefulCreepersExpectPlatform
{
    @ExpectPlatform
    public static Path getConfigDirectory()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSpawns(EntityType<EntityResourcefulCreeper> entityType, int weight) {}

    @ExpectPlatform
    public static List<Block> getDefaults()
    {
        return Collections.EMPTY_LIST;
    }

    @ExpectPlatform
    public static int getColour(ItemStack itemStack)
    {
        return 0;
    }

    @ExpectPlatform
    public static List<Tier> getTierList()
    {
        return new ArrayList<>();
    }

    @ExpectPlatform
    public static boolean isCorrectTierForDrops(Tier tier, BlockState blockState)
    {
        return false;
    }
}
