package net.creeperhost.resourcefulcreepers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.utils.Env;
import io.sentry.Sentry;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.resourcefulcreepers.config.ConfigDataRC;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.data.ItemDrop;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.creeperhost.resourcefulcreepers.util.TextureBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ResourcefulCreepers
{
    public static Logger LOGGER = LogManager.getLogger();
    public static ConfigBuilder configBuilder;
    public static ConfigDataRC configData;
    public static int DEFAULT_COLOUR = 894731;
    private static final ExecutorService TEXTURE_CREATION_EXECUTOR = Executors.newFixedThreadPool(5, new ThreadFactoryBuilder().setNameFormat("resourcefulcreepers-texture_creation-%d").build());
    public static final ExecutorService REGISTER_THREAD_BECAUSE_FORGE_IS_DUMB = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("resourcefulcreepers-mob_creation-%d").build());

    public static void init()
    {
        if (!Constants.CONFIG_FOLDER.toFile().exists())
        {
            LOGGER.info("Creating config folder at " + Constants.CONFIG_FOLDER);
            Constants.CONFIG_FOLDER.toFile().mkdirs();
        }
        configBuilder = new ConfigBuilder(Constants.CONFIG_FILE.getFileName().toString(), Constants.CONFIG_FILE, ConfigDataRC.class);
        configData = (ConfigDataRC) configBuilder.getConfigData();
        if(!configData.disableSentry)
        {
            Sentry.init(options ->
            {
                options.setDsn("https://9bf679b2950ef2c84b8b21794211ad46@sentry.crprh.st/7");

                options.setTracesSampleRate(Platform.isDevelopmentEnvironment() ? 1.0 : 0.025);
                options.setEnvironment(SharedConstants.getCurrentVersion().getName());
                options.setRelease(Constants.MOD_VERSION);
                options.setTag("commit", BuildInfo.version);
                options.setTag("modloader", Platform.isForgeLike() ? "Forge" : "Fabric");
                options.setTag("ram", String.valueOf(((Runtime.getRuntime().maxMemory() / 1024) / 1024)));
                options.setDist(System.getProperty("os.arch"));
                options.setServerName(Platform.getEnv() == EnvType.CLIENT ? "integrated" : "dedicated");
                options.setDebug(Platform.isDevelopmentEnvironment());
                options.addInAppInclude("net.creeperhost.resourcefulcreepers");
            });
        }

        try
        {
            //TODO Figure out creeper generation based on ores
            if (!configData.generateDefaultTypes && !Constants.CREEPER_TYPES_CONFIG.toFile().exists())
            {
                LOGGER.info("creeper_types.json does not exist, Creating new file using the ores tag");
                configData.autoGenerateCreeperTypesFromOreTags = true;
                ResourcefulCreepers.configBuilder.save(ResourcefulCreepers.configData);
            }
            CreeperTypeList.init(Constants.CREEPER_TYPES_CONFIG.toFile());
            List<String> names = new ArrayList<>();
            List<CreeperType> dupes = new ArrayList<>();
            if (CreeperTypeList.INSTANCE.creeperTypes != null && !CreeperTypeList.INSTANCE.creeperTypes.isEmpty())
            {
                for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
                {
                    if (!names.contains(creeperType.getName()))
                    {
                        names.add(creeperType.getName());
                    }
                    else
                    {
                        dupes.add(creeperType);
                    }
                }

                if (!dupes.isEmpty())
                {
                    List<CreeperType> copy = CreeperTypeList.INSTANCE.creeperTypes;
                    for (CreeperType dupe : dupes)
                    {
                        LOGGER.error("Found duplicate entry for " + dupe.getName() + " removing");
                        copy.remove(dupe);
                    }
                    CreeperTypeList.INSTANCE.creeperTypes = copy;
                    CreeperTypeList.updateFile();
                }
            }
            generateDefaultTypes();
            ModEntities.ENTITIES.register();
            ModEntities.ITEMS.register();

            if (Platform.getEnvironment() == Env.CLIENT)
            {
                ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world ->
                {
                    //TODO Figure out creeper generation based on ores
//                    if (configData.autoGenerateCreeperTypesFromOreTags)
//                    {
//                        int amount = CreeperBuilder.generateFromOreTags();
//                        configData.autoGenerateCreeperTypesFromOreTags = false;
//                        configBuilder.save();
//                        LOGGER.info("Finished creating new CreeperTypes, " + amount + " types have been created, A restart is needed for these changes to take effect");
//                    }


                    if (CreeperTypeList.INSTANCE.creeperTypes != null && !CreeperTypeList.INSTANCE.creeperTypes.isEmpty())
                    {
                        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
                        {
                            try
                            {
                                LOGGER.info("Running texture builder for {}", creeperType.getName());
                                CompletableFuture.runAsync(() -> TextureBuilder.createCreeperTexture(creeperType), TEXTURE_CREATION_EXECUTOR);
                            } catch (Exception e)
                            {
                                Sentry.captureException(e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        } catch (Exception e)
        {
            Sentry.captureException(e);
        }
    }

    public static void generateDefaultTypes()
    {
        if(configData.generateDefaultTypes)
        {
            CreeperTypeList.INSTANCE.creeperTypes.clear();

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("copper_ore", "Copper Creeper", 1, DEFAULT_COLOUR, -1, true, 10, 1, 4, true, 0, createSingleList("minecraft:copper_ore", 2), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("iron_ore", "Iron Creeper", 1, DEFAULT_COLOUR, -1, true, 10, 1, 4, true, 0, createSingleList("minecraft:iron_ore", 2), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("gold_ore", "Gold Creeper", 1,  DEFAULT_COLOUR, -1, true, 10, 1, 4, true, 0, createSingleList("minecraft:gold_ore", 2), defaultBiomes()));

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("coal_ore", "Coal Creeper", 1, DEFAULT_COLOUR, -1,true, 10, 1, 4,true, 0, createSingleList("minecraft:coal_ore", 3), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("redstone_ore", "Redstone Creeper",2, DEFAULT_COLOUR, -1,true, 10, 1, 4, true, 0, createSingleList("minecraft:redstone_ore", 3), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("lapis_ore", "Lapis Creeper", 1, DEFAULT_COLOUR, -1, true, 10, 1, 4,true, 0, createSingleList("minecraft:lapis_ore", 3), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("quartz_ore_creeper", "Quartz Creeper", 2, DEFAULT_COLOUR, -1, true, 10, 1, 4, true, 0, createSingleList("minecraft:nether_quartz_ore", 3),netherBiomes()));

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("diamond_ore", "Diamond Creeper", 2, DEFAULT_COLOUR, -1,true, 5, 1, 4, false, 0, createSingleList("minecraft:diamond_ore", 1), defaultBiomes()));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("emerald_ore", "Emerald Creeper", 2, DEFAULT_COLOUR, -1, true, 5, 1, 4, false, 0, createSingleList("minecraft:emerald_ore", 1), defaultBiomes()));

            configData.generateDefaultTypes = false;
            configBuilder.save(configData);
            CreeperTypeList.updateFile();
        }
//        generateDataJsons();
    }

    //For In Dev use only
    private static void generateDataJsons() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File dataDir = new File("[Workspace Location]/ResourcefulCreepers/neoforge/src/main/resources/data/resourcefulcreepers/neoforge/biome_modifier");
        if (!dataDir.exists()) throw new IllegalStateException();

        CreeperTypeList.INSTANCE.creeperTypes.forEach(type -> {
            JsonObject json = new JsonObject();
            if (type.getBiomesTags().size() != 1) throw new IllegalStateException();
            json.addProperty("type", "neoforge:add_spawns");
            json.addProperty("biomes", "#" + type.getBiomesTags().get(0));
            JsonObject spawners = new JsonObject();
            spawners.addProperty("type", Constants.MOD_ID + ":" + type.getName());
            spawners.addProperty("maxCount", type.getMaxGroup());
            spawners.addProperty("minCount", type.getMinGroup());
            spawners.addProperty("weight", type.getSpawnWeight());
            json.add("spawners", spawners);

            try (FileWriter writer = new FileWriter(new File(dataDir, type.getName() + "_spawn.json"))){
                gson.toJson(json, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static List<String> defaultBiomes()
    {
        List<String> list = new ArrayList<>();
        list.add(BiomeTags.IS_OVERWORLD.location().toString());
        return list;
    }

    public static List<String> netherBiomes()
    {
        List<String> list = new ArrayList<>();
        list.add(BiomeTags.IS_NETHER.location().toString());
        return list;
    }

    public static List<ItemDrop> createSingleList(String name, int amount)
    {
        List<ItemDrop> itemDrops = new ArrayList<>();
        itemDrops.add(new ItemDrop(name, amount));
        return itemDrops;
    }

    public static CreeperType getTypeFromName(String name)
    {
        if(CreeperTypeList.INSTANCE.creeperTypes == null) return null;
        if(CreeperTypeList.INSTANCE.creeperTypes.isEmpty()) return null;

        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
        {
            if(creeperType.getName().equalsIgnoreCase(name))
            {
                return creeperType;
            }
        }
        return null;
    }
}
