package net.creeperhost.resourcefulcreepers;

import net.creeperhost.resourcefulcreepers.data.CreeperType;
import net.creeperhost.resourcefulcreepers.data.CreeperTypeList;
import net.creeperhost.resourcefulcreepers.data.OreGenData;
import net.creeperhost.resourcefulcreepers.mixin.MixinCountPlacement;
import net.creeperhost.resourcefulcreepers.mixin.MixinHeightRangePlacement;
import net.creeperhost.resourcefulcreepers.mixin.MixinTrapezoidHeight;
import net.creeperhost.resourcefulcreepers.mixin.MixinUniformHeight;
import net.creeperhost.resourcefulcreepers.util.ColorHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CreeperBuilder {
    public static HashMap<ResourceLocation, OreGenData> ORE_DATA = new HashMap<>();
    public static int MAX_TIER = 5;

    public static void onServerStarted(MinecraftServer server) {
        if (ResourcefulCreepers.configData.autoGenerateCreeperTypesFromOreTags) {
            int amount = CreeperBuilder.generateFromOreTags(server.getLevel(Level.OVERWORLD));
            ResourcefulCreepers.configData.autoGenerateCreeperTypesFromOreTags = false;
            ResourcefulCreepers.configBuilder.save(ResourcefulCreepers.configData);
            ResourcefulCreepers.LOGGER.info("Finished creating new CreeperTypes, " + amount + " types have been created, A restart is needed for these changes to take effect");
        }
    }

    private static int generateFromOreTags(ServerLevel level) {
        generateOreData(level);

        //            TODO Why does this always allow natural spawns? That does not seem correct...

        //Clear out the old list
        CreeperTypeList.INSTANCE.creeperTypes.clear();
        List<Block> defaults = ResourcefulCreepersPlatform.getDefaults();
        int amount = 0;
        for (Block aDefault : defaults) {
            ResourceLocation name = BuiltInRegistries.BLOCK.getKey(aDefault);
            ResourceLocation empty = ResourceLocation.withDefaultNamespace("empty");
            if (name == empty) {
                ResourcefulCreepers.LOGGER.error("Unable to find blocks resource location for " + name + " Skipping");
                continue;
            }
            OreGenData oreGenData = ORE_DATA.get(name);
            if (oreGenData == null) {
                ResourcefulCreepers.LOGGER.error("Unable to find oreData for " + name + " Skipping");
                continue;
            }
            int tier = -1;
            if (calculateTier(aDefault.defaultBlockState()) >= 0) {
                tier = calculateTier(aDefault.defaultBlockState());
                if (tier > MAX_TIER) tier = MAX_TIER;
            } else {
                ResourcefulCreepers.LOGGER.error("Unable to find calculate tier for " + name + " Skipping");
                continue;
            }

            CreeperType creeperType = new CreeperType(name.getPath(), aDefault.getName().getString(), tier, ResourcefulCreepers.DEFAULT_COLOUR,
                    ColorHelper.getRandomColour(new ItemStack(aDefault)), true, oreGenData.getWeight(), 1, 4, true,
                    aDefault.defaultDestroyTime(), ResourcefulCreepers.createSingleList(name.toString(), 1), ResourcefulCreepers.defaultBiomes());

            CreeperTypeList.INSTANCE.creeperTypes.add(creeperType);
            amount++;
        }
        CreeperTypeList.updateFile();
        return amount;
    }


//    public static int generateFromOreTags()
//    {
//        generateOreData();
//        MAX_TIER = ResourcefulCreepersPlatform.getTierList().size();
//
//        //Clear out the old list
//        CreeperTypeList.INSTANCE.creeperTypes.clear();
//        List<Block> defaults = ResourcefulCreepersPlatform.getDefaults();
//        int amount = 0;
//        for (Block aDefault : defaults)
//        {
//            ResourceLocation name = BuiltInRegistries.BLOCK.getKey(aDefault);
//            ResourceLocation empty = ResourceLocation.withDefaultNamespace("empty");
//            if(name == empty)
//            {
//                ResourcefulCreepers.LOGGER.error("Unable to find blocks resource location for " + name + " Skipping");
//                continue;
//            }
//            OreGenData oreGenData = ORE_DATA.get(name);
//            if(oreGenData == null)
//            {
//                ResourcefulCreepers.LOGGER.error("Unable to find oreData for " + name + " Skipping");
//                continue;
//            }
//            int tier = -1;
//            if(calculateTier(aDefault.defaultBlockState()) >= 0)
//            {
//                tier = calculateTier(aDefault.defaultBlockState());
//                if(tier > MAX_TIER) tier = MAX_TIER;
//            }
//            else
//            {
//                ResourcefulCreepers.LOGGER.error("Unable to find calculate tier for " + name + " Skipping");
//                continue;
//            }
//
//            CreeperType creeperType = new CreeperType(name.getPath(), aDefault.getName().getString(), tier, ResourcefulCreepers.DEFAULT_COLOUR,
//                    ColorHelper.getRandomColour(new ItemStack(aDefault)), true, oreGenData.getWeight(), 1,4, true,
//                    aDefault.defaultDestroyTime(), ResourcefulCreepers.createSingleList(name.toString(), 1), ResourcefulCreepers.defaultBiomes());
//
//            CreeperTypeList.INSTANCE.creeperTypes.add(creeperType);
//            amount++;
//        }
//        CreeperTypeList.updateFile();
//        return amount;
//    }

    private static void generateOreData(Iterable<ServerLevel> levels) {
        System.out.println(levels);
    }

    private static void generateOreData(ServerLevel level) {
        WorldGenContext context = level.getChunkSource().chunkMap.worldGenContext;
        Set<Holder<Biome>> possibleBiomes = context.generator().getBiomeSource().possibleBiomes();

        ResourcefulCreepers.LOGGER.info(possibleBiomes);

        possibleBiomes.forEach(biomeHolder -> {
            Biome biome = biomeHolder.value();
            BiomeGenerationSettings genSettings = biome.getGenerationSettings();
            List<HolderSet<PlacedFeature>> features = genSettings.features();
            features.forEach(holders -> holders.forEach(holder -> {
                PlacedFeature placedFeature = holder.value();
                Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder = placedFeature.feature();
                ConfiguredFeature<?, ?> configuredFeature = configuredFeatureHolder.value();

                if (!(configuredFeature.config() instanceof OreConfiguration oreConfiguration)) return;
                for (OreConfiguration.TargetBlockState state : oreConfiguration.targetStates) {
                    if (!ResourcefulCreepersPlatform.isOre(state.state)) {
                        return;
                    }
                }

                AtomicInteger minWeight = new AtomicInteger();
                AtomicInteger minY = new AtomicInteger();
                AtomicInteger maxY = new AtomicInteger();

                placedFeature.placement().forEach(placementModifier -> {
                    if (placementModifier instanceof CountPlacement countPlacement) {

                        IntProvider intProvider = ((MixinCountPlacement) countPlacement).getcount();
                        minWeight.set(intProvider.getMaxValue());
                    }
                    if (placementModifier instanceof HeightRangePlacement heightRangePlacement) {
                        HeightProvider heightProvider = ((MixinHeightRangePlacement) heightRangePlacement).getheight();
                        if (heightProvider instanceof UniformHeight uniformHeight) {
                            VerticalAnchor verticalAnchor = ((MixinUniformHeight) uniformHeight).getminInclusive();
                            VerticalAnchor verticalAnchor2 = ((MixinUniformHeight) uniformHeight).getmaxInclusive();

                            //TODO Requires more brain power to track down
                            //int min = ((MixinVerticalAnchor)verticalAnchor).getvalue();
                            //int max = ((MixinVerticalAnchor)verticalAnchor2).getvalue();
                            minY.set(0);
                            maxY.set(0);
                        }
                        if (heightProvider instanceof TrapezoidHeight trapezoidHeight) {
                            VerticalAnchor verticalAnchor = ((MixinTrapezoidHeight) trapezoidHeight).getminInclusive();
                            VerticalAnchor verticalAnchor2 = ((MixinTrapezoidHeight) trapezoidHeight).getmaxInclusive();

                            //TODO Requires more brain power to track down
                            //int min = ((MixinVerticalAnchor)verticalAnchor).getvalue();
                            //int max = ((MixinVerticalAnchor)verticalAnchor2).getvalue();
                            minY.set(0);
                            maxY.set(0);
                        }
                    }
                });

                oreConfiguration.targetStates.forEach(targetBlockState ->
                {
                    ResourceLocation name = BuiltInRegistries.BLOCK.getKey(targetBlockState.state.getBlock());
                    ORE_DATA.put(name, new OreGenData(oreConfiguration.size, minWeight.get(), minY.get(), maxY.get()));
                });
            }));
        });
    }

    public static int calculateTier(BlockState blockState) {
        if (blockState.is(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)) {
            return 5;
        } else if (blockState.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) {
            return 4;
        } else if (blockState.is(BlockTags.INCORRECT_FOR_IRON_TOOL)) {
            return 3;
        } else if (blockState.is(BlockTags.INCORRECT_FOR_STONE_TOOL)) {
            return 2;
        } else if (blockState.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) {
            return 1;
        }
        return 0;
    }
}
