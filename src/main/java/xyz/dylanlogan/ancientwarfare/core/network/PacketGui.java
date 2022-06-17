package xyz.dylanlogan.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.core.config.AWLog;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;

import java.io.IOException;

public class PacketGui extends PacketBase {

    private NBTTagCompound packetData;

    public PacketGui() {
        packetData = new NBTTagCompound();
    }

    public void setOpenGui(int id, int x, int y, int z) {
        packetData.setBoolean("openGui", true);
        packetData.setInteger("id", id);
        packetData.setInteger("x", x);
        packetData.setInteger("y", y);
        packetData.setInteger("z", z);
    }

    public void setTag(String key, NBTTagCompound tag) {
        packetData.setTag(key, tag);
    }

    public void setData(NBTTagCompound tag) {
        this.packetData = tag;
    }

    @Override
    protected void writeToStream(ByteBuf data) {
        if (packetData != null) {
            ByteBufOutputStream bbos = new ByteBufOutputStream(data);
            try {
                CompressedStreamTools.writeCompressed(packetData, bbos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void readFromStream(ByteBuf data) {
        try {
            packetData = CompressedStreamTools.readCompressed(new ByteBufInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void execute(EntityPlayer player) {
        if (packetData.hasKey("openGui")) {
            NetworkHandler.INSTANCE.openGui(player, packetData.getInteger("id"), packetData.getInteger("x"), packetData.getInteger("y"), packetData.getInteger("z"));
        } else if (player.openContainer instanceof ContainerBase) {
            ((ContainerBase) player.openContainer).onPacketData(packetData);
        } else {
            AWLog.logError("Invalid target found when processing GUI/Container packet : " + player.openContainer + " packet: " + packetData);
        }
    }

}
