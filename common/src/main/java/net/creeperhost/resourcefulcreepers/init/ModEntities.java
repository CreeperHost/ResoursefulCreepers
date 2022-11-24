package net.creeperhost.resourcefulcreepers.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.biome.BiomeModifications;
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
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registry.ENTITY_TYPE_REGISTRY);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.create(new ResourceLocation(Constants.MOD_ID, "creative_tab"), () -> new ItemStack(Items.CREEPER_HEAD));

    @Deprecated
    public static final HashMap<EntityType<EntityResourcefulCreeper>, Integer> STORED_TYPES = new HashMap<>();

    @Deprecated
    public static final HashMap<String, String> MOB_NAMES = new HashMap<>();

    public static final Map<CreeperType, Supplier<EntityType<EntityResourcefulCreeper>>> CREEPERS = Util.make(new LinkedHashMap<>(), map ->
    {
        for(CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
        {
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
           map.put(creeperType, ITEMS.register(creeperType.getName(), () -> new SpawnEggItem(CREEPERS.get(creeperType).get(), creeperType.getSpawnEggColour1(), creeperType.getSpawnEggColour2(), new Item.Properties().tab(CREATIVE_MODE_TAB))
           {
               @Override
               public Component getName(@NotNull ItemStack itemStack)
               {
                   return Component.literal(creeperType.getDisplayName() + " Spawn Egg");
               }
           }));

           if(creeperType.allowNaturalSpawns()) ResourcefulCreepers.addSpawn(() -> CREEPERS.get(creeperType).get());
       }
    });

    @Deprecated
    public static void createResourcePack()
    {
        try
        {
            System.out.println("Creating resource pack");
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            String jsonString = gson.toJson(MOB_NAMES);
            File folders = Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers/assets/minecraft/lang").toFile();
            if (!folders.exists()) folders.mkdirs();
            File file = folders.toPath().resolve("en_us.json").toFile();
            createMeta(Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers/pack.mcmeta").toFile());
            if (!file.exists())
            {
                try (FileOutputStream configOut = new FileOutputStream(file))
                {
                    IOUtils.write(jsonString, configOut, Charset.defaultCharset());
                } catch (Throwable ignored)
                {
                }
            }
            try
            {
                pack(Constants.CONFIG_FOLDER.resolve("ResourcefulCreepers").toString(), Platform.getGameFolder().resolve("resourcepacks/ResourcefulCreepers.zip").toString());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Deprecated
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

    @Deprecated
    public static class Meta
    {
        Pack pack = new Pack();
    }

    @Deprecated
    public static class Pack
    {
        String description = "resourcefulcreepers";
        int pack_format = 8;
    }

    @Deprecated
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
                    e.printStackTrace();
                }
            });
        }
    }
}
