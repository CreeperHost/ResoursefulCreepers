package net.creeperhost.resourcefulcreepers.forge;

import dev.architectury.platform.forge.EventBuses;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class ResourcefulCreepersForge
{
    public ResourcefulCreepersForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Constants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ResourcefulCreepers.init();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
