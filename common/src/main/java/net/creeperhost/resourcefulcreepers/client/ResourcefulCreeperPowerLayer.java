package net.creeperhost.resourcefulcreepers.client;

import net.creeperhost.resourcefulcreepers.entites.EntityResourcefulCreeper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class ResourcefulCreeperPowerLayer extends EnergySwirlLayer<EntityResourcefulCreeper, ResourcefulCreeperModel<EntityResourcefulCreeper>>
{
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final ResourcefulCreeperModel<EntityResourcefulCreeper> model;

    public ResourcefulCreeperPowerLayer(RenderLayerParent<EntityResourcefulCreeper, ResourcefulCreeperModel<EntityResourcefulCreeper>> renderLayerParent, EntityModelSet modelSet)
    {
        super(renderLayerParent);
        this.model = new ResourcefulCreeperModel<>(modelSet.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    @Override
    protected float xOffset(float f)
    {
        return f * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation()
    {
        return POWER_LOCATION;
    }

    @Override
    protected EntityModel<EntityResourcefulCreeper> model()
    {
        return model;
    }
}
