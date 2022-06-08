package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(NewRegistryEvent event)
    {
//        ModEntities.loadCreeperTypes();
    }
}
