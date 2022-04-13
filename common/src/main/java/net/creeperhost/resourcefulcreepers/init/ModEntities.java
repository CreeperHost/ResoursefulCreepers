package net.creeperhost.resourcefulcreepers.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.utils.Env;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepersExpectPlatform;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperRender;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registry.ENTITY_TYPE_REGISTRY);
    public static final DeferredRegister<Item> SPAWN_EGG_ITEMS = DeferredRegister.create(Constants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(new ResourceLocation(Constants.MOD_ID, "creative_tab"), () -> new ItemStack(Items.CREEPER_HEAD));
    public static final HashMap<EntityType<EntityResourcefulCreeper>, Integer> STORED_TYPES = new HashMap<>();

    public static void init()
    {
        loadCreeperTypes();
    }

    public static void loadCreeperTypes()
    {
        if(CreeperTypeList.INSTANCE.creeperTypes.isEmpty()) return;

        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
        {
            try
            {
                try
                {
                    EntityType<EntityResourcefulCreeper> entityType = EntityType.Builder.of(EntityResourcefulCreeper::new,
                            MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(creeperType.getName());
                    ENTITIES.register(creeperType.getName(), () -> entityType);


                    STORED_TYPES.put(entityType, creeperType.getSpawnWeight());
                    EntityAttributeRegistry.register(() -> entityType, () -> EntityResourcefulCreeper.prepareAttributes(creeperType));

                    if (Platform.getEnvironment() == Env.CLIENT)
                    {
                        EntityRendererRegistry.register(() -> entityType, ResourcefulCreeperRender::new);
                    }

                    SpawnEggItem spawnEggItem = new SpawnEggItem(entityType, creeperType.getSpawnEggColour1(), creeperType.getSpawnEggColour2(), new Item.Properties().tab(CREATIVE_TAB))
                    {
                        @Override
                        public Component getName(ItemStack itemStack)
                        {
                            return new TranslatableComponent(creeperType.getDisplayName() + " Spawn Egg");
                        }

                        @Override
                        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag)
                        {
                            list.add(new TranslatableComponent("Tier: " + creeperType.getTier()));
                            list.add(new TranslatableComponent("Block: " + creeperType.getItemDrops().get(0).getName()));
                            super.appendHoverText(itemStack, level, list, tooltipFlag);
                        }
                    };
                    SPAWN_EGG_ITEMS.register(creeperType.getName(), () -> spawnEggItem);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        ENTITIES.register();
        SPAWN_EGG_ITEMS.register();
        STORED_TYPES.forEach(ResourcefulCreepersExpectPlatform::registerSpawns);
    }
}
