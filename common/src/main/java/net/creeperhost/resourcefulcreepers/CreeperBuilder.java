package net.creeperhost.resourcefulcreepers;

import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.data.OreGenData;
import net.creeperhost.resourcefulcreepers.mixin.MixinCountPlacement;
import net.creeperhost.resourcefulcreepers.mixin.MixinHeightRangePlacement;
import net.creeperhost.resourcefulcreepers.mixin.MixinTrapezoidHeight;
import net.creeperhost.resourcefulcreepers.mixin.MixinUniformHeight;
import net.creeperhost.resourcefulcreepers.util.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreeperBuilder
{
    public static HashMap<ResourceLocation, OreGenData> ORE_DATA = new HashMap<>();
    public static int MAX_TIER = 0;

    public static int generateFromOreTags()
    {
        generateOreData();
        MAX_TIER = ResourcefulCreepersExpectPlatform.getTierList().size();

        //Clear out the old list
        CreeperTypeList.INSTANCE.creeperTypes.clear();
        List<Block> defaults = ResourcefulCreepersExpectPlatform.getDefaults();
        int amount = 0;
        for (Block aDefault : defaults)
        {
            ResourceLocation name = BuiltInRegistries.BLOCK.getKey(aDefault);
            ResourceLocation empty = new ResourceLocation("empty");
            if(name == empty)
            {
                ResourcefulCreepers.LOGGER.error("Unable to find blocks resource location for " + name + " Skipping");
                continue;
            }
            OreGenData oreGenData = ORE_DATA.get(name);
            if(oreGenData == null)
            {
                ResourcefulCreepers.LOGGER.error("Unable to find oreData for " + name + " Skipping");
                continue;
            }
            int tier = -1;
            if(calculateTier(aDefault.defaultBlockState()) >= 0)
            {
                tier = calculateTier(aDefault.defaultBlockState());
                if(tier > MAX_TIER) tier = MAX_TIER;
            }
            else
            {
                ResourcefulCreepers.LOGGER.error("Unable to find calculate tier for " + name + " Skipping");
                continue;
            }

            CreeperType creeperType = new CreeperType(name.getPath(), aDefault.getName().getString(), tier, ResourcefulCreepers.DEFAULT_COLOUR,
                    ColorHelper.getRandomColour(new ItemStack(aDefault)), true, oreGenData.getWeight(), 1,4, true,
                    aDefault.defaultDestroyTime(), ResourcefulCreepers.createSingleList(name.toString(), 1), ResourcefulCreepers.defaultBiomes());

            CreeperTypeList.INSTANCE.creeperTypes.add(creeperType);
            amount++;
        }
        CreeperTypeList.updateFile();
        return amount;
    }

    public static void generateOreData()
    {
        BuiltInRegistries.FEATURE.forEach(configuredFeature ->
        {
//            configuredFeature.getFeatures().forEach(configuredFeature1 ->
//            {
//                if(configuredFeature1.config() instanceof OreConfiguration oreConfiguration)
//                {
//                    AtomicInteger minWeight = new AtomicInteger();
//                    AtomicInteger minY = new AtomicInteger();
//                    AtomicInteger maxY = new AtomicInteger();
//
//                    configuredFeature.placement().forEach(placementModifier ->
//                    {
//                        if(placementModifier instanceof CountPlacement countPlacement)
//                        {
//                            IntProvider intProvider = ((MixinCountPlacement)countPlacement).getcount();
//                            minWeight.set(intProvider.getMaxValue());
//                        }
//                        if(placementModifier instanceof HeightRangePlacement heightRangePlacement)
//                        {
//                            HeightProvider heightProvider = ((MixinHeightRangePlacement)heightRangePlacement).getheight();
//                            if(heightProvider instanceof UniformHeight uniformHeight)
//                            {
//                                VerticalAnchor verticalAnchor = ((MixinUniformHeight) uniformHeight).getminInclusive();
//                                VerticalAnchor verticalAnchor2 = ((MixinUniformHeight) uniformHeight).getmaxInclusive();
//
//                                //TODO Requires more brain power to track down
////                                int min = ((MixinVerticalAnchor)verticalAnchor).getvalue();
////                                int max = ((MixinVerticalAnchor)verticalAnchor2).getvalue();
//                                minY.set(0);
//                                maxY.set(0);
//                            }
//                            if(heightProvider instanceof TrapezoidHeight trapezoidHeight)
//                            {
//                                VerticalAnchor verticalAnchor = ((MixinTrapezoidHeight) trapezoidHeight).getminInclusive();
//                                VerticalAnchor verticalAnchor2 = ((MixinTrapezoidHeight) trapezoidHeight).getmaxInclusive();
//
//                                //TODO Requires more brain power to track down
////                                int min = ((MixinVerticalAnchor)verticalAnchor).getvalue();
////                                int max = ((MixinVerticalAnchor)verticalAnchor2).getvalue();
//                                minY.set(0);
//                                maxY.set(0);
//                            }
//                        }
//                    });
//                    oreConfiguration.targetStates.forEach(targetBlockState ->
//                    {
//                        ResourceLocation name = Registry.BLOCK.getKey(targetBlockState.state.getBlock());
//                        ORE_DATA.put(name, new OreGenData(oreConfiguration.size, minWeight.get(), minY.get(), maxY.get()));
//                    });
//                }
//            });
        });
    }

    public static int calculateTier(BlockState blockState)
    {
        List<Tier> tierList = ResourcefulCreepersExpectPlatform.getTierList();
        for (Tier tier : tierList)
        {
            if(ResourcefulCreepersExpectPlatform.isCorrectTierForDrops(tier, blockState))
            {
                return tier.getLevel();
            }
        }
        return 0;
    }
}
