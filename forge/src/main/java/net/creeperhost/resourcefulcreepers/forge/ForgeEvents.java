package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event)
    {
        ModEntities.STORED_TYPES.forEach((entityResourcefulCreeperEntityType, integer) ->
        {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityResourcefulCreeperEntityType, integer, 1, 1));
        });
    }
}
