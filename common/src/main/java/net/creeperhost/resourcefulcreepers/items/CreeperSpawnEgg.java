package net.creeperhost.resourcefulcreepers.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 24/03/2024
 */
public class CreeperSpawnEgg extends SpawnEggItem {
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public CreeperSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(null, backgroundColor, highlightColor, props);
        this.typeSupplier = type;
    }

    @Override
    public EntityType<?> getType(ItemStack itemStack) {
        EntityType<?> type = super.getType(itemStack);
        return type != null ? type : typeSupplier.get();
    }

    protected EntityType<?> getDefaultType() {
        return this.typeSupplier.get();
    }

    public int getColor(ItemStack itemStack, int i) {
        return getColor(i);
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return getDefaultType().requiredFeatures();
    }
}
