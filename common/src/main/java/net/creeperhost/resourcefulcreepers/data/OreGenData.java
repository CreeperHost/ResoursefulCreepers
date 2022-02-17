package net.creeperhost.resourcefulcreepers.data;

public class OreGenData
{
    public int maxVeinSize;
    public int weight;
    public int minY;
    public int maxY;

    public OreGenData(int maxVeinSize, int weight, int minY, int maxY)
    {
        this.maxVeinSize = maxVeinSize;
        this.weight = weight;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int getMaxVeinSize()
    {
        return maxVeinSize;
    }

    public int getWeight()
    {
        return weight;
    }

    public int getMinY()
    {
        return minY;
    }

    public int getMaxY()
    {
        return maxY;
    }
}
