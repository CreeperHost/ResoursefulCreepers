package net.creeperhost.resourcefulcreepers.data;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class CreeperType
{
    private final String name;
    private final String displayName;
    private final int tier;
    private final int spawnEggColour1;
    private final int spawnEggColour2;
    private final boolean dropItemsOnDeath;
    private final int spawnWeight;
    private final boolean allowNaturalSpawns;
    private final double armourValue;
    private final List<ItemDrop> itemDrops;

    public CreeperType(String name, String displayName, int tier, int spawnEggColour1, int spawnEggColour2, boolean dropItemsOnDeath, int spawnWeight, boolean allowNaturalSpawns, double armourValue, List<ItemDrop> itemDrops)
    {
        this.name = name;
        this.displayName = displayName;
        this.tier = tier;
        this.spawnEggColour1 = spawnEggColour1;
        this.spawnEggColour2 = spawnEggColour2;
        this.dropItemsOnDeath = dropItemsOnDeath;
        this.spawnWeight = spawnWeight;
        this.allowNaturalSpawns = allowNaturalSpawns;
        this.armourValue = armourValue;
        this.itemDrops = itemDrops;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public int getSpawnEggColour1()
    {
        return spawnEggColour1;
    }

    public int getSpawnEggColour2()
    {
        return spawnEggColour2;
    }

    public int getTier()
    {
        return tier;
    }

    public boolean shouldDropItemsOnDeath()
    {
        return dropItemsOnDeath;
    }

    public int getSpawnWeight()
    {
        return spawnWeight;
    }

    public boolean allowNaturalSpawns()
    {
        return allowNaturalSpawns;
    }

    public double getArmourValue()
    {
        return armourValue;
    }

    public List<ItemDrop> getItemDrops()
    {
        return itemDrops;
    }

    public ArrayList<ItemStack> getItemDropsAsList()
    {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (ItemDrop itemDrop : getItemDrops())
        {
            Item item = Registry.ITEM.get(new ResourceLocation(itemDrop.getName()));
            if(item != null && item != Items.AIR)
            {
                itemStacks.add(new ItemStack(item, itemDrop.getAmount()));
            }
        }
        return itemStacks;
    }
}
