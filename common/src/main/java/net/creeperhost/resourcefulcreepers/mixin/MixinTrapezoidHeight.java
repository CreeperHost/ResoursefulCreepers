package net.creeperhost.resourcefulcreepers.mixin;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrapezoidHeight.class)
public interface MixinTrapezoidHeight
{
    @Accessor("minInclusive")
    VerticalAnchor getminInclusive();

    @Accessor("maxInclusive")
    VerticalAnchor getmaxInclusive();
}
