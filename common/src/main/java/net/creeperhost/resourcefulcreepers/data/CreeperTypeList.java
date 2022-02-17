package net.creeperhost.resourcefulcreepers.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CreeperTypeList
{
    public static CreeperTypeList INSTANCE;

    public List<CreeperType> creeperTypes = new ArrayList<>();

    public void add(CreeperType creeperType)
    {
        creeperTypes.add(creeperType);
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
            INSTANCE = gson.fromJson(fileReader, CreeperTypeList.class);
        } catch (Exception ignored) {}
    }

    public static void init(File file)
    {
        try
        {
            if (!file.exists())
            {
                CreeperTypeList.INSTANCE = new CreeperTypeList();
                FileWriter tileWriter = new FileWriter(file);
                tileWriter.write(CreeperTypeList.saveConfig());
                tileWriter.close();
            }
            else
            {
                CreeperTypeList.loadFromFile(file);
            }
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }
    }

    public static void writeToFile(String json)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(Constants.CREEPER_TYPES_CONFIG.toFile()))
        {
            IOUtils.write(json, fileOutputStream, Charset.defaultCharset());
        } catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public static void updateFile()
    {
        writeToFile(saveConfig());
    }
}
