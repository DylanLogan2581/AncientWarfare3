package xyz.dylanlogan.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import xyz.dylanlogan.ancientwarfare.api.IAncientWarfarePlantable;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler;
import xyz.dylanlogan.ancientwarfare.core.util.BlockPosition;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased {

    protected static final int TOP_LENGTH = 27, FRONT_LENGTH = 3, BOTTOM_LENGTH = 3;
    private static final int SIZE = 16;
    private byte[] targetMap = new byte[SIZE * SIZE];

    /**
     * flag should be set to true whenever updating inventory internally (e.g. harvesting blocks) to prevent
     * unnecessary inventory rescanning.  should be set back to false after blocks are added to inventory
     */
    private boolean shouldCountResources;

    public TileWorksiteUserBlocks() {
        this.shouldCountResources = true;
        this.inventory = new SlotListener(TOP_LENGTH + FRONT_LENGTH + BOTTOM_LENGTH);
    }

    @Override
    public final boolean userAdjustableBlocks() {
        return true;
    }

    protected boolean isTarget(BlockPosition p) {
        return isTarget(p.x, p.z);
    }

    protected boolean isTarget(int x1, int y1) {
        int z = (y1 - getWorkBoundsMin().z) * SIZE + x1 - getWorkBoundsMin().x;
        return z >= 0 && z < targetMap.length && targetMap[z] == 1;
    }

    protected boolean isBonemeal(ItemStack stack) {
        return stack.getItem() == Items.dye && stack.getItemDamage() == 15;
    }

    protected boolean isFarmable(Block block){
        try{
            return isFarmable(block, 0, 0, 0);
        }catch (Exception e){
            return false;
        }
    }

    protected boolean isFarmable(Block block, int x, int y, int z){
        return block instanceof IPlantable;
    }

    protected boolean canReplace(int x, int y, int z){
        return worldObj.getBlock(x, y, z).isReplaceable(worldObj, x, y, z);
    }

    protected boolean tryPlace(ItemStack stack, int x, int y, int z, ForgeDirection face){
        ForgeDirection direction = face.getOpposite();
        if(stack.getItem() instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) stack.getItem()).tryPlant(worldObj, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, stack.copy());
        }
        EntityPlayer owner = getOwnerAsPlayer();
        if(owner.isEntityInvulnerable()){
            owner.inventory.setInventorySlotContents(owner.inventory.currentItem, stack);
        }
        return stack.tryPlaceItemIntoWorld(owner, worldObj, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, face.ordinal(), 0.25F, 0.25F, 0.25F);
    }

    protected final void pickupItems() {
        List<EntityItem> items = getEntitiesWithinBounds(EntityItem.class);
        if(items.isEmpty())
            return;
        int[] indices = getIndicesForPickup();
        ItemStack stack;
        for (EntityItem item : items) {
            if(item.isEntityAlive()) {
                stack = item.getEntityItem();
                if (stack != null) {
                    stack = InventoryTools.mergeItemStack(inventory, stack, indices);
                    if (stack != null) {
                        item.setEntityItemStack(stack);
                    }else{
                        item.setDead();
                    }
                }
            }
        }
    }

    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined();
    }

    @Override
    protected void validateCollection(Collection<BlockPosition> blocks) {
        if(!hasWorkBounds()){
            blocks.clear();
            return;
        }
        Iterator<BlockPosition> it = blocks.iterator();
        BlockPosition pos;
        while (it.hasNext() && (pos = it.next()) != null) {
            if (!isInBounds(pos) || !isTarget(pos)) {
                it.remove();
            }
        }
    }

    @Override
    protected void fillBlocksToProcess(Collection<BlockPosition> targets) {
        BlockPosition min = getWorkBoundsMin();
        BlockPosition max = getWorkBoundsMax();
        for (int x = min.x; x < max.x + 1; x++) {
            for (int z = min.z; z < max.z + 1; z++) {
                if (isTarget(x, z)) {
                    targets.add(new BlockPosition(x, min.y, z));
                }
            }
        }
    }

    //TODO implement to check target blocks, clear invalid ones
    public void onTargetsAdjusted() {
        onBoundsAdjusted();
    }

    @Override
    protected void onBoundsSet() {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                targetMap[z * SIZE + x] = (byte) 1;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByteArray("targetMap", targetMap);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("targetMap")) {
            targetMap = tag.getByteArray("targetMap");
        }
    }

    public byte[] getTargetMap() {
        return targetMap;
    }

    public void setTargetBlocks(byte[] targets) {
        boolean change = !Objects.deepEquals(targetMap, targets);
        targetMap = targets;
        if(change) {
            onTargetsAdjusted();
            markDirty();
        }
    }

    @Override
    protected void updateBlockWorksite() {
        worldObj.theProfiler.startSection("Items Pickup");
        if (worldObj.getWorldTime() % 20 == 0) {
            pickupItems();
        }
        worldObj.theProfiler.endStartSection("Count Resources");
        if (shouldCountResources) {
            countResources();
            shouldCountResources = false;
        }
        worldObj.theProfiler.endSection();
    }

    protected abstract void countResources();

    protected final class SlotListener extends BlockRotationHandler.InventorySided{

        public SlotListener(int inventorySize) {
            super(TileWorksiteUserBlocks.this, BlockRotationHandler.RotationType.FOUR_WAY, inventorySize);
        }

        @Override
        public ItemStack decrStackSize(int var1, int var2) {
            ItemStack result = super.decrStackSize(var1, var2);
            if(result != null && getFilterForSlot(var1) != null)
                shouldCountResources = true;
            return result;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int var1) {
            ItemStack result = super.getStackInSlotOnClosing(var1);
            if(result != null && getFilterForSlot(var1) != null)
                shouldCountResources = true;
            return result;
        }

        @Override
        public void setInventorySlotContents(int var1, ItemStack var2) {
            super.setInventorySlotContents(var1, var2);
            if(getFilterForSlot(var1) != null)
                shouldCountResources = true;
        }
    }
}
