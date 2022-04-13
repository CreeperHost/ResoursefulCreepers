package net.creeperhost.resourcefulcreepers.forge;

import io.sentry.Sentry;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event)
    {
        ModEntities.STORED_TYPES.forEach((entityResourcefulCreeperEntityType, integer) ->
        {
            try
            {
                event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityResourcefulCreeperEntityType, integer, 1, 1));
            } catch (Exception e)
            {
                Sentry.captureException(e);
                e.printStackTrace();
            }
        });
    }
}
