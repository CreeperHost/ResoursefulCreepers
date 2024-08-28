package net.creeperhost.resourcefulcreepers.neoforge;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.CreeperBuilder;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod (Constants.MOD_ID)
public class ResourcefulCreepersNeoForge {
    public ResourcefulCreepersNeoForge(IEventBus modBus) {
        ResourcefulCreepers.init();
        modBus.addListener(this::registerSpawns);

        NeoForge.EVENT_BUS.addListener(this::serverStarted);

        if (FMLEnvironment.dist.isClient()) {
            NeoForgeClient.init(modBus);
        }
    }

    private void registerSpawns(SpawnPlacementRegisterEvent event) {
        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes) {
            if (creeperType.allowNaturalSpawns()) {
                ResourcefulCreepers.LOGGER.info("registering spawn for {}", creeperType.getDisplayName());
                event.register(ModEntities.CREEPERS.get(creeperType).get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, spawnType, blockPos, randomSource) ->  ModEntities.checkMonsterSpawnRules(type, level, spawnType, blockPos, randomSource, creeperType), SpawnPlacementRegisterEvent.Operation.OR);
            }
        }
    }

    private void serverStarted(ServerStartedEvent event) {
        CreeperBuilder.onServerStarted(event.getServer());
    }
}
