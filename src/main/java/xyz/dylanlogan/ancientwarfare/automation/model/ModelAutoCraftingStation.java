package xyz.dylanlogan.ancientwarfare.automation.model;

import net.minecraft.client.model.ModelRenderer;
import xyz.dylanlogan.ancientwarfare.core.model.crafting_table.ModelCraftingBase;

public class ModelAutoCraftingStation extends ModelCraftingBase {

    public ModelAutoCraftingStation() {
        ModelRenderer paperLarge = new ModelRenderer(this, "paperLarge");
        paperLarge.setTextureOffset(65, 0);
        paperLarge.rotationPointY = -14.01f;
        paperLarge.rotateAngleY = -0.052359793f;
        paperLarge.addBox(-6, 0, -6, 12, 0, 12);
        addPiece(paperLarge);
        addHammer();
        addSaw();
    }

    private void addSaw() {
        ModelRenderer sawBlade1 = new ModelRenderer(this, "sawBlade1");
        sawBlade1.setTextureOffset(0, 34);
        sawBlade1.setRotationPoint(5, -15, -2);
        sawBlade1.addBox(0, 0, 0, 1, 1, 9);
        ModelRenderer sawHandle1 = new ModelRenderer(this, "sawHandle1");
        sawHandle1.setTextureOffset(0, 45);
        sawHandle1.setRotationPoint(-3, 0, -1);
        sawHandle1.addBox(0, 0, 0, 4, 1, 1);
        sawBlade1.addChild(sawHandle1);
        ModelRenderer sawHandle2 = new ModelRenderer(this, "sawHandle2");
        sawHandle2.setTextureOffset(0, 48);
        sawHandle2.setRotationPoint(-3, 0, -3);
        sawHandle2.addBox(0, 0, 0, 4, 1, 1);
        sawBlade1.addChild(sawHandle2);
        ModelRenderer sawHandle3 = new ModelRenderer(this, "sawHandle3");
        sawHandle3.setTextureOffset(0, 51);
        sawHandle3.setRotationPoint(-3, 0, -2);
        sawHandle3.addBox(0, 0, 0, 1, 1, 1);
        sawBlade1.addChild(sawHandle3);
        ModelRenderer sawHandle4 = new ModelRenderer(this, "sawHandle4");
        sawHandle4.setTextureOffset(5, 51);
        sawHandle4.setRotationPoint(0, 0, -2);
        sawHandle4.addBox(0, 0, 0, 1, 1, 1);
        sawBlade1.addChild(sawHandle4);
        ModelRenderer sawBlade2 = new ModelRenderer(this, "sawBlade2");
        sawBlade2.setTextureOffset(21, 34);
        sawBlade2.setRotationPoint(1, 0.01f, 9);
        setPieceRotation(sawBlade2, 0, 0.10471966f, 0);
        sawBlade2.addBox(-2, 0, -10, 2, 1, 10);
        float toothAngle = 0.7853982f;
        ModelRenderer sawTooth1 = new ModelRenderer(this, "sawTooth1");
        sawTooth1.setTextureOffset(0, 54);
        sawTooth1.setRotationPoint(-2, 0.51f, -0.75f);
        sawTooth1.rotateAngleY = toothAngle;
        sawTooth1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth1);
        ModelRenderer sawTooth2 = new ModelRenderer(this, "sawTooth2");
        sawTooth2.setTextureOffset(0, 54);
        sawTooth2.setRotationPoint(-2, 0.51f, -1.75f);
        sawTooth2.rotateAngleY = toothAngle;
        sawTooth2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth2);
        ModelRenderer sawTooth3 = new ModelRenderer(this, "sawTooth3");
        sawTooth3.setTextureOffset(0, 54);
        sawTooth3.setRotationPoint(-2, 0.51f, -2.75f);
        sawTooth3.rotateAngleY = toothAngle;
        sawTooth3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth3);
        ModelRenderer sawTooth4 = new ModelRenderer(this, "sawTooth4");
        sawTooth4.setTextureOffset(0, 54);
        sawTooth4.setRotationPoint(-2, 0.51f, -3.75f);
        sawTooth4.rotateAngleY = toothAngle;
        sawTooth4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth4);
        ModelRenderer sawTooth5 = new ModelRenderer(this, "sawTooth5");
        sawTooth5.setTextureOffset(0, 54);
        sawTooth5.setRotationPoint(-2, 0.51f, -4.75f);
        sawTooth5.rotateAngleY = toothAngle;
        sawTooth5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth5);
        ModelRenderer sawTooth6 = new ModelRenderer(this, "sawTooth6");
        sawTooth6.setTextureOffset(0, 54);
        sawTooth6.setRotationPoint(-2, 0.51f, -5.75f);
        sawTooth6.rotateAngleY = toothAngle;
        sawTooth6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth6);
        ModelRenderer sawTooth7 = new ModelRenderer(this, "sawTooth7");
        sawTooth7.setTextureOffset(0, 54);
        sawTooth7.setRotationPoint(-2, 0.51f, -6.75f);
        sawTooth7.rotateAngleY = toothAngle;
        sawTooth7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth7);
        ModelRenderer sawTooth8 = new ModelRenderer(this, "sawTooth8");
        sawTooth8.setTextureOffset(0, 54);
        sawTooth8.setRotationPoint(-2, 0.51f, -7.75f);
        sawTooth8.rotateAngleY = toothAngle;
        sawTooth8.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth8);
        ModelRenderer sawTooth9 = new ModelRenderer(this, "sawTooth9");
        sawTooth9.setTextureOffset(0, 54);
        sawTooth9.setRotationPoint(-2, 0.51f, -8.75f);
        sawTooth9.rotateAngleY = toothAngle;
        sawTooth9.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        sawBlade2.addChild(sawTooth9);
        sawBlade1.addChild(sawBlade2);
        addPiece(sawBlade1);
    }
}
