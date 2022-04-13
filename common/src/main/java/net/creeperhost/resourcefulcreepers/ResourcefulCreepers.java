package net.creeperhost.resourcefulcreepers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.sentry.Sentry;
import net.creeperhost.resourcefulcreepers.config.Config;
import net.creeperhost.resourcefulcreepers.util.TextureBuilder;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.data.ItemDrop;
import net.creeperhost.resourcefulcreepers.init.ModEntities;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourcefulCreepers
{
    public static Logger LOGGER = LogManager.getLogger();
    public static int DEFAULT_COLOUR = 894731;
    private static final ExecutorService TEXTURE_CREATION_EXECUTOR = Executors.newFixedThreadPool(5, new ThreadFactoryBuilder().setNameFormat("resourcefulcreepers-texture_creation-%d").build());
    public static final ExecutorService REGISTER_THREAD_BECAUSE_FORGE_IS_DUMB = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("resourcefulcreepers-mob_creation-%d").build());

    public static void init()
    {
        Sentry.init(options ->
        {
            options.setDsn("https://dcbda43f2f2a4ef38f702798205092dd@sentry.creeperhost.net/5");

            options.setTracesSampleRate(Platform.isDevelopmentEnvironment() ? 1.0 : 0.025);
            options.setEnvironment(SharedConstants.getCurrentVersion().getName());
            options.setRelease(Constants.MOD_VERSION);
            options.setTag("commit", BuildInfo.version);
            options.setTag("modloader", Minecraft.getInstance().getLaunchedVersion());
            options.setTag("ram", String.valueOf(((Runtime.getRuntime().maxMemory() / 1024) /1024)));
            options.setDist(System.getProperty("os.arch"));
            options.setServerName(Platform.getEnv() == EnvType.CLIENT ? "integrated" : "dedicated");
            options.setDebug(Platform.isDevelopmentEnvironment());
        });

        try
        {

            if (!Constants.CONFIG_FOLDER.toFile().exists())
            {
                LOGGER.info("Creating config folder at " + Constants.CONFIG_FOLDER);
                Constants.CONFIG_FOLDER.toFile().mkdirs();
            }
            Config.init(Constants.CONFIG_FILE.toFile());
            if (!Config.INSTANCE.generateDefaultTypes && !Constants.CREEPER_TYPES_CONFIG.toFile().exists())
            {
                LOGGER.info("creeper_types.json does not exist, Creating new file using the ores tag");
                Config.INSTANCE.autoGenerateCreeperTypesFromOreTags = true;
                Config.saveConfigToFile(Constants.CONFIG_FILE.toFile());
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
            if(Platform.isFabric()) ModEntities.init();
            if (Platform.getEnvironment() == Env.CLIENT)
            {
                ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world ->
                {
                    if (Config.INSTANCE.autoGenerateCreeperTypesFromOreTags)
                    {
                        int amount = CreeperBuilder.generateFromOreTags();
                        Config.INSTANCE.autoGenerateCreeperTypesFromOreTags = false;
                        Config.saveConfigToFile(Constants.CONFIG_FILE.toFile());
                        LOGGER.info("Finished creating new CreeperTypes, " + amount + " types have been created, A restart is needed for these changes to take effect");
                    }

                    if (CreeperTypeList.INSTANCE.creeperTypes != null && !CreeperTypeList.INSTANCE.creeperTypes.isEmpty())
                    {
                        for (CreeperType creeperType : CreeperTypeList.INSTANCE.creeperTypes)
                        {
                            try
                            {
                                System.out.println("Running texture builder for " + creeperType.getName());
                                CompletableFuture.runAsync(() -> TextureBuilder.createCreeperTexture(creeperType), TEXTURE_CREATION_EXECUTOR);
                            } catch (Exception e)
                            {
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
        if(Config.INSTANCE.generateDefaultTypes)
        {
            CreeperTypeList.INSTANCE.creeperTypes.clear();

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("copper_ore", "Copper Creeper", 1, DEFAULT_COLOUR, -1, true, 10, true, 0, createSingleList("minecraft:copper_ore", 2)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("iron_ore", "Iron Creeper", 1, DEFAULT_COLOUR, -1, true, 10, true, 0, createSingleList("minecraft:iron_ore", 2)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("gold_ore", "Gold Creeper", 1,  DEFAULT_COLOUR, -1, true, 10, true, 0, createSingleList("minecraft:gold_ore", 2)));

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("coal_ore", "Coal Creeper", 1, DEFAULT_COLOUR, -1,true, 10, true, 0, createSingleList("minecraft:coal_ore", 3)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("redstone_ore", "Redstone Creeper",2, DEFAULT_COLOUR, -1,true, 10, true, 0, createSingleList("minecraft:redstone_ore", 3)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("lapis_ore", "Lapis Creeper", 1, DEFAULT_COLOUR, -1, true, 10, true, 0, createSingleList("minecraft:lapis_ore", 3)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("quartz_ore_creeper", "Quartz Creeper", 2, DEFAULT_COLOUR, -1, true, 10, true, 0, createSingleList("minecraft:nether_quartz_ore", 3)));

            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("diamond_ore", "Diamond Creeper", 2, DEFAULT_COLOUR, -1,true, 20, false, 0, createSingleList("minecraft:diamond_ore", 1)));
            CreeperTypeList.INSTANCE.creeperTypes.add(new CreeperType("emerald_ore", "Emerald Creeper", 2, DEFAULT_COLOUR, -1, true, 20, false, 0, createSingleList("minecraft:emerald_ore", 1)));

            Config.INSTANCE.generateDefaultTypes = false;
            Config.saveConfigToFile(Constants.CONFIG_FILE.toFile());
            CreeperTypeList.updateFile();
        }
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
