package net.creeperhost.resourcefulcreepers;

import dev.architectury.platform.Platform;

import java.nio.file.Path;

public class Constants
{
    public static final String MOD_ID = "resourcefulcreepers";

    //Paths
    public static Path CONFIG_FOLDER = Platform.getConfigFolder().resolve(MOD_ID);
    public static Path CONFIG_FILE = CONFIG_FOLDER.resolve(MOD_ID + ".json");
    public static Path CREEPER_TYPES_CONFIG = CONFIG_FOLDER.resolve(MOD_ID + "_types.json");
    public static Path TEXTURE_PATH = CONFIG_FOLDER.resolve("textures");
}
