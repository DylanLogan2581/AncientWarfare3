package xyz.dylanlogan.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xyz.dylanlogan.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import xyz.dylanlogan.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerTileBase;
import xyz.dylanlogan.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWorksiteBase extends ContainerTileBase<TileWorksiteBoundedInventory> {

    public final InventorySided inventory;
    public int guiHeight, topLabel, frontLabel, bottomLabel, rearLabel, leftLabel, rightLabel, playerLabel;

    public ContainerWorksiteBase(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        inventory = tileEntity.inventory;
    }

    protected int addSlots(int xPosStart, int yPosStart, int firstSlotIndex, int numberOfSlots) {
        SlotFiltered slot;
        int x1, y1, xPos, yPos;
        int maxY = 0;
        for (int i = 0, slotNum = firstSlotIndex; i < numberOfSlots; i++, slotNum++) {
            x1 = i % 9;
            y1 = i / 9;
            xPos = xPosStart + x1 * 18;
            yPos = yPosStart + y1 * 18;
            if (yPos + 18 > maxY) {
                maxY = yPos + 18;
            }
            slot = new SlotFiltered(inventory, slotNum, xPos, yPos, inventory.getFilterForSlot(slotNum));
            addSlotToContainer(slot);
        }
        return maxY;
    }

    /**
     * @return should always return null for normal implementation, not sure wtf the rest of the code is about
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        Slot slot = this.getSlot(slotClickedIndex);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }
        int slots = tileEntity.getSizeInventory();
        ItemStack stackFromSlot = slot.getStack();
        if (slotClickedIndex < slots) {
            this.mergeItemStack(stackFromSlot, slots, slots + playerSlots, false);
        } else {
            this.mergeItemStack(stackFromSlot, 0, slots, true);
        }
        if (stackFromSlot.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        return null;
    }

}
