package net.creeperhost.resourcefulcreepers.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepersPlatform;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperRender;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.creeperhost.resourcefulcreepers.items.CreeperSpawnEgg;
import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
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
        }
    }

    public static boolean checkMonsterSpawnRules(EntityType<?> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource, CreeperType creeperType) {
        if (levelAccessor.getDifficulty() == Difficulty.PEACEFUL) return false;
        BlockPos belowPos = blockPos.below();
        if (!levelAccessor.getBlockState(belowPos).isValidSpawn(levelAccessor, belowPos, entityType)) return false;
        if (!levelAccessor.getBiome(blockPos).is(BiomeTags.IS_OVERWORLD)) return true;
        return Monster.isDarkEnoughToSpawn(levelAccessor, blockPos, randomSource);
    }

    static {
        CreativeTabRegistry.appendStack(CreativeModeTabs.SPAWN_EGGS, EGGS.values().stream().map(itemSupplier -> () -> new ItemStack(itemSupplier.get())));
    }
}
