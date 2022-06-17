package xyz.dylanlogan.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import xyz.dylanlogan.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeSetup extends ContainerNpcBase<NpcFactionTrader> {

    public final FactionTradeList tradeList;
    public boolean tradesChanged = false;

    public ContainerNpcFactionTradeSetup(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.startTrade(player);

        addPlayerSlots();
    }

    @Override
    public void sendInitData() {
        tradeList.updateTradesForView();
        NBTTagCompound tag = new NBTTagCompound();
        tradeList.writeToNBT(tag);

        NBTTagCompound packetTag = new NBTTagCompound();
        packetTag.setTag("tradeData", tag);
        sendDataToClient(packetTag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList.readFromNBT(tag.getCompoundTag("tradeData"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer p_75134_1_) {
        this.entity.closeTrade();
        super.onContainerClosed(p_75134_1_);
    }

    public void onGuiClosed() {
        if (player.worldObj.isRemote && tradesChanged) {
            tradeList.removeEmptyTrades();
            NBTTagCompound tag = new NBTTagCompound();
            tradeList.writeToNBT(tag);
            NBTTagCompound packetTag = new NBTTagCompound();
            packetTag.setTag("tradeData", tag);
            sendDataToServer(packetTag);
        }
    }

}
