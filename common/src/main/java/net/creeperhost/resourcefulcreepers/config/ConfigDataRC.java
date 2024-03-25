package net.creeperhost.resourcefulcreepers.config;

import net.creeperhost.polylib.blue.endless.jankson.Comment;
import net.creeperhost.polylib.config.ConfigData;

public class ConfigDataRC extends ConfigData
{
    @Comment("Generate default Creepers")
    public boolean generateDefaultTypes = true;
//    @Comment("Generate Creepers using the ORES tag")
//    public boolean autoGenerateCreeperTypesFromOreTags = false;
    @Comment("Explosion size multiplier, Used to increase the range of the Creepers explosion")
    public float explosionMultiplier = 0.25F;
    @Comment("Explosion size multiplier for Powered Creepers, Used to increase the range of the Creepers explosion")
    public float poweredExplosionMultiplier = 0.5F;
    @Comment("Allow Creeper explosions to create ores")
    public boolean explosionsGenerateOres = true;
    @Comment("Disable Creepers dropping ores when killed")
    public boolean overrideOreDrops = false;
    @Comment("Add AI to attracts Creepers towards Armour stands")
    public boolean creepersAttractedToArmourStand = true;
    @Comment("Enable/Disable sentry support")
    public boolean disableSentry = false;
    @Comment("Set tamed Creepers to be non-hostile")
    public boolean nonHostileWhenTamed = true;
    @Comment("Force all fake air blocks to still be replaceable for Creeper explosions")
    public boolean forceAirBlock = true;
    @Comment("Enable/Disable nerfing Creepers to have a chance to not drop ores when killed via automation")
    public boolean nerfDropsWhenAutomated = false;
    @Comment("The percent that automated drops are reduced by")
    public int nerfDropPercentage = 20;
    @Comment("The percent chance of a automated drop not dropping anything")
    public int noDropChance = 20;
    @Comment("The entity damage range of the creeper explosion, this does not effect blocks.")
    public float explosionDamageRangeMultiplier = 1;
}
