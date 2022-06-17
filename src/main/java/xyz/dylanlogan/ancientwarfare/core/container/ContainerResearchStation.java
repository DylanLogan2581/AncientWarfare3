package xyz.dylanlogan.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import xyz.dylanlogan.ancientwarfare.core.config.AWCoreStatics;
import xyz.dylanlogan.ancientwarfare.core.item.ItemResearchBook;
import xyz.dylanlogan.ancientwarfare.core.research.ResearchTracker;
import xyz.dylanlogan.ancientwarfare.core.tile.TileResearchStation;

import java.util.ArrayList;
import java.util.List;

public class ContainerResearchStation extends ContainerTileBase<TileResearchStation> {

    public boolean useAdjacentInventory;
    public String researcherName;
    public int currentGoal = -1;
    public int progress = 0;
    public List<Integer> queuedResearch = new ArrayList<Integer>();

    public ContainerResearchStation(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        researcherName = tileEntity.getCrafterName();
        useAdjacentInventory = tileEntity.useAdjacentInventory;
        if (!player.worldObj.isRemote) {
            if (researcherName != null) {
                currentGoal = ResearchTracker.INSTANCE.getCurrentGoal(player.worldObj, researcherName);
                progress = ResearchTracker.INSTANCE.getProgress(player.worldObj, researcherName);
                queuedResearch.addAll(ResearchTracker.INSTANCE.getResearchQueueFor(player.worldObj, researcherName));
            }
        }

        int slotNum = 0;
        Slot slot = new Slot(tileEntity, slotNum, 8, 18 + 4) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return ItemResearchBook.getResearcherName(par1ItemStack) != null;
            }
        };
        addSlotToContainer(slot);

        int x2, y2, yBase = 8 + 3 * 18 + 10 + 12 + 4 + 10;
        for (int y1 = 0; y1 < 3; y1++) {
            y2 = y1 * 18 + yBase;
            for (int x1 = 0; x1 < 3; x1++) {
                x2 = x1 * 18 + 8 + 18;//x1*18 + 8 + 3*18;
                slotNum++;
                slot = new Slot(tileEntity, slotNum, x2, y2);
                addSlotToContainer(slot);
            }
        }

        this.addPlayerSlots();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();

            int playerSlotStart = tileEntity.getSizeInventory();
            int playerSlotEnd = playerSlotStart + playerSlots;
            if (slotClickedIndex < playerSlotStart)//book , storage slot
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex < playerSlotEnd)//player slots, merge into storage
            {
                if (!this.mergeItemStack(slotStack, 1, playerSlotStart, false))//merge into storage
                {
                    return null;
                }
            }
            if (slotStack.stackSize == 0) {
                theSlot.putStack(null);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.stackSize == slotStackCopy.stackSize) {
                return null;
            }
            theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        if (researcherName != null) {
            tag.setString("researcherName", researcherName);
        } else {
            tag.setBoolean("clearResearcher", true);
        }
        tag.setInteger("currentGoal", currentGoal);
        tag.setInteger("progress", progress);
        if (!queuedResearch.isEmpty()) {
            int[] queueData = new int[queuedResearch.size()];
            for (int i = 0; i < queuedResearch.size(); i++) {
                queueData[i] = queuedResearch.get(i);
            }
            tag.setIntArray("queuedResearch", queueData);
        }
        tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
        tag.setInteger("inventoryDirection", tileEntity.inventoryDirection.ordinal());
        tag.setInteger("inventorySide", tileEntity.inventorySide.ordinal());
        this.sendDataToClient(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("researcherName")) {
            researcherName = tag.getString("researcherName");
        }
        else if (tag.hasKey("clearResearcher")) {
            researcherName = null;
        }
        if (tag.hasKey("currentGoal")) {
            currentGoal = tag.getInteger("currentGoal");
        }
        if (tag.hasKey("progress")) {
            progress = tag.getInteger("progress");
        }
        if (tag.hasKey("queuedResearch")) {
            queuedResearch.clear();
            int[] data = tag.getIntArray("queuedResearch");
            for (int i : data) {
                queuedResearch.add(i);
            }
        }
        if (this.researcherName == null) {
            this.queuedResearch.clear();
            this.progress = 0;
            this.currentGoal = -1;
        }
        if (tag.hasKey("useAdjacentInventory")) {
            this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
            if (!player.worldObj.isRemote) {
                tileEntity.useAdjacentInventory = useAdjacentInventory;
            }
        }
        if (tag.hasKey("inventoryDirection")) {
            tileEntity.inventoryDirection = ForgeDirection.getOrientation(tag.getInteger("inventoryDirection"));
        }
        if (tag.hasKey("inventorySide")) {
            tileEntity.inventorySide = ForgeDirection.getOrientation(tag.getInteger("inventorySide"));
        }
        if(!player.worldObj.isRemote){
            tileEntity.markDirty();
        }
        this.refreshGui();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (player.worldObj.isRemote) {
            return;
        }
        tileEntity.addTorque(ForgeDirection.UNKNOWN, AWCoreStatics.researchPerTick);//do research whenever the GUI is open
        NBTTagCompound tag = null;
        String name = tileEntity.getCrafterName();
        /**
         * synch researcher name
         */
        if (name == null && researcherName == null) {

        } else if (name == null) {
            tag = new NBTTagCompound();
            researcherName = null;
            tag.setBoolean("clearResearcher", true);
            tag.setInteger("currentGoal", -1);
            tag.setInteger("progress", 0);
        } else if (researcherName == null || !name.equals(researcherName)) {
            tag = new NBTTagCompound();
            researcherName = name;
            tag.setString("researcherName", name);
            currentGoal = ResearchTracker.INSTANCE.getCurrentGoal(player.worldObj, researcherName);
            tag.setInteger("currentGoal", currentGoal);
            progress = ResearchTracker.INSTANCE.getProgress(player.worldObj, researcherName);
            tag.setInteger("progress", progress);
        }else {
            int g = ResearchTracker.INSTANCE.getCurrentGoal(player.worldObj, researcherName);
            if (g != currentGoal) {
                tag = new NBTTagCompound();
                currentGoal = g;
                tag.setInteger("currentGoal", currentGoal);
            }
            int p = ResearchTracker.INSTANCE.getProgress(player.worldObj, researcherName);
            if (p != progress) {
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                progress = p;
                tag.setInteger("progress", progress);
            }
        }

        /**
         * synch queued research
         */
        if (researcherName != null) {
            List<Integer> queue = ResearchTracker.INSTANCE.getResearchQueueFor(player.worldObj, researcherName);
            if (!queue.equals(queuedResearch)) {
                if (tag == null) {
                    tag = new NBTTagCompound();
                }
                queuedResearch.clear();
                queuedResearch.addAll(queue);
                int[] queueData = new int[queue.size()];
                for (int i = 0; i < queue.size(); i++) {
                    queueData[i] = queue.get(i);
                }
                tag.setIntArray("queuedResearch", queueData);
            }
        } else {
            queuedResearch.clear();
        }

        /**
         * synch use-adjacent inventory status
         */
        if (tileEntity.useAdjacentInventory != useAdjacentInventory) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            this.useAdjacentInventory = tileEntity.useAdjacentInventory;
            tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
        }

        if (tag != null) {
            this.sendDataToClient(tag);
        }

    }

    /**
     * should be called from client-side to send update packet to server
     */
    public void toggleUseAdjacentInventory() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("useAdjacentInventory", !useAdjacentInventory);
        useAdjacentInventory = !useAdjacentInventory;
        sendDataToServer(tag);
    }

    public void onSidePressed() {
        int o = (tileEntity.inventorySide.ordinal() + 1)% ForgeDirection.VALID_DIRECTIONS.length;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("inventorySide", o);
        sendDataToServer(tag);
        tileEntity.inventorySide = ForgeDirection.getOrientation(o);
    }

    public void onDirPressed() {
        int o = (tileEntity.inventoryDirection.ordinal() + 1)% ForgeDirection.VALID_DIRECTIONS.length;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("inventoryDirection", o);
        sendDataToServer(tag);
        tileEntity.inventoryDirection = ForgeDirection.getOrientation(o);
    }
}
