package xyz.dylanlogan.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

import java.util.ArrayList;
import java.util.List;

public class POTrade extends Trade {

    private List<ItemStack> compactInput = new ArrayList<ItemStack>();
    private List<ItemStack> compactOutput = new ArrayList<ItemStack>();

    @Override
    public void setInputStack(int index, ItemStack stack) {
        super.setInputStack(index, stack);
        updateCompactInput();
    }

    @Override
    public void setOutputStack(int index, ItemStack stack) {
        super.setOutputStack(index, stack);
        updateCompactOutput();
    }

    private void updateCompactInput() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack temp : input) {
            if (temp != null) {
                list.add(temp.copy());
            }
        }
        compactInput = InventoryTools.compactStackList3(list);
    }

    private void updateCompactOutput() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack temp : output) {
            if (temp != null) {
                list.add(temp.copy());
            }
        }
        compactOutput = InventoryTools.compactStackList3(list);
    }

    /**
     * Check through the input inventory and ensure it contains all materials necessary to complete this trade.<br>
     */
    public boolean isAvailable(IInventory storage) {
        for (ItemStack stack : compactOutput) {
            if (InventoryTools.getCountOf(storage, -1, stack) < stack.stackSize) {
                return false;
            }
        }
        return InventoryTools.canInventoryHold(storage, -1, compactInput);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        updateCompactInput();
        updateCompactOutput();
    }

}
