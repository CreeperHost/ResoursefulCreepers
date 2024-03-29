package net.creeperhost.resourcefulcreepers.neoforge;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.BiomeManager;

@Mod (Constants.MOD_ID)
public class ResourcefulCreepersNeoForge {
    public ResourcefulCreepersNeoForge() {
        // Submit our event bus to let architectury register our content on the right time
//        EventBuses.registerModEventBus(Constants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ResourcefulCreepers.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> NeoForgeClient::init);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonLoaded);
    }

    private void commonLoaded(final FMLCommonSetupEvent event) {
        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes) {
            if (creeperType.allowNaturalSpawns()) {
                ResourcefulCreepers.LOGGER.info("registering spawn for {}", creeperType.getDisplayName());
                ResourcefulCreepers.addSpawn(() -> ModEntities.CREEPERS.get(creeperType).get(), creeperType);
            }
        }
    }
}
