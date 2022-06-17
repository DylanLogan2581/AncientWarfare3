package xyz.dylanlogan.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import xyz.dylanlogan.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import xyz.dylanlogan.ancientwarfare.core.interfaces.ITorque;
import xyz.dylanlogan.ancientwarfare.core.model.ModelBaseAW;
import xyz.dylanlogan.ancientwarfare.core.model.ModelLoader;
import xyz.dylanlogan.ancientwarfare.core.model.ModelPiece;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWindmillBlades extends TileEntitySpecialRenderer implements IItemRenderer {

    private final ResourceLocation texture, cubeTexture;
    private final ModelBaseAW model, cube;
    private final ModelPiece windmillShaft, blade, bladeJoint, bladeShaft;

    public RenderWindmillBlades() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/windmill_blade.m2f"));
        cube = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/cube.m2f"));
        windmillShaft = model.getPiece("windmillShaft");
        blade = model.getPiece("blade");
        bladeJoint = model.getPiece("bladeJoint");
        bladeShaft = model.getPiece("bladeShaft");
        texture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_blade.png");
        cubeTexture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_blade_block.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta) {
        TileWindmillBlade blade = (TileWindmillBlade) te;
        if (blade.isControl) {
            GL11.glPushMatrix();
            bindTexture(texture);
            GL11.glTranslated(x + 0.5d, y + 0.5d, z + 0.5d);
            renderModel(-blade.getRotation(delta), blade.windmillDirection, (blade.windmillSize - 1) / 2);
            GL11.glPopMatrix();
        } else if (blade.controlPos == null) {
            GL11.glPushMatrix();
            bindTexture(cubeTexture);
            GL11.glTranslated(x + 0.5d, y + 0.5d, z + 0.5d);
            cube.renderModel();
            GL11.glPopMatrix();
        }
    }

    protected void renderModel(float bladeRotatation, int face, int height) {
        float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
        if (rot[0] != 0) {
            GL11.glRotatef(rot[0], 1, 0, 0);
        }
        else if (rot[1] != 0) {
            GL11.glRotatef(rot[1], 0, 1, 0);
        }

        float textureWidth = model.textureWidth();
        float textureHeight = model.textureHeight();
        GL11.glRotatef(bladeRotatation, 0, 0, 1);
        windmillShaft.render(model.textureWidth(), model.textureHeight());

        for (int i = 0; i < 4; i++) {
            GL11.glRotatef(90, 0, 0, 1);
            bladeShaft.render(textureWidth, textureHeight);
            for (int k = 1; k < height; k++) {
                blade.render(textureWidth, textureHeight);
                if (k == height - 1) {
                    bladeJoint.render(textureWidth, textureHeight);
                }
                else {
                    GL11.glTranslatef(0, 1, 0);
                }
            }
            GL11.glTranslatef(0, 2 - height, 0);
        }
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        bindTexture(texture);
        GL11.glTranslatef(0.5f, -0.25f, 0.5f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.35f, 0.35f, 0.35f);

        float textureWidth = model.textureWidth();
        float textureHeight = model.textureHeight();
        windmillShaft.setRotation(0, 0, 0);
        windmillShaft.render(textureWidth, textureHeight);
        int height = 5;

        bladeShaft.render(textureWidth, textureHeight);
        for (int k = 1; k < height; k++) {
            blade.render(textureWidth, textureHeight);
            if (k == height - 1) {
                bladeJoint.render(textureWidth, textureHeight);
            }
            else {
                GL11.glTranslatef(0, 1, 0);
            }
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
