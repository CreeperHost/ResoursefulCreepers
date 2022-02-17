package net.creeperhost.resourcefulcreepers.client;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ResourcefulCreeperClient
{
    public static HashMap<String, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    public static void addTexture(String resourceName, ResourceLocation resourceLocation)
    {
        if(!TEXTURE_MAP.containsKey(resourceName))
        {
            TEXTURE_MAP.put(resourceName, resourceLocation);
        }
    }

    public static ResourceLocation getTexture(String resourceName)
    {
        if(TEXTURE_MAP.containsKey(resourceName))
        {
            return TEXTURE_MAP.get(resourceName);
        }
        return null;
    }
}
