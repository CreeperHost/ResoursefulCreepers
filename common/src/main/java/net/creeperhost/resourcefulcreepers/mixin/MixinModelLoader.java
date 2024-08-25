package net.creeperhost.resourcefulcreepers.mixin;

import net.creeperhost.resourcefulcreepers.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Mixin (ModelBakery.class)
public abstract class MixinModelLoader {

    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation arg) throws IOException;

    @Shadow @Final private UnbakedModel missingModel;
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow @Final private Set<ResourceLocation> loadingStack;

    @Inject (method = "getModel", at = @At ("HEAD"), cancellable = true)
    public void getModel(ResourceLocation location, CallbackInfoReturnable<UnbakedModel> cir) {
        if (!location.getNamespace().equalsIgnoreCase(Constants.MOD_ID)) return;
        if (this.unbakedCache.containsKey(location)) return;

        this.loadingStack.add(location);
        ResourceLocation resourcelocation = this.loadingStack.iterator().next();
        try {
            if (!this.unbakedCache.containsKey(resourcelocation)) {
                UnbakedModel unbakedmodel = this.loadBlockModel(ResourceLocation.fromNamespaceAndPath("resourcefulcreepers", "item/spawn_egg"));
                this.unbakedCache.put(resourcelocation, unbakedmodel);
            }
        } catch (Exception var7) {
            this.unbakedCache.put(resourcelocation, this.missingModel);
        } finally {
            this.loadingStack.remove(resourcelocation);
        }

        cir.setReturnValue(this.unbakedCache.getOrDefault(location, this.missingModel));
    }
}
