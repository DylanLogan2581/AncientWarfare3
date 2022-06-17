package xyz.dylanlogan.ancientwarfare.core.model.crafting_table;

import net.minecraft.client.model.ModelRenderer;

public class ModelResearchStation extends ModelCraftingBase {

    public ModelResearchStation() {
        ModelRenderer bookBottomCover = new ModelRenderer(this, "bookBottomCover");
        bookBottomCover.setTextureOffset(0, 35);
        bookBottomCover.setRotationPoint(-6, -15.01f, -1);
        bookBottomCover.addBox(0, 0, 0, 5, 1, 8);
        addPiece(bookBottomCover);
        ModelRenderer paper1 = new ModelRenderer(this, "paper1");
        paper1.setTextureOffset(65, 0);
        paper1.setRotationPoint(2, -14.01f, 3);
        paper1.rotateAngleY = -0.36651903f;
        paper1.addBox(-2, 0, -3, 4, 0, 6);
        addPiece(paper1);
        ModelRenderer paper2 = new ModelRenderer(this, "paper2");
        paper2.setTextureOffset(65, 7);
        paper2.setRotationPoint(4, -14.02f, -3);
        paper2.rotateAngleY = 0.29670596f;
        paper2.addBox(-2, 0, -3, 4, 0, 6);
        addPiece(paper2);
        ModelRenderer paper3 = new ModelRenderer(this, "paper3");
        paper3.setTextureOffset(65, 14);
        paper3.setRotationPoint(-2, -14.01f, -4);
        paper3.rotateAngleY = -0.3141596f;
        paper3.addBox(-2, 0, -3, 4, 0, 6);
        addPiece(paper3);
    }
}
