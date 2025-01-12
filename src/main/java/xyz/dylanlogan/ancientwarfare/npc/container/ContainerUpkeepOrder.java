package xyz.dylanlogan.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.npc.orders.UpkeepOrder;

public class ContainerUpkeepOrder extends ContainerBase {

    public final UpkeepOrder upkeepOrder;
    private boolean hasChanged;

    public ContainerUpkeepOrder(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("Cannot open Work Order GUI for null stack/item.");
        }
        upkeepOrder = UpkeepOrder.getUpkeepOrder(stack);
        if (upkeepOrder == null) {
            throw new IllegalArgumentException("Upkeep orders was null for some reason");
        }
        addPlayerSlots();
        removeSlots();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("upkeepOrder")) {
            upkeepOrder.readFromNBT(tag.getCompoundTag("upkeepOrder"));
            hasChanged = true;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (hasChanged && !player.worldObj.isRemote) {
            upkeepOrder.write(player.getCurrentEquippedItem());
        }
    }

    public void onClose() {
        NBTTagCompound outer = new NBTTagCompound();
        outer.setTag("upkeepOrder", upkeepOrder.writeToNBT(new NBTTagCompound()));
        sendDataToServer(outer);
    }
}
