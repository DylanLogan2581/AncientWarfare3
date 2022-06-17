package xyz.dylanlogan.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerEntityBase;
import xyz.dylanlogan.ancientwarfare.npc.entity.NpcBase;

public class ContainerNpcBase<T extends NpcBase> extends ContainerEntityBase<T> {

    public ContainerNpcBase(EntityPlayer player, int x) {
        super(player, x);
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return super.canInteractWith(var1) && var1.getDistanceSqToEntity(entity) < 64;
    }

    public void repack() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("repack", true);
        sendDataToServer(tag);
    }

    public void setHome() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("setHome", true);
        sendDataToServer(tag);
    }

    public void clearHome() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("clearHome", true);
        sendDataToServer(tag);
    }
}
