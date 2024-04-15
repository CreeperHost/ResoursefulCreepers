package net.creeperhost.resourcefulcreepers.fabric;

import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.biome.Biome;

public class ResourcefulCreepersFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ResourcefulCreepers.init();
        ModEntities.registerSpawns();
    }
}
