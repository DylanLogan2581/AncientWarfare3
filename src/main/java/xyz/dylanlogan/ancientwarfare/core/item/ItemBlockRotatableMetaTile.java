package xyz.dylanlogan.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IOwnable;

public class ItemBlockRotatableMetaTile extends ItemBlock {

    IRotatableBlock rotatable;

    public ItemBlockRotatableMetaTile(Block block) {
        super(block);
        if (!(block instanceof IRotatableBlock)) {
            throw new IllegalArgumentException("Must be a rotatable block!!");
        }
        rotatable = (IRotatableBlock) block;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (val) {
            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te instanceof IOwnable) {
                ((IOwnable) te).setOwner(player);
            }
            if (te instanceof IRotatableTile) {
                ((IRotatableTile) te).setPrimaryFacing(BlockRotationHandler.getFaceForPlacement(player, rotatable, side));
            }
            player.worldObj.markBlockForUpdate(x, y, z);
        }
        return val;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

}
