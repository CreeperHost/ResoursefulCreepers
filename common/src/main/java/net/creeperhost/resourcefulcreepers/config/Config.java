package net.creeperhost.resourcefulcreepers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;

public class Config
{
    public static Config INSTANCE;

    public boolean generateDefaultTypes;
    public boolean autoGenerateCreeperTypesFromOreTags;
    public float explosionMultiplier;
    public float poweredExplosionMultiplier;
    public boolean explosionsGenerateOres;
    public boolean overrideOreDrops;
    public boolean creepersAttractedToArmourStand;

    public Config()
    {
        generateDefaultTypes = true;
        autoGenerateCreeperTypesFromOreTags = false;
        explosionMultiplier = 0.25F;
        poweredExplosionMultiplier = 0.5F;
        explosionsGenerateOres = true;
        overrideOreDrops = false;
        creepersAttractedToArmourStand = true;
    }

    public static String saveConfig()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(INSTANCE);
    }

    public static void loadFromFile(File file)
    {
        Gson gson = new Gson();
        try
        {
            FileReader fileReader = new FileReader(file);
            INSTANCE = gson.fromJson(fileReader, Config.class);
        } catch (Exception ignored) {}
    }

    public static void saveConfigToFile(File file)
    {
        try (FileOutputStream configOut = new FileOutputStream(file))
        {
            IOUtils.write(Config.saveConfig(), configOut, Charset.defaultCharset());
        }
        catch (Throwable ignored) {}
    }

    public static void init(File file)
    {
        try
        {
            if (!file.exists())
            {
                Config.INSTANCE = new Config();
                FileWriter tileWriter = new FileWriter(file);
                tileWriter.write(Config.saveConfig());
                tileWriter.close();
            }
            else
            {
                Config.loadFromFile(file);
            }
        } catch (Exception ignored) {}
    }
}
