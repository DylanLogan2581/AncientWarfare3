package xyz.dylanlogan.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.core.container.ContainerBase;
import xyz.dylanlogan.ancientwarfare.structure.item.ItemStructureScanner;
import xyz.dylanlogan.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureScanner extends ContainerBase {

    private final ItemStructureSettings settings;

    public ContainerStructureScanner(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack builderItem = player.getHeldItem();
        if (isInvalid(builderItem)) {
            throw new IllegalArgumentException("No scanner in hand");
        }
        settings = ItemStructureSettings.getSettingsFor(builderItem);
        addPlayerSlots();
        removeSlots();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("export")) {
            boolean include = tag.getBoolean("export");
            String name = tag.getString("name");
            NBTTagCompound validation = tag.getCompoundTag("validation");
            if (ItemStructureScanner.scanStructure(player.worldObj, settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, include, validation))
                settings.clearSettings();
        }
        if (tag.hasKey("reset")) {
            settings.clearSettings();
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (par1EntityPlayer.worldObj.isRemote) {
            return;
        }
        ItemStack builderItem = par1EntityPlayer.getHeldItem();
        if (isInvalid(builderItem)) {
            return;
        }
        ItemStructureSettings.setSettingsFor(builderItem, settings);
    }

    private boolean isInvalid(ItemStack stack) {
        return stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemStructureScanner);
    }

    public void export(String name, boolean include, NBTTagCompound validation) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("name", name);
        tag.setBoolean("export", include);
        tag.setTag("validation", validation);
        sendDataToServer(tag);
    }
}
