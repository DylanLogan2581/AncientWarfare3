package xyz.dylanlogan.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import xyz.dylanlogan.ancientwarfare.automation.tile.torque.TileFlywheelControl;
import xyz.dylanlogan.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import xyz.dylanlogan.ancientwarfare.core.model.ModelBaseAW;
import xyz.dylanlogan.ancientwarfare.core.model.ModelLoader;
import xyz.dylanlogan.ancientwarfare.core.model.ModelPiece;
import org.lwjgl.opengl.GL11;

public class RenderTileTorqueFlywheelController extends TileEntitySpecialRenderer implements IItemRenderer {

    private float[][] gearboxRotationMatrix = new float[6][];
    private final ModelBaseAW controllerModel;
    private final ModelPiece controlInput, controlOutput, controlSpindle;

    private final ResourceLocation texture[] = new ResourceLocation[3];//1, tex2, tex3;

    public RenderTileTorqueFlywheelController() {
        texture[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_light.png");
        texture[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_medium.png");
        texture[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_heavy.png");

        ModelLoader loader = new ModelLoader();
        controllerModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_controller.m2f"));
        controlInput = controllerModel.getPiece("inputGear");
        controlOutput = controllerModel.getPiece("outputGear");
        controlSpindle = controllerModel.getPiece("spindle");

        gearboxRotationMatrix[0] = new float[]{-90, 0, 0};//d
        gearboxRotationMatrix[1] = new float[]{90, 0, 0};//u
        gearboxRotationMatrix[2] = new float[]{0, 0, 0};//n
        gearboxRotationMatrix[3] = new float[]{0, 180, 0};//s
        gearboxRotationMatrix[4] = new float[]{0, 90, 0};//w
        gearboxRotationMatrix[5] = new float[]{0, 270, 0};//e
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5d, y, z + 0.5d);

        TileFlywheelControl flywheel = (TileFlywheelControl) te;

        ITorqueTile[] neighbors = flywheel.getTorqueCache();
        ForgeDirection d = flywheel.getPrimaryFacing();

        float outputRotation = flywheel.getClientOutputRotation(d, delta);
        float inputRotation = outputRotation;
        float flywheelRotation = flywheel.getFlywheelRotation(delta);

        ITorqueTile inputNeighbor = neighbors[d.getOpposite().ordinal()];
        if (inputNeighbor != null && inputNeighbor.canOutputTorque(d) && inputNeighbor.useOutputRotation(d.getOpposite())) {
            inputRotation = inputNeighbor.getClientOutputRotation(d.getOpposite(), delta);
        }

        bindTexture(texture[te.getBlockMetadata() % texture.length]);
        renderModel(outputRotation, inputRotation, flywheelRotation, d.ordinal());
        GL11.glPopMatrix();
    }

    protected void renderModel(float outR, float inR, float wheelR, int face) {
        float[] rot = gearboxRotationMatrix[face];
        if (rot[0] != 0) {
            GL11.glRotatef(rot[0], 1, 0, 0);
        }
        if (rot[1] != 0) {
            GL11.glRotatef(rot[1], 0, 1, 0);
        }
        if (rot[2] != 0) {
            GL11.glRotatef(rot[2], 0, 0, 1);
        }
        controlInput.setRotation(0, 0, -inR);
        controlOutput.setRotation(0, 0, -outR);
        controlSpindle.setRotation(0, -wheelR, 0);
        controllerModel.renderModel();
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
        GL11.glTranslatef(0.5f, 0, 0.5f);
        bindTexture(texture[item.getItemDamage() % texture.length]);
        renderModel(0, 0, 0, 2);
        GL11.glPopMatrix();
    }

}
