package xyz.dylanlogan.ancientwarfare.structure.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.block.BlockIconMap;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.structure.item.AWStructuresItemLoader;
import xyz.dylanlogan.ancientwarfare.structure.tile.TileDraftingStation;

public class BlockDraftingStation extends Block {

    private BlockIconMap iconMap = new BlockIconMap();

    public BlockDraftingStation() {
        super(Material.rock);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        setHardness(2.f);
    }

    public BlockDraftingStation setIcon(int side, String texName) {
        this.iconMap.setIconTexture(side, 0, texName);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess world, int x, int y, int z, int side) {
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconMap.registerIcons(reg);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIconFor(side, meta);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileDraftingStation();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_DRAFTING_STATION, x, y, z);
        }
        return true;
    }
}
