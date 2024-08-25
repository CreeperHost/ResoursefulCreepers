package net.creeperhost.resourcefulcreepers.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class ResourcefulCreeperModel<EntityResourcefulCreeper extends Entity> extends AgeableListModel<EntityResourcefulCreeper>
{
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private static final int Y_OFFSET = 6;

    public ResourcefulCreeperModel(ModelPart modelPart) {
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.leftHindLeg = modelPart.getChild("right_hind_leg");
        this.rightHindLeg = modelPart.getChild("left_hind_leg");
        this.leftFrontLeg = modelPart.getChild("right_front_leg");
        this.rightFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, 6.0F, 0.0F));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 6.0F, 0.0F));
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation);
        partDefinition.addOrReplaceChild("right_hind_leg", cubeListBuilder, PartPose.offset(-2.0F, 18.0F, 4.0F));
        partDefinition.addOrReplaceChild("left_hind_leg", cubeListBuilder, PartPose.offset(2.0F, 18.0F, 4.0F));
        partDefinition.addOrReplaceChild("right_front_leg", cubeListBuilder, PartPose.offset(-2.0F, 18.0F, -4.0F));
        partDefinition.addOrReplaceChild("left_front_leg", cubeListBuilder, PartPose.offset(2.0F, 18.0F, -4.0F));
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(EntityResourcefulCreeper entity, float f, float g, float h, float i, float j) {
        this.head.yRot = i * 0.017453292F;
        this.head.xRot = j * 0.017453292F;
        this.rightHindLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * g;
        this.leftHindLeg.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
        this.rightFrontLeg.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
        this.leftFrontLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * g;
    }

    @Override
    protected Iterable<ModelPart> headParts()
    {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts()
    {
        return ImmutableList.of(this.head, this.root, this.leftHindLeg, this.rightHindLeg, this.leftFrontLeg, this.rightFrontLeg);
    }
}
