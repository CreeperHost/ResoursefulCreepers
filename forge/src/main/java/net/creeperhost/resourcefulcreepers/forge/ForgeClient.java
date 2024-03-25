package net.creeperhost.resourcefulcreepers.forge;

import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by brandon3055 on 24/03/2024
 */
public class ForgeClient {

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeClient::registerColours);
    }

    //TODO, Figure out a fabric equivalent
    private static void registerColours(RegisterColorHandlersEvent.Item event) {
        ModEntities.EGGS.forEach((creeperType, itemSupplier) -> event.register(itemSupplier.get()::getColor, itemSupplier.get()));
    }
}
