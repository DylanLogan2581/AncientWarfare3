package xyz.dylanlogan.ancientwarfare.structure.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.structure.item.AWStructuresItemLoader;
import xyz.dylanlogan.ancientwarfare.structure.tile.TileSoundBlock;

public class BlockSoundBlock extends Block {

    public BlockSoundBlock() {
        super(Material.rock);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int face){
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity instanceof TileSoundBlock) {
            Block block = ((TileSoundBlock) tileEntity).getBlockCache();
            if (block != null) {
                return block.getIcon(face, 0);
            }
        }
        return getIcon(face, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta){
        return Blocks.jukebox.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){

    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileSoundBlock();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getCurrentEquippedItem();
        if(itemStack!=null && itemStack.getItem() instanceof ItemBlock){
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if(tileEntity instanceof TileSoundBlock) {
                ((TileSoundBlock)tileEntity).setBlockCache(itemStack);
            }
        }
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SOUND_BLOCK, x, y, z);
        }
        return true;
    }

}
