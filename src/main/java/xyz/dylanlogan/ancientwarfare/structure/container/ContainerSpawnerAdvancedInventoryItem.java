package xyz.dylanlogan.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.dylanlogan.ancientwarfare.structure.tile.SpawnerSettings;

public class ContainerSpawnerAdvancedInventoryItem extends ContainerSpawnerAdvancedInventoryBase {

    public ContainerSpawnerAdvancedInventoryItem(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

        settings = new SpawnerSettings();
        ItemStack item = player.getHeldItem();
        if (item == null || !item.hasTagCompound() || !item.getTagCompound().hasKey("spawnerSettings")) {
            throw new IllegalArgumentException("stack cannot be null, and must have tag compounds!!");
        }
        settings.readFromNBT(item.getTagCompound().getCompoundTag("spawnerSettings"));
        inventory = settings.getInventory();

        this.addSettingsInventorySlots();
        this.addPlayerSlots(8, 70, 8);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerSettings")) {
            ItemStack item = player.getHeldItem();
            item.setTagInfo("spawnerSettings", tag.getCompoundTag("spawnerSettings"));
        }
    }

}
