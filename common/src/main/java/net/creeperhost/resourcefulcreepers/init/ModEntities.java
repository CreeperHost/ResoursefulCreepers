package net.creeperhost.resourcefulcreepers.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.utils.Env;
import io.sentry.Sentry;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepersExpectPlatform;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperRender;
import net.creeperhost.resourcefulcreepers.config.Config;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registry.ENTITY_TYPE_REGISTRY);
    public static final DeferredRegister<Item> SPAWN_EGG_ITEMS = DeferredRegister.create(Constants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(new ResourceLocation(Constants.MOD_ID, "creative_tab"), () -> new ItemStack(Items.CREEPER_HEAD));
    public static final HashMap<EntityType<EntityResourcefulCreeper>, Integer> STORED_TYPES = new HashMap<>();
    public static final HashMap<String, String> MOB_NAMES = new HashMap<>();

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
                        MOB_NAMES.put(creeperType.getName(), creeperType.getDisplayName());
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
                    Sentry.captureException(e);
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                Sentry.captureException(e);
                e.printStackTrace();
            }
        }
        if(Platform.getEnvironment() == Env.CLIENT)
        {
            createResourcePack();
        }
        ENTITIES.register();
        SPAWN_EGG_ITEMS.register();
        STORED_TYPES.forEach(ResourcefulCreepersExpectPlatform::registerSpawns);
    }

    public static void createResourcePack()
    {
        System.out.println("Creating resource pack");
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String jsonString = gson.toJson(MOB_NAMES);
        File folders = Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers/assets/resourcefulcreepers/lang").toFile();
        if(!folders.exists()) folders.mkdirs();
        File file = folders.toPath().resolve("en_us.json").toFile();
        createMeta(Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers/pack.mcmeta").toFile());
        if(!file.exists())
        {
            try (FileOutputStream configOut = new FileOutputStream(file))
            {
                IOUtils.write(jsonString, configOut, Charset.defaultCharset());
            } catch (Throwable ignored) {}
        }
        try
        {
            pack(Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers").toString(), Platform.getGameFolder().resolve("resourcepacks/ResourcefulCreepers.zip").toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void createMeta(File file)
    {
        if(!file.exists())
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Meta meta = new Meta();
            String json = gson.toJson(meta);
            try (FileOutputStream configOut = new FileOutputStream(file))
            {
                IOUtils.write(json, configOut, Charset.defaultCharset());
            } catch (Throwable ignored) {}
        }
    }

    public static class Meta
    {
        Pack pack = new Pack();
    }

    public static class Pack
    {
        String description = "resourcefulcreepers";
        int pack_format = 8;
    }

    public static void pack(String sourceDirPath, String zipFilePath) throws IOException
    {
        if(Paths.get(zipFilePath).toFile().exists()) return;
        Path p = Files.createFile(Paths.get(zipFilePath));
        Path pp = Paths.get(sourceDirPath);
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
             Stream<Path> paths = Files.walk(pp)) {
            paths.filter(path -> !Files.isDirectory(path)).forEach(path ->
            {
                ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                try {
                    zs.putNextEntry(zipEntry);
                    Files.copy(path, zs);
                    zs.closeEntry();
                } catch (IOException e) {
                    System.err.println(e);
                }
            });
        }
    }
}
