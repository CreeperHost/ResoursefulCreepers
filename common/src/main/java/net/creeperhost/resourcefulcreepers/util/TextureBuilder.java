package net.creeperhost.resourcefulcreepers.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperClient;
import net.creeperhost.resourcefulcreepers.client.ResourcefulCreeperRender;
import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;

import java.io.FileInputStream;
import java.nio.file.Path;

public class TextureBuilder
{
    public static ResourceLocation createCreeperTexture(CreeperType creeperType)
    {
        String resourceName = creeperType.getName();
        Path texture = Constants.TEXTURE_PATH.resolve(creeperType.getName() + ".png");
        ResourceLocation newLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "/textures/entities/" + creeperType.getName());
        if (!texture.toFile().exists() && !creeperType.getItemDropsAsList().isEmpty())
        {
            ItemStack itemStack = creeperType.getItemDropsAsList().get(0);
            try
            {
                Resource resource = Minecraft.getInstance().getResourceManager().getResource(ResourcefulCreeperRender.CREEPER_LOCATION).get();
                NativeImage nativeImage = NativeImage.read(resource.open());

                int lastColour = 0;
                int lastRandomColour = 0;

                for (int i = 0; i < nativeImage.getHeight(); i++)
                {
                    for (int j = 0; j < nativeImage.getWidth(); j++)
                    {
                        try
                        {
                            int colour = nativeImage.getPixelRGBA(j, i);
                            if(lastColour == 0) lastColour = colour;
                            if(lastRandomColour == 0) lastRandomColour = ColorHelper.getRandomColour(itemStack);

                            if(lastColour != colour)
                            {
                                lastRandomColour = ColorHelper.getRandomColour(itemStack);
                            }
                            if(!isFacePixel(j, i) && colour != 0)
                            {
                                nativeImage.setPixelRGBA(j, i, lastRandomColour);
                            }
                            if(needsBlending(j, i))
                            {
                                int black = -1308622848;
                                nativeImage.blendPixel(j, i, black);
                            }
                        } catch (IllegalArgumentException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                if(!Constants.TEXTURE_PATH.toFile().exists())
                {
                    ResourcefulCreepers.LOGGER.info("Crating texture cache folder at " + Constants.TEXTURE_PATH);
                    Constants.TEXTURE_PATH.toFile().mkdirs();
                }
                if(!texture.toFile().exists())
                {
                    ResourcefulCreepers.LOGGER.info("Writing texture file for " + creeperType.getName() + " to " + texture);
                    nativeImage.writeToFile(texture);
                }
                nativeImage.close();
            } catch (Exception e)
            {
                e.printStackTrace();
                return ResourcefulCreeperRender.CREEPER_LOCATION;
            }
        }
        if(texture.toFile().exists())
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream(texture.toFile());
                NativeImage nativeImage = NativeImage.read(fileInputStream);
                Minecraft.getInstance().getTextureManager().register(newLocation, new DynamicTexture(nativeImage));
                ResourcefulCreeperClient.addTexture(resourceName, newLocation);
                fileInputStream.close();
                return newLocation;
            } catch (Exception e)
            {
                e.printStackTrace();
                return ResourcefulCreeperRender.CREEPER_LOCATION;
            }
        }
        return ResourcefulCreeperRender.CREEPER_LOCATION;
    }

    public static boolean isFacePixel(int x, int y)
    {
        if(x == 10 && y == 11) return true;
        if(x == 13 && y == 11) return true;
        if(x == 11 && y == 13) return true;
        if(x == 12 && y == 13) return true;
        if(x == 10 && y == 14) return true;
        if(x == 11 && y == 14) return true;
        if(x == 12 && y == 14) return true;
        if(x == 13 && y == 14) return true;
        return false;
    }

    public static boolean needsBlending(int x, int y)
    {
        if(x == 9 && y == 10) return true;
        if(x == 10 && y == 10) return true;
        if(x == 9 && y == 11) return true;

        if(x == 13 && y == 10) return true;
        if(x == 14 && y == 10) return true;
        if(x == 14 && y == 11) return true;

        if(x == 11 && y == 12) return true;
        if(x == 12 && y == 12) return true;

        if(x == 10 && y == 13) return true;
        if(x == 13 && y == 13) return true;

        return false;
    }
}
