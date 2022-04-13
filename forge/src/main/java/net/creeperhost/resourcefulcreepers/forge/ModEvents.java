package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegistryEvent.Register<Item> event)
    {
        ModEntities.loadCreeperTypes();
    }
}
