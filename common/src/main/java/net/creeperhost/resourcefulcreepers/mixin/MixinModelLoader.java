package net.creeperhost.resourcefulcreepers.mixin;

import net.creeperhost.resourcefulcreepers.Constants;
import net.creeperhost.resourcefulcreepers.ResourcefulCreepers;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class MixinModelLoader
{
    @Shadow protected abstract void loadModel(ResourceLocation arg) throws Exception;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow @Final public static ModelResourceLocation MISSING_MODEL_LOCATION;

    @Shadow @Final private Set<ResourceLocation> loadingStack;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModel(ResourceLocation resourceLocation, CallbackInfoReturnable<UnbakedModel> cir)
    {
        if(!resourceLocation.getNamespace().equalsIgnoreCase(Constants.MOD_ID)) return;
        if(this.unbakedCache.containsKey(resourceLocation)) return;
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation("minecraft:creeper_spawn_egg#inventory");


        UnbakedModel unbakedmodel = this.unbakedCache.get(MISSING_MODEL_LOCATION);
        this.loadingStack.add(modelResourceLocation);

        try
        {
            loadModel(modelResourceLocation);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            this.loadingStack.remove(modelResourceLocation);
        }

        cir.setReturnValue(this.unbakedCache.getOrDefault(modelResourceLocation, unbakedmodel));
    }
}
