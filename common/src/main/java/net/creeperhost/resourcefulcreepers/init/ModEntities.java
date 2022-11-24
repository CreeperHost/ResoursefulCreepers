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
import net.fabricmc.api.EnvType;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registry.ENTITY_TYPE_REGISTRY);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.create(new ResourceLocation(Constants.MOD_ID, "creative_tab"), () -> new ItemStack(Items.CREEPER_HEAD));

    @Deprecated
    public static final HashMap<EntityType<EntityResourcefulCreeper>, Integer> STORED_TYPES = new HashMap<>();

    public static final Map<CreeperType, Supplier<EntityType<EntityResourcefulCreeper>>> CREEPERS = Util.make(new LinkedHashMap<>(), map ->
    {
        for(CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
        {
            ResourcefulCreepers.LOGGER.info("registering entity for {}", creeperType.getDisplayName());

            map.put(creeperType, ENTITIES.register(creeperType.getName(), () -> EntityType.Builder.of(EntityResourcefulCreeper::new,
                   MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(creeperType.getName())));

           //Register the entities attributes
            EntityAttributeRegistry.register(() -> map.get(creeperType).get(), () -> EntityResourcefulCreeper.prepareAttributes(creeperType));
            //Register the render
            if(Platform.getEnv() == EnvType.CLIENT)
            {
                EntityRendererRegistry.register(() -> map.get(creeperType).get(), ResourcefulCreeperRender::new);
            }
        }
    });

    public static final HashMap<CreeperType, Supplier<Item>> EGGS = Util.make(new LinkedHashMap<>(), map ->
    {
       for(CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
       {
           ResourcefulCreepers.LOGGER.info("registering entity spawn egg for {}", creeperType.getDisplayName());

           map.put(creeperType, ITEMS.register(creeperType.getName(), () -> new SpawnEggItem(CREEPERS.get(creeperType).get(), creeperType.getSpawnEggColour1(), creeperType.getSpawnEggColour2(), new Item.Properties().tab(CREATIVE_MODE_TAB))
           {
               @Override
               public Component getName(@NotNull ItemStack itemStack)
               {
                   return Component.literal(creeperType.getDisplayName() + " Spawn Egg");
               }
           }));

           if(creeperType.allowNaturalSpawns())
           {
               ResourcefulCreepers.LOGGER.info("registering spawn for {}", creeperType.getDisplayName());
               ResourcefulCreepers.addSpawn(() -> CREEPERS.get(creeperType).get(), creeperType);
           }
       }
    });
}
