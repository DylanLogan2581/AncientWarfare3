package xyz.dylanlogan.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.automation.item.AWAutomationItemLoader;
import xyz.dylanlogan.ancientwarfare.automation.tile.torque.TileHandGenerator;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockHandCrankedEngine extends BlockTorqueBase {

    protected BlockHandCrankedEngine(String regName) {
        super(Material.rock);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        this.setBlockName(regName);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileHandGenerator();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return Blocks.iron_block.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

}
