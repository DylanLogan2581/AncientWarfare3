package xyz.dylanlogan.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xyz.dylanlogan.ancientwarfare.core.config.AWLog;
import xyz.dylanlogan.ancientwarfare.core.crafting.AWCraftingManager;
import xyz.dylanlogan.ancientwarfare.core.interfaces.IInteractableTile;
import xyz.dylanlogan.ancientwarfare.core.inventory.InventoryBasic;
import xyz.dylanlogan.ancientwarfare.core.item.ItemResearchBook;
import xyz.dylanlogan.ancientwarfare.core.network.NetworkHandler;
import xyz.dylanlogan.ancientwarfare.core.util.InventoryTools;

public class TileWarehouseCraftingStation extends TileEntity implements IInteractableTile,IInvBasic {

    public InventoryCrafting layoutMatrix;
    public InventoryCraftResult result;
    public InventoryBasic bookInventory;

    ItemStack[] matrixShadow;

    public TileWarehouseCraftingStation() {
        Container c = new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer var1) {
                return true;
            }

            @Override
            public void onCraftMatrixChanged(IInventory par1iInventory) {
                onInventoryChanged(null);
            }
        };

        layoutMatrix = new InventoryCrafting(c, 3, 3);
        matrixShadow = new ItemStack[layoutMatrix.getSizeInventory()];
        result = new InventoryCraftResult();
        bookInventory = new InventoryBasic(1, this);
    }

    /**
     * called to shadow a copy of the input matrix, to know what to refill
     */
    public void preItemCrafted() {
        ItemStack stack;
        for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
            stack = layoutMatrix.getStackInSlot(i);
            matrixShadow[i] = stack == null ? null : stack.copy();
        }
    }

    public void onItemCrafted() {
        TileWarehouseBase warehouse = getWarehouse();
        if (warehouse == null) {
            return;
        }
        AWLog.logDebug("crafting item...");
        int q;
        ItemStack layoutStack;
        for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
            layoutStack = matrixShadow[i];
            if (layoutStack == null) {
                continue;
            }
            if (layoutMatrix.getStackInSlot(i) != null) {
                continue;
            }
            q = warehouse.getCountOf(layoutStack);
            AWLog.logDebug("warehouse count of: " + layoutStack + " :: " + q);
            if (q > 0) {
                warehouse.decreaseCountOf(layoutStack, 1);
                layoutStack = layoutStack.copy();
                layoutStack.stackSize = 1;
                layoutMatrix.setInventorySlotContents(i, layoutStack);
            }
        }
        if (!worldObj.isRemote) {
            warehouse.updateViewers();
        }
    }

    public final TileWarehouseBase getWarehouse() {
        if (yCoord <= 1)//could not possibly be a warehouse below...
        {
            return null;
        }
        TileEntity te = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
        if (te instanceof TileWarehouseBase) {
            return (TileWarehouseBase) te;
        }
        return null;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    private void onLayoutMatrixChanged() {
        this.result.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(layoutMatrix, worldObj, getCrafterName()));
    }

    public String getCrafterName() {
        return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
    }

    @Override
    public void setWorldObj(World world){
        super.setWorldObj(world);
        onLayoutMatrixChanged();
    }

    @Override
    public void onInventoryChanged(net.minecraft.inventory.InventoryBasic internal){
        onLayoutMatrixChanged();
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        InventoryTools.readInventoryFromNBT(bookInventory, tag.getCompoundTag("bookInventory"));
        InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
        InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagCompound inventoryTag = InventoryTools.writeInventoryToNBT(bookInventory, new NBTTagCompound());
        tag.setTag("bookInventory", inventoryTag);

        inventoryTag = InventoryTools.writeInventoryToNBT(result, new NBTTagCompound());
        tag.setTag("resultInventory", inventoryTag);

        inventoryTag = InventoryTools.writeInventoryToNBT(layoutMatrix, new NBTTagCompound());
        tag.setTag("layoutMatrix", inventoryTag);

    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CRAFTING, xCoord, yCoord, zCoord);
        }
        return true;
    }

}
