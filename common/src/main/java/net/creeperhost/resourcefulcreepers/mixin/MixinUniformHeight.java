package net.creeperhost.resourcefulcreepers.mixin;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformHeight.class)
public interface MixinUniformHeight
{
    @Accessor("minInclusive")
    VerticalAnchor getminInclusive();

    @Accessor("maxInclusive")
    VerticalAnchor getmaxInclusive();
}
