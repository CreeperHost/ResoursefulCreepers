package net.creeperhost.resourcefulcreepers.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorHelper
{
    public static List<Integer> getColour(ItemStack itemStack)
    {
        if(itemStack.isEmpty()) return null;
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, Minecraft.getInstance().level, Minecraft.getInstance().player, 16);
        TextureAtlasSprite textureAtlasSprite = bakedModel.getParticleIcon();
        ResourceLocation particleLocation = new ResourceLocation(textureAtlasSprite.getName().getNamespace(), "textures/" + textureAtlasSprite.getName().getPath() + ".png");
        try
        {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(particleLocation).get();
            NativeImage nativeImage = NativeImage.read(resource.open());
            List<Integer> colourList = new ArrayList<>();
            for (int i = 0; i < nativeImage.getHeight(); i++)
            {
                for (int j = 0; j < nativeImage.getWidth(); j++)
                {
                    int colour = nativeImage.getPixelRGBA(j, i);

                    if(!colourList.contains(colour))
                    {
                        colourList.add(colour);
                    }
                }
            }
            return colourList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRandomColour(ItemStack stack)
    {
        List<Integer> colours = getColour(stack);
        if(colours == null || colours.isEmpty()) return -1;

        int random = new Random().nextInt(colours.size());

        return colours.get(random);
    }

    public static ResourceLocation getItemTexture(ItemStack itemStack)
    {
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 16);
        TextureAtlasSprite textureAtlasSprite = bakedModel.getParticleIcon();
        return new ResourceLocation(textureAtlasSprite.getName().getNamespace(), "textures/" + textureAtlasSprite.getName().getPath() + ".png");
    }
}
