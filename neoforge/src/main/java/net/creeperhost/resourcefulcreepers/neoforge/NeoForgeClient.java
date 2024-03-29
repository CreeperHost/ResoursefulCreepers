package net.creeperhost.resourcefulcreepers.neoforge;

import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Created by brandon3055 on 24/03/2024
 */
public class NeoForgeClient {

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(NeoForgeClient::registerColours);
    }

    //TODO, Figure out a fabric equivalent
    private static void registerColours(RegisterColorHandlersEvent.Item event) {
        ModEntities.EGGS.forEach((creeperType, itemSupplier) -> event.register(itemSupplier.get()::getColor, itemSupplier.get()));
    }
}
