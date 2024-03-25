package net.creeperhost.resourcefulcreepers.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperRender;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.creeperhost.resourcefulcreepers.items.CreeperSpawnEgg;
import net.fabricmc.api.EnvType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registries.ENTITY_TYPE);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);

    public static final Map<CreeperType, Supplier<EntityType<EntityResourcefulCreeper>>> CREEPERS = new HashMap<>();
    public static final HashMap<CreeperType, Supplier<CreeperSpawnEgg>> EGGS = new HashMap<>();

    static {
        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes) {
            ResourcefulCreepers.LOGGER.info("registering entity for {}", creeperType.getDisplayName());
            CREEPERS.put(creeperType, ENTITIES.register(creeperType.getName(), () -> {
                        EntityType<EntityResourcefulCreeper> type = EntityType.Builder.of(EntityResourcefulCreeper::new, MobCategory.MONSTER)
                                .sized(0.6F, 1.7F)
                                .clientTrackingRange(8)
                                .build(creeperType.getName());
                        EntityAttributeRegistry.register(() -> type, () -> EntityResourcefulCreeper.prepareAttributes(creeperType));
                        if (Platform.getEnv() == EnvType.CLIENT) {
                            EntityRendererRegistry.register(() -> type, ResourcefulCreeperRender::new);
                        }
                        return type;
                    })
            );

            ResourcefulCreepers.LOGGER.info("registering entity spawn egg for {}", creeperType.getDisplayName());

            EGGS.put(creeperType, ITEMS.register(creeperType.getName(), () -> new CreeperSpawnEgg(CREEPERS.get(creeperType), creeperType.getSpawnEggColour1(), creeperType.getSpawnEggColour2(), new Item.Properties()) {
                @Override
                public Component getName(@NotNull ItemStack itemStack) {
                    return Component.translatable("item.resourcefulcreepers.spawn_egg", creeperType.getDisplayName());
                }
            }));

            if (creeperType.allowNaturalSpawns()) {
                ResourcefulCreepers.LOGGER.info("registering spawn for {}", creeperType.getDisplayName());
                ResourcefulCreepers.addSpawn(() -> CREEPERS.get(creeperType).get(), creeperType);
            }

            //Register the entities attributes
//            EntityAttributeRegistry.register(() -> CREEPERS.get(creeperType).get(), () -> EntityResourcefulCreeper.prepareAttributes(creeperType));
            //Register the render
//            if (Platform.getEnv() == EnvType.CLIENT) {
//                EntityRendererRegistry.register(() -> CREEPERS.get(creeperType).get(), ResourcefulCreeperRender::new);
//            }
        }
    }

    static {
        CreativeTabRegistry.appendStack(CreativeModeTabs.SPAWN_EGGS, EGGS.values().stream().map(itemSupplier -> () -> new ItemStack(itemSupplier.get())));
    }
}
