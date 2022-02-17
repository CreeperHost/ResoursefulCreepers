package net.creeperhost.resourcefulcreepers.data;

public class ItemDrop
{
    public String name;
    public int amount;

    public ItemDrop(String name, int amount)
    {
        this.name = name;
        this.amount = amount;
    }

    public String getName()
    {
        return name;
    }

    public int getAmount()
    {
        return amount;
    }
}
