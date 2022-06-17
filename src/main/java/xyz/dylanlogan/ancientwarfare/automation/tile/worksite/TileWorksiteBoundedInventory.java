package xyz.dylanlogan.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import xyz.dylanlogan.ancientwarfare.api.IAncientWarfareFarmable;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.BlockTools;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

import java.util.List;

/**
 * abstract base class for worksite based tile-entities (or at least a template to copy from)
 * <p/>
 * handles the management of worker references and work-bounds, as well as inventory bridge methods.
 * <p/>
 * All implementing classes must initialize the inventory field in their constructor, or things
 * will go very crashy when the block is placed in the world.
 *
 * 
 */
public abstract class TileWorksiteBoundedInventory extends TileWorksiteBounded implements ISidedInventory {

    public InventorySided inventory;

    public TileWorksiteBoundedInventory() {

    }

    public void openAltGui(EntityPlayer player) {
        //noop, must be implemented by individual tiles, if they have an alt-control gui
    }

    /**
     * attempt to add an item stack to this worksites inventory.<br>
     * iterates through input sides in the order given,
     * so should pick the most restrictive inventory first,
     * least restrictive last
     */
    public final void addStackToInventory(ItemStack stack, RelativeSide... sides) {
        int[] slots = inventory.getRawIndicesCombined(sides);
        stack = InventoryTools.mergeItemStack(inventory, stack, slots);
        if (stack != null) {
            InventoryTools.dropItemInWorld(worldObj, stack, xCoord, yCoord, zCoord);
        }
    }

    protected boolean harvestBlock(int x, int y, int z, RelativeSide... relativeSides) {
        int[] combinedIndices = inventory.getRawIndicesCombined(relativeSides);
        Block block = worldObj.getBlock(x, y, z);
        List<ItemStack> stacks;
        if(block instanceof IAncientWarfareFarmable) {
            stacks = ((IAncientWarfareFarmable) block).doHarvest(worldObj, x, y, z, getFortune());
        } else {
            int meta = worldObj.getBlockMetadata(x, y, z);
            stacks = block.getDrops(worldObj, x, y, z, meta, getFortune());
            if (!InventoryTools.canInventoryHold(inventory, combinedIndices, stacks)) {
                return false;
            }
            if (!BlockTools.canBreakBlock(worldObj, getOwnerAsPlayer(), x, y, z, block, meta)) {
                return false;
            }
            worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            if (!worldObj.setBlockToAir(x, y, z)) {
                return false;
            }
        }
        for (ItemStack stack : stacks) {
            stack = InventoryTools.mergeItemStack(inventory, stack, combinedIndices);//was already validated that items would fit via canInventoryHold call
            if (stack != null)//but just in case, drop into world anyway if not null..
            {
                InventoryTools.dropItemInWorld(worldObj, stack, xCoord, yCoord, zCoord);
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getEntitiesWithinBounds(Class<T> clazz){
        BlockPosition p1 = getWorkBoundsMin();
        BlockPosition p2 = getWorkBoundsMax();
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(p1.x, p1.y, p1.z, p2.x + 1, p2.y + 1, p2.z + 1);
        return worldObj.getEntitiesWithinAABB(clazz, bb);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (inventory != null) {
            NBTTagCompound invTag = new NBTTagCompound();
            inventory.writeToNBT(invTag);
            tag.setTag("inventory", invTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("inventory") && inventory != null) {
            inventory.readFromNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public final int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public final ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public final ItemStack decrStackSize(int var1, int var2) {
        return inventory.decrStackSize(var1, var2);
    }

    @Override
    public final ItemStack getStackInSlotOnClosing(int var1) {
        return inventory.getStackInSlotOnClosing(var1);
    }

    @Override
    public final void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
    }

    @Override
    public final String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public final boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public final int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public final boolean isUseableByPlayer(EntityPlayer var1) {
        return inventory.isUseableByPlayer(var1);
    }

    @Override
    public final void openInventory() {
        inventory.openInventory();
    }

    @Override
    public final void closeInventory() {
        inventory.closeInventory();
    }

    @Override
    public final boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public final int[] getAccessibleSlotsFromSide(int var1) {
        return inventory.getAccessibleSlotsFromSide(var1);
    }

    @Override
    public final boolean canInsertItem(int var1, ItemStack var2, int var3) {
        return inventory.canInsertItem(var1, var2, var3);
    }

    @Override
    public final boolean canExtractItem(int var1, ItemStack var2, int var3) {
        return inventory.canExtractItem(var1, var2, var3);
    }

}
